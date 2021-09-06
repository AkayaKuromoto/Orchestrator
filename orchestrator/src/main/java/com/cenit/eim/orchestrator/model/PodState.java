package com.cenit.eim.orchestrator.model;

public enum PodState {

    READY(0), NOT_READY(1), UNKNOWN(2);

    private int id;

    PodState(int id) {
        this.id = id;
    }

    public static PodState getPodStateFromId(int podStateId){
        if(podStateId == 0){
            return READY;
        }
        if(podStateId == 1){
            return NOT_READY;
        }
        return UNKNOWN;
    }
}
