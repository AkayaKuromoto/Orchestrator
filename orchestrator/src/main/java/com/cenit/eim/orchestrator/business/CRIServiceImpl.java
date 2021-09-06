package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import com.cenit.eim.orchestrator.business.model.ContainerResponse;
import com.cenit.eim.orchestrator.model.Container;
import com.cenit.eim.orchestrator.model.ContainerState;
import com.cenit.eim.orchestrator.model.Pod;
import com.cenit.eim.orchestrator.model.PodState;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.springframework.stereotype.Service;
import runtime.v1alpha2.*;
import org.openapitools.model.ContainerCreateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CRIServiceImpl implements CRIService {

    private final ManagedChannel channel =
            NettyChannelBuilder
                .forAddress(new DomainSocketAddress("/run/containerd/containerd.sock"))
                .eventLoopGroup(new EpollEventLoopGroup())
                .channelType(EpollDomainSocketChannel.class)
                .usePlaintext()
                .build();

    private final RuntimeServiceGrpc.RuntimeServiceBlockingStub runtimeServiceStub =
            RuntimeServiceGrpc.newBlockingStub(channel);

    private final ImageServiceGrpc.ImageServiceBlockingStub imageServiceStub =
            ImageServiceGrpc.newBlockingStub(channel);

    @Override
    public List<Pod> getAllPods() {
        ListPodSandboxRequest listPodSandboxRequest = ListPodSandboxRequest.newBuilder().build().getDefaultInstanceForType();

        ListPodSandboxResponse listPodSandboxResponse = runtimeServiceStub.listPodSandbox(listPodSandboxRequest);

        return listPodSandboxResponse.getItemsList()
                .stream()
                .map(
                        podSandbox -> new Pod(
                                podSandbox.getId(),
                                podSandbox.getMetadata().getName(),
                                podSandbox.getMetadata().getNamespace(),
                                PodState.getPodStateFromId(podSandbox.getStateValue())))
                .collect(Collectors.toList());
    }

    @Override
    public Pod getPod(String podId, boolean verbose) {
        PodSandboxStatusRequest podSandboxStatusRequest =
                PodSandboxStatusRequest.newBuilder()
                        .setPodSandboxId(podId)
                        .setVerbose(verbose)
                        .build();

        PodSandboxStatusResponse podSandboxStatusResponse = runtimeServiceStub.podSandboxStatus(podSandboxStatusRequest);

        return new Pod(
                podSandboxStatusResponse.getStatus().getId(),
                podSandboxStatusResponse.getStatus().getMetadata().getName(),
                podSandboxStatusResponse.getStatus().getMetadata().getNamespace(),
                PodState.getPodStateFromId(podSandboxStatusResponse.getStatus().getStateValue()));
    }

    @Override
    public Pod createPodWithContainers(String podName, String podUid, String podNamespace, List<ContainerCreateRequest> containerRequests, int attempt) {
        Pod pod = createPodSandbox(podName,
                podUid,
                podNamespace,
                attempt);

        List<Container> containerList = new ArrayList<>();

        for(ContainerCreateRequest containerRequest : containerRequests){
            containerList.add(createContainer(pod,
                    podName,
                    podUid,
                    podNamespace,
                    containerRequest.getContainerName(),
                    containerRequest.getImageId(),
                    pod.getPodId(),
                    attempt));
        }

        pod.setContainers(containerList);

        return pod;
    }

    private Container createContainer(Pod pod, String podName, String podUid, String podNamespace, String containerName, String image, String podId, int attempt) {
        PodSandboxConfig.Builder podSandboxConfig = createPodSandboxConfig(podName, podUid, podNamespace, attempt);

        ContainerConfig config = ContainerConfig.newBuilder()
                .setMetadata(
                        ContainerMetadata.newBuilder()
                                .setName(containerName)
                                .setAttempt(attempt)
                                .build()
                )
                .setImage(
                        ImageSpec.newBuilder()
                                .setImage(image)
                                .build()
                )
//                .setCommand()
//                .setArgs()
//                .setWorkingDir("/tmp/aaa")
//                .setEnvs()
//                .setMounts()
//                .setDevices()
//                .putLabels()
//                .putAnnotations()
//                .setLogPath()
//                .setLinux()
                .build();

        CreateContainerRequest containerCreateRequest =
                CreateContainerRequest.newBuilder()
                        .setConfig(config)
                        .setPodSandboxId(podId)
                        .setSandboxConfig(podSandboxConfig)
                        .build();

        CreateContainerResponse createContainerResponse = runtimeServiceStub.createContainer(containerCreateRequest);

        ContainerResponse containerResponse = new ContainerResponse(createContainerResponse.getContainerId(), image);

        return new Container(containerResponse.getContainerId(), ContainerState.CREATED, containerResponse.getContainerImage(), pod);
    }

    private Pod createPodSandbox(String podName, String podUid, String podNamespace, int attempt) {
        PodSandboxConfig.Builder podSandboxConfig = createPodSandboxConfig(podName, podUid, podNamespace, attempt);

        RunPodSandboxRequest podSandboxRequest =
                RunPodSandboxRequest.newBuilder()
                        .setConfig(podSandboxConfig)
//                        .setRuntimeHandler()
                        .build();

        RunPodSandboxResponse podSandboxResponse = runtimeServiceStub.runPodSandbox(podSandboxRequest);


        return new Pod(podSandboxResponse.getPodSandboxId(),
                podName,
                podNamespace,
                PodState.READY);
    }

    private PodSandboxConfig.Builder createPodSandboxConfig(String podName, String podUid, String podNamespace, int attempt){
        PodSandboxMetadata.Builder podSandboxMetadata =
                PodSandboxMetadata.newBuilder()
                        .setName(podName)
                        .setUid(podUid)
                        .setNamespace(podNamespace)
                        .setAttempt(attempt);

        return PodSandboxConfig.newBuilder()
                .setMetadata(podSandboxMetadata);
//            .setHostname()
//            .setLogDirectory()
//            .setDnsConfig()
//            .setPortMappings()
//            .putLabels()
//            .putAnnotations()
//            .setLinux()
//            .setWindows();
    }

    @Override
    public void deletePodById(String podId, long timeout) {
        List<Container> containers = getAllContainersFromPod(getPodById(podId));

        for (Container container : containers){
            stopContainer(container.getContainerId(), timeout);
        }

        removePodSandboxById(podId);
    }

    @Override
    public void deletePodByNamespace(String podNamespace, long timeout) {
        List<Pod> pods = getPodsByNamespace(podNamespace);

        pods.forEach(pod -> deletePodById(pod.getPodId(), timeout));
    }

    private List<Pod> getPodsByNamespace(String podNamespace) {
        return getAllPods().stream().filter(pod -> pod.getPodNamespace().equals(podNamespace)).collect(Collectors.toList());
    }

    private void stopContainer(String containerId, long timeout) {
        StopContainerRequest stopContainerRequest =
                StopContainerRequest.newBuilder()
                        .setContainerId(containerId)
                        .setTimeout(timeout)
                        .build();

        runtimeServiceStub.stopContainer(stopContainerRequest);
    }

    private List<Container> getAllContainersFromPod(Pod pod) {
        ContainerFilter containerFilter =
                ContainerFilter.newBuilder()
                        .setPodSandboxId(pod.getPodId())
                        .build();

        ListContainersRequest listContainersRequest =
                ListContainersRequest.newBuilder()
                        .setFilter(containerFilter)
                        .build();

        ListContainersResponse listContainersResponse = runtimeServiceStub.listContainers(listContainersRequest);

        return listContainersResponse.getContainersList()
                .stream()
                .map(
                        container -> new Container(
                                container.getId(),
                                ContainerState.getContainerStateFromId(container.getStateValue()),
                                container.getImage().getImage(), pod))
                .collect(Collectors.toList());
    }

    private void removePodSandboxById(String podId){
        RemovePodSandboxRequest removePodSandboxRequest =
                RemovePodSandboxRequest.newBuilder()
                        .setPodSandboxId(podId)
                        .build();

        runtimeServiceStub.removePodSandbox(removePodSandboxRequest);
    }

    private Pod getPodById(String podId){
        for(Pod pod : getAllPods()){
            if(pod.getPodId().equals(podId)){
                return pod;
            }
        }

        return null;
    }

    @Override
    public ListImagesResponse listImages() {
        ImageSpec.Builder imageSpec = ImageSpec.newBuilder();

        ImageFilter.Builder imageFilter =
                ImageFilter.newBuilder()
                        .setImage(imageSpec.build());

        ListImagesRequest.Builder listImagesRequest =
                ListImagesRequest.newBuilder()
                        .setFilter(imageFilter.build());

        return imageServiceStub.listImages(listImagesRequest.build());
    }

    @Override
    public ImageStatusResponse getImageStatus(String imageId, Map<String, String> imageAnnotations, boolean verbose) {
        ImageSpec.Builder imageSpec =
                ImageSpec.newBuilder()
                        .setImage(imageId)
                        .putAllAnnotations(imageAnnotations);

        ImageStatusRequest.Builder imageStatusRequest =
                ImageStatusRequest.newBuilder()
                        .setImage(imageSpec)
                        .setVerbose(verbose);

        return imageServiceStub.imageStatus(imageStatusRequest.build());
    }

    @Override
    public String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException {
        ImageSpec.Builder imageSpec =
                ImageSpec.newBuilder()
                    .setImage(imageId);

        PullImageRequest.Builder pullImageRequest =
                PullImageRequest.newBuilder()
                    .setImage(imageSpec);

        PullImageResponse pullImageResponse;

        try{
            pullImageResponse = imageServiceStub
                    .pullImage(pullImageRequest.build());
        } catch (StatusRuntimeException e){
            throw new ImageNotFoundException();
        }

        return pullImageResponse.getImageRef();
    }

    @Override
    public String removeImage(String imageId, Map<String, String> imageAnnotations) {
        ImageSpec.Builder imageSpec =
                ImageSpec.newBuilder()
                        .setImage(imageId)
                        .putAllAnnotations(imageAnnotations);

        RemoveImageRequest.Builder removeImageRequest =
                RemoveImageRequest.newBuilder()
                        .setImage(imageSpec);

        imageServiceStub.removeImage(removeImageRequest.build());
        return imageId;
    }
}
