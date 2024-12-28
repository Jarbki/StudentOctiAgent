package com.octiagent;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.Agent;
import se.miun.dt175g.octi.core.Node;


public class StudentAgent extends Agent {

	private List<Node<OctiState, OctiAction>> solutionPath = new ArrayList<>();

	// has the current best move stored
	private volatile OctiState currentBestMove;
	
	@Override
	public OctiAction getNextMove(OctiState state) {

		if (currentBestMove == null){
			
		}
		// Example of an agent playing just random moves
		// REMOVE THIS CODE
		// Random rand = new Random();
		// var legalActions = state.getLegalActions();
		// return legalActions.get(rand.nextInt(legalActions.size()));
		
		
		Node<OctiState, OctiAction> root = Node.root(state);
		var gameTree = Agent.generateChildNodes(root);
		
		if (state.isTerminal()){

		}
		// TODO your implementation
	}

	// method that iterates through the tree
	private void searchTree(){

	}

	// method that determines a heuristic for given state
	private void determineHeuristic(){

	}

	// stores a current solution path
	private void storePath(){

	}



	// plan is to see if a solution path can be found, and return a state on that path.
	// when the opponent makes a move, and a new state is given to the method, then a thread will search the stored solution path 
	// to see if the given state is on that path.
	// Another thread will continuosly try to see if a better solution path can be found from the current state of the game,
	// and if one is found, replace the current stored solution path. 
	// this way the algorithm will be able to quickly return a given legal action, even if it might not be the best one.
	// but given time, the actions will improve.


	//objective 1
	// find solution path
	// implement searching algorithm

	// objective 2
	// ensure solution is for my student agent

	// objective 3
	// implement thread to search for possible better solution paths

	// objective 5 
	// develop heuristic function


}
