package com.octiagent;

import se.miun.dt175g.octi.core.OctiState;

public class StateWrapper {
    private OctiState state;
    private double eval;

    public StateWrapper(OctiState state, double eval) {
        this.state = state;
        this.eval = eval;
    }

    public OctiState getState() {
        return this.state;
    }

    public void setState(OctiState state) {
        this.state = state;
    }

    public double getEval() {
        return this.eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public void addEval(double eval) {
        this.eval += eval;
    }
}
