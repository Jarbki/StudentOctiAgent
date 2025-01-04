package com.octiagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.Player;
import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.OctiBoard;
import se.miun.dt175g.octi.core.Agent;
import se.miun.dt175g.octi.core.Node;
import se.miun.dt175g.octi.core.JumpAction;

public class StudentAgent extends Agent {

	private List<Node<OctiState, OctiAction>> solutionPath = new ArrayList<>();

	// has the current best move stored

	private int depthLimit = 30;

	@Override
	public OctiAction getNextMove(OctiState state) {

		Node<OctiState, OctiAction> root = Node.root(state);
		List<Node<OctiState, OctiAction>> gameTree = Agent.generateChildNodes(root);

		Random rand = new Random();
		Node<OctiState, OctiAction> bestNode = gameTree.get(rand.nextInt(gameTree.size()));
		double bestEval = evaluateState(bestNode.state, bestNode.state.getCurrentPlayer());

		for (Node<OctiState, OctiAction> node : gameTree) {
			OctiState currentChild = node.state;
			double eval = miniMax(depthLimit, currentChild, state.getCurrentPlayer(), Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
			if (eval > bestEval) {
				bestEval = eval;
				bestNode = node;
			}
		}

		return bestNode.action;

	}

	private double miniMax(int depth, OctiState state, Player currentPlayer, double alpha, double beta) {
		Node<OctiState, OctiAction> root = Node.root(state);
		List<Node<OctiState, OctiAction>> gameTree = Agent.generateChildNodes(root);

		if (depth == 0 || state.isTerminal()) {
			return evaluateState(state, currentPlayer);

		} else {

			boolean opponent = currentPlayer.equals(state.getBlackPlayer());

			if (opponent) {
				double minEval = Double.POSITIVE_INFINITY;
				for (Node<OctiState, OctiAction> node : gameTree) {
					OctiState currentState = node.state;
					double eval = miniMax(depth - 1, currentState, currentState.getCurrentPlayer(), alpha, beta);
					minEval = Math.min(eval, minEval);

					double currentBeta = Math.min(minEval, beta);
					if (alpha >= currentBeta) {
						break;
					}
				}
				return minEval;

			} else {
				double maxEval = Double.NEGATIVE_INFINITY;
				for (Node<OctiState, OctiAction> node : gameTree) {
					OctiState currentState = node.state;
					double eval = miniMax(depth - 1, currentState, currentState.getCurrentPlayer(), alpha, beta);
					maxEval = Math.max(eval, maxEval);
					double currentAlpha = Math.max(maxEval, alpha);
					if (beta <= currentAlpha) {
						break;
					}
				}
				return maxEval;
			}
		}
	}

	// method that determines a heuristic value for given state
	private double evaluateState(OctiState state, Player currentPlayer) {
		double counter = 0;
		OctiBoard board = state.getBoard();
		List<JumpAction> redPods = state.getJumpActions(board.getPodsForPlayer(currentPlayer.getColor()));

		// other things to get a good evaluation.
		return counter;
	}

	// stores a current solution path
	private void storePath() {

	}

	// plan is to see if a solution path can be found, and return a state on that
	// path.
	// when the opponent makes a move, and a new state is given to the method, then
	// a thread will search the stored solution path
	// to see if the given state is on that path.
	// Another thread will continuosly try to see if a better solution path can be
	// found from the current state of the game,
	// and if one is found, replace the current stored solution path.
	// this way the algorithm will be able to quickly return a given legal action,
	// even if it might not be the best one.
	// but given time, the actions will improve.

	// objective 1
	// find solution path
	// implement searching algorithm

	// objective 2
	// ensure solution is for my student agent

	// objective 3
	// implement thread to search for possible better solution paths

	// objective 5
	// develop heuristic function

}
