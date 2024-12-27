package com.octiagent;


import java.util.Random;
import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.Agent;


public class StudentAgent extends Agent {
	
	@Override
	public OctiAction getNextMove(OctiState state) {

		// Example of an agent playing just random moves
		// REMOVE THIS CODE
		Random rand = new Random();
		var legalActions = state.getLegalActions();
		return legalActions.get(rand.nextInt(legalActions.size()));

		// TODO your implementation
	}

}
