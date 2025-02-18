package com.octiagent;

import se.miun.dt175g.octi.core.OctiAction;

public class EvalAction {

    public OctiAction action;
    public int eval;

    public EvalAction(int eval, OctiAction action) {
        this.action = action;
        this.eval = eval;

    }

}
