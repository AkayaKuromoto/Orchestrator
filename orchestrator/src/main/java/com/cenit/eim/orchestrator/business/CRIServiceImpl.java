package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.model.ContainerDef;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.springframework.stereotype.Service;
import runtime.v1alpha2.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CRIServiceImpl implements CRIService {

    private final ManagedChannel channel = NettyChannelBuilder.forAddress(new DomainSocketAddress("/run/containerd/containerd.sock"))
            .eventLoopGroup(new EpollEventLoopGroup()).channelType(EpollDomainSocketChannel.class).usePlaintext().build();

    private final RuntimeServiceGrpc.RuntimeServiceBlockingStub runtimeServiceStub = RuntimeServiceGrpc.newBlockingStub(channel);

    private final ImageServiceGrpc.ImageServiceBlockingStub imageServiceStub = ImageServiceGrpc.newBlockingStub(channel);

    @Override
    public ListPodSandboxResponse getAllPods() {
        PodSandboxFilter podSandboxFilter = PodSandboxFilter.newBuilder().putLabelSelector("podManagedBy", "orchestrator").build();
        ListPodSandboxRequest listPodSandboxRequest = ListPodSandboxRequest.newBuilder().setFilter(podSandboxFilter).build();

        return runtimeServiceStub.listPodSandbox(listPodSandboxRequest);
    }

    @Override
    public RunPodSandboxResponse createPod(PodDef podDef, String uuid, int attempt) {
        return createPodSandbox(podDef.getName(), uuid, podDef.getNamespace(), podDef.getId(), attempt);
    }

    @Override
    public Map<ContainerDef, CreateContainerResponse> createContainersInPod(PodDef podDef, String podId, String uuid, int attempt) {
        Map<ContainerDef, CreateContainerResponse> responses = new HashMap<>();
        for (ContainerDef containerDef : podDef.getContainers()) {
            CreateContainerResponse createContainerResponse = createContainer(podDef.getName(), uuid, podDef.getNamespace(), podDef.getId(), containerDef.getId(), containerDef.getContainerName(), containerDef.getImage(), podId, attempt);
            responses.put(containerDef, createContainerResponse);
            startContainer(createContainerResponse.getContainerId());
        }

        return responses;
    }

    private void startContainer(String containerId) {
        StartContainerRequest startContainerRequest = StartContainerRequest.newBuilder().setContainerId(containerId).build();
        runtimeServiceStub.startContainer(startContainerRequest);
    }

    private CreateContainerResponse createContainer(String podName, String podUid, String podNamespace, Long podDefId, Long containerDefId, String containerName, String image, String podId, int attempt) {
        PodSandboxConfig podSandboxConfig = createPodSandboxConfig(podName, podUid, podNamespace, podDefId, attempt);
        ContainerConfig config = ContainerConfig.newBuilder()
                .setMetadata(ContainerMetadata.newBuilder().setName(containerName).setAttempt(attempt).build())
                .setImage(ImageSpec.newBuilder().setImage(image).build()).build();
        CreateContainerRequest containerCreateRequest = CreateContainerRequest.newBuilder().setConfig(config).setPodSandboxId(podId).setSandboxConfig(podSandboxConfig).build();

        return runtimeServiceStub.createContainer(containerCreateRequest);
    }

    private RunPodSandboxResponse createPodSandbox(String podName, String podUid, String podNamespace, Long podDefId, int attempt) {
        PodSandboxConfig podSandboxConfig = createPodSandboxConfig(podName, podUid, podNamespace, podDefId, attempt);
        RunPodSandboxRequest podSandboxRequest = RunPodSandboxRequest.newBuilder().setConfig(podSandboxConfig).build();

        return runtimeServiceStub.runPodSandbox(podSandboxRequest);
    }

    private PodSandboxConfig createPodSandboxConfig(String podName, String podUid, String podNamespace, Long podDefId, int attempt) {
        PodSandboxMetadata.Builder podSandboxMetadata = PodSandboxMetadata.newBuilder().setName(podName).setUid(podUid).setNamespace(podNamespace).setAttempt(attempt);

        return PodSandboxConfig.newBuilder().setMetadata(podSandboxMetadata).putLabels("podManagedBy", "orchestrator").build();
    }

    @Override
    public void deletePodById(String podId, long timeout) {
        PodSandbox podSandbox = getPodById(podId);
        List<Container> containerList = getAllContainersFromPod(podSandbox).getContainersList();
        for (Container container : containerList) {
            stopContainer(container.getId(), timeout);
            removeContainer(container.getId());
        }
        stopPodSandbox(podId);
        removePodSandbox(podId);
    }

    private void removePodSandbox(String podId) {
        RemovePodSandboxRequest removePodSandboxRequest = RemovePodSandboxRequest.newBuilder().setPodSandboxId(podId).build();
        runtimeServiceStub.removePodSandbox(removePodSandboxRequest);
    }

    private void stopPodSandbox(String podId) {
        StopPodSandboxRequest stopPodSandboxRequest = StopPodSandboxRequest.newBuilder().setPodSandboxId(podId).build();
        runtimeServiceStub.stopPodSandbox(stopPodSandboxRequest);
    }

    private void removeContainer(String containerId) {
        RemoveContainerRequest removeContainerRequest = RemoveContainerRequest.newBuilder().setContainerId(containerId).build();
        runtimeServiceStub.removeContainer(removeContainerRequest);
    }

    private void stopContainer(String containerId, long timeout) {
        StopContainerRequest stopContainerRequest = StopContainerRequest.newBuilder().setContainerId(containerId).setTimeout(timeout).build();
        runtimeServiceStub.stopContainer(stopContainerRequest);
    }

    private ListContainersResponse getAllContainersFromPod(PodSandbox pod) {
        ContainerFilter containerFilter = ContainerFilter.newBuilder().setPodSandboxId(pod.getId()).build();
        ListContainersRequest listContainersRequest = ListContainersRequest.newBuilder().setFilter(containerFilter).build();

        return runtimeServiceStub.listContainers(listContainersRequest);
    }

    private PodSandbox getPodById(String podId) {
        for (PodSandbox pod : getAllPods().getItemsList()) {
            if (pod.getId().equals(podId)) {
                return pod;
            }
        }

        return null;
    }

    @Override
    public ListImagesResponse listImages() {
        ImageSpec.Builder imageSpec = ImageSpec.newBuilder();
        ImageFilter.Builder imageFilter = ImageFilter.newBuilder().setImage(imageSpec.build());
        ListImagesRequest.Builder listImagesRequest = ListImagesRequest.newBuilder().setFilter(imageFilter.build());

        return imageServiceStub.listImages(listImagesRequest.build());
    }

    @Override
    public ImageStatusResponse getImageStatus(String imageId, boolean verbose) {
        ImageSpec.Builder imageSpec = ImageSpec.newBuilder().setImage(imageId);
        ImageStatusRequest.Builder imageStatusRequest = ImageStatusRequest.newBuilder().setImage(imageSpec).setVerbose(verbose);

        return imageServiceStub.imageStatus(imageStatusRequest.build());
    }

    /* maybe return object not only string i need*/
    @Override
    public String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException {
        ImageSpec.Builder imageSpec = ImageSpec.newBuilder().setImage(imageId);
        PullImageRequest.Builder pullImageRequest = PullImageRequest.newBuilder().setImage(imageSpec);
        PullImageResponse pullImageResponse;
        try {
            pullImageResponse = imageServiceStub
                    .pullImage(pullImageRequest.build());
        } catch (StatusRuntimeException e) {
            throw new ImageNotFoundException();
        }

        return pullImageResponse.getImageRef();
    }

    @Override
    public String removeImage(String imageId) {
        ImageSpec.Builder imageSpec = ImageSpec.newBuilder().setImage(imageId);
        RemoveImageRequest.Builder removeImageRequest = RemoveImageRequest.newBuilder().setImage(imageSpec);
        imageServiceStub.removeImage(removeImageRequest.build());

        return imageId;
    }
}
