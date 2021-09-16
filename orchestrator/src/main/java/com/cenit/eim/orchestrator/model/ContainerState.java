package com.cenit.eim.orchestrator.model;

public enum ContainerState {
    CREATED(0), RUNNING(1), EXITED(2), UNKNOWN(3);

    int id;

    ContainerState(int id) {
        this.id = id;
    }

    public static ContainerState getContainerStateFromId(int containerStateId) {
        if (containerStateId == 0) {
            return CREATED;
        }
        if (containerStateId == 1) {
            return RUNNING;
        }
        if (containerStateId == 2) {
            return EXITED;
        }
        return UNKNOWN;
    }
}
