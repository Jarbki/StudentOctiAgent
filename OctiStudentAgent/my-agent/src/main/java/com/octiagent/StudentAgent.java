package com.octiagent;

import java.util.List;
import java.util.Random;
import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.Player;
import se.miun.dt175g.octi.core.Pod;
import se.miun.dt175g.octi.core.Point;
import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.OctiBoard;
import se.miun.dt175g.octi.core.Agent;
import se.miun.dt175g.octi.core.Direction;
import se.miun.dt175g.octi.core.Node;
import se.miun.dt175g.octi.core.JumpAction;
import se.miun.dt175g.octi.core.JumpActionElement;

public class StudentAgent extends Agent {

	private int depthLimit = 30;

	@Override
	public OctiAction getNextMove(OctiState state) {

		// Random rand = new Random();
		// Node<OctiState, OctiAction> bestNode =
		// gameTree.get(rand.nextInt(gameTree.size()));
		// double bestEval = evaluateState(bestNode.state, true);

		// int count = 1;
		// for (Node<OctiState, OctiAction> node : gameTree) {
		// System.out.println("Exploring Node: " + count);
		// OctiState currentChild = node.state;
		// double eval = miniMax(depthLimit, currentChild, state.getCurrentPlayer(),
		// Double.NEGATIVE_INFINITY,
		// Double.POSITIVE_INFINITY, count);

		// if (eval > bestEval) {
		// bestEval = eval;
		// bestNode = node;
		// }
		// count++;
		// }

		System.out.println("getNextMoveCalled!");
		StateWrapper eval = miniMax(depthLimit, state, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		System.out.println(eval.getEval());
		Node<OctiState, OctiAction> node = Node.root(eval.getState());

		return node.action;

	}

	private StateWrapper miniMax(int depth, OctiState state, boolean currentPlayer, double alpha, double beta) {

		System.out.println("depth: " + depth);
		Node<OctiState, OctiAction> root = Node.root(state);
		List<Node<OctiState, OctiAction>> gameTree = Agent.generateChildNodes(root);

		double currentEval = evaluateState(state, currentPlayer);
		StateWrapper bestState = new StateWrapper(state, currentEval);

		if (depth == 0 || state.isTerminal()) {
			// return evaluateState(state, currentPlayer);
			System.out.println("returning: depth=" + depth + "terminal=" + state.isTerminal());
			return bestState;
		}

		if (currentPlayer) {
			// we want to maximize the value for current player(this agent)
			double maxEval = Double.NEGATIVE_INFINITY;

			for (Node<OctiState, OctiAction> node : gameTree) {
				OctiState currentState = node.state;
				StateWrapper wrapper = miniMax(depth - 1, currentState, false, alpha, beta);
				double eval = wrapper.getEval();

				if (eval > maxEval) {
					bestState.setState(currentState);
					bestState.addEval(eval);
					maxEval = eval;

				}

				alpha = Math.max(maxEval, alpha);
				if (beta <= alpha) {
					// System.out.println("branch pruned");
					break;
				}
			}
			System.out.println("returning max");
			return bestState;

		} else {
			// we want to minimize the value for current player(other agent)
			double minEval = Double.POSITIVE_INFINITY;

			for (Node<OctiState, OctiAction> node : gameTree) {
				OctiState currentState = node.state;
				StateWrapper wrapper = miniMax(depth - 1, currentState, true, alpha, beta);
				double eval = wrapper.getEval();

				if (eval < minEval) {
					bestState.setState(currentState);
					bestState.addEval(eval);
					minEval = eval;

				}

				beta = Math.min(minEval, beta);
				if (beta <= alpha) {
					// System.out.println("branch pruned");
					break;
				}
			}
			System.out.println("returning min");
			return bestState;
		}

	}

	// method that determines a heuristic value for given state
	private double evaluateState(OctiState state, boolean current) {

		OctiBoard board = state.getBoard();

		Point[] redBase = state.getRedBase();
		Point[] blackBase = state.getBlackBase();

		List<JumpAction> jumpActionsRed = state.getJumpActions(board.getPodsForPlayer("red"));
		List<JumpAction> jumpActionsBlack = state.getJumpActions(board.getPodsForPlayer("black"));

		List<Pod> podsRed = board.getPodsForPlayer("red");
		List<Pod> podsBlack = board.getPodsForPlayer("black");

		double evaluationRed = evaluateJumpActions(jumpActionsRed, state, board)
				+ evaluatePods(podsRed, board, redBase);
		double evaluationBlack = evaluateJumpActions(jumpActionsBlack, state, board)
				+ evaluatePods(podsBlack, board, blackBase);

		if (current) {
			// System.out.println("eval red");
			// System.out.println("eval=" + (evaluationRed - evaluationBlack));
			return evaluationRed - evaluationBlack;

		} else {
			// System.out.println("eval black");
			// System.out.println("eval=" + (evaluationRed - evaluationBlack));
			return evaluationBlack - evaluationRed;
		}

	}

	private double evaluateJumpActions(List<JumpAction> jumps, OctiState state, OctiBoard board) {
		double evaluation = 0;

		for (JumpAction jump : jumps) {
			OctiState jumpState = state.performAction(jump);
			if (jumpState.isTerminal()) {
				evaluation += 5;
			}

			List<JumpActionElement> jumpElements = jump.getJumpActionElements();
			for (JumpActionElement jumpElement : jumpElements) {
				if (jumpElement.isCapturePod()) {
					evaluation++;
				}
			}
		}

		return evaluation;
	}

	private double evaluatePods(List<Pod> pods, OctiBoard board, Point[] bases) {

		double eval = 0;

		for (Pod pod : pods) {
			eval += evaluateProngs(pod) + evaluateDistance(board, pod, bases);
		}

		return eval;
	}

	private double evaluateDistance(OctiBoard board, Pod pod, Point[] bases) {
		double eval = 15;
		Point point = board.getPositionFromPod(pod);
		double shortestDist = Double.POSITIVE_INFINITY;

		for (Point base : bases) {
			int baseX = base.x();
			int baseY = base.y();

			int podX = point.x();
			int podY = point.y();

			double diffY = Math.abs(baseY - podY);
			double diffX = Math.abs(baseX - podX);

			double distance = Math.sqrt((diffY * diffY) + (diffX * diffX));

			if (distance < shortestDist) {
				shortestDist = distance;
			}
		}

		return eval - shortestDist;
	}

	private double evaluateProngs(Pod pod) {
		double eval = 0;
		for (Direction dir : Direction.values()) {
			switch (dir) {

				case FRONT:
					if (pod.hasProng(dir)) {
						eval++;
					}

					break;

				case FRONT_LEFT:
					if (pod.hasProng(dir)) {
						eval++;
					}

					break;

				case FRONT_RIGHT:
					if (pod.hasProng(dir)) {
						eval++;
					}

					break;

				case LEFT:
					if (pod.hasProng(dir)) {
						eval++;
					}

					break;

				case RIGHT:
					if (pod.hasProng(dir)) {
						eval++;
					}

					break;

				default:
					break;
			}
		}
		return eval;
	}

}
