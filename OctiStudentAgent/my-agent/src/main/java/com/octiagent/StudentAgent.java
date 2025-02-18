package com.octiagent;

import java.util.List;
import java.util.Random;

import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.Pod;
import se.miun.dt175g.octi.core.Point;
import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.OctiBoard;
import se.miun.dt175g.octi.core.Agent;
import se.miun.dt175g.octi.core.JumpAction;
import se.miun.dt175g.octi.core.JumpActionElement;
import se.miun.dt175g.octi.core.Node;

public class StudentAgent extends Agent {

	private int depthLimit = 15;

	private boolean firstGame = true;
	private int returnTime = 20;

	private static final int[][] BOARD_VALUES_RED = {
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 100, 200, 200, 100, 0 },
			{ 0, 150, 250, 250, 150, 0 },
			{ 0, 250, 500, 500, 250, 0 },
			{ 0, 300, 650, 650, 300, 0 },
			{ 0, 2000, 2000, 2000, 2000, 0 },
			{ 600, 700, 800, 800, 700, 600 }
	};

	// The bord for black player
	private static final int[][] BOARD_VALUES_BLACK = {
			{ 600, 700, 800, 800, 700, 600 },
			{ 0, 2000, 2000, 2000, 2000, 0 },
			{ 0, 300, 650, 650, 300, 0 },
			{ 0, 250, 500, 500, 250, 0 },
			{ 0, 150, 250, 250, 150, 0 },
			{ 0, 100, 200, 200, 100, 0 },
			{ 0, 0, 0, 0, 0, 0 }
	};

	@Override
	public OctiAction getNextMove(OctiState state) {

		Node<OctiState, OctiAction> node = Node.root(state);

		// calculate the return time and depth limit based on time
		if (firstGame) {
			returnTime = (int) this.timeLimit / 5;
			if (this.timeLimit > 200) {
				depthLimit += ((int) this.timeLimit) / 100;
			}
		}

		long deadLine = System.currentTimeMillis() + this.timeLimit - returnTime;

		EvalAction bestAction = MiniMax(node, depthLimit, deadLine);

		return bestAction.action;
	}

	private EvalAction MiniMax(Node<OctiState, OctiAction> node, int depth, long deadLine) {
		return Max(node, depth, deadLine, Integer.MIN_VALUE, Integer.MAX_VALUE);

	}

	// choosing for the maximizing player
	private EvalAction Max(Node<OctiState, OctiAction> node, int depth, long deadLine, int alpha, int beta) {
		if (depth <= 0 || System.currentTimeMillis() >= deadLine) {
			int eval = evaluateState(node.state, depth + 1);
			return new EvalAction(eval, null);
		}

		if (node.state.isTerminal()) {
			int eval = evaluateState(node.state, depth + 1);
			return new EvalAction(eval, null);
		}

		// iterate through the child nodes
		int maxEval = Integer.MIN_VALUE;
		EvalAction bestAction = null;
		var childNodes = Agent.generateChildNodes(node);

		for (Node<OctiState, OctiAction> child : childNodes) {
			EvalAction evalAction = Min(child, depth - 1, deadLine, alpha, beta);

			alpha = Math.max(alpha, evalAction.eval);

			if (evalAction.eval > maxEval) {
				maxEval = evalAction.eval;
				bestAction = new EvalAction(maxEval, child.action);
			}

			// pruning
			if (alpha >= beta) {
				break;
			}

		}

		return bestAction;
	}

	// choosing for the minimizing player
	private EvalAction Min(Node<OctiState, OctiAction> node, int depth, long deadLine, int alpha, int beta) {
		if (depth <= 0 || System.currentTimeMillis() >= deadLine) {
			int eval = evaluateState(node.state, depth + 1);
			return new EvalAction(eval, null);
		}

		if (node.state.isTerminal()) {
			int eval = evaluateState(node.state, depth + 1);
			return new EvalAction(eval, null);
		}

		// iterate through the child nodes
		int minEval = Integer.MAX_VALUE;
		EvalAction bestAction = null;
		var childNodes = Agent.generateChildNodes(node);

		for (Node<OctiState, OctiAction> child : childNodes) {
			EvalAction evalAction = Max(child, depth - 1, deadLine, alpha, beta);

			beta = Math.min(beta, evalAction.eval);

			if (evalAction.eval < minEval) {
				minEval = evalAction.eval;
				bestAction = new EvalAction(minEval, child.action);
			}

			// pruning
			if (beta <= alpha) {
				break;
			}

		}

		return bestAction;

	}

	// heuristic methods
	private int evaluateState(OctiState state, int depth) {
		int score = 0;

		// Get pods for both players
		List<Pod> myAgentPods = state.getBoard().getPodsForPlayer(player.getColor());
		List<Pod> opponentsPods = state.getBoard().getPodsForPlayer(oppPlayer.getColor());

		Point[] myBases = player.getColor() == "red" ? state.getRedBase() : state.getBlackBase();
		Point[] opponentBases = player.getColor() == "red" ? state.getBlackBase() : state.getRedBase();

		// Evaluate the distance between the pods and the bases
		score += distance(myAgentPods, state.getBoard(), opponentBases);
		score -= distance(opponentsPods, state.getBoard(), myBases);

		// Set score based on pods left
		score += 4000 / (myAgentPods.size() + 1);
		score -= 4000 / (opponentsPods.size() + 1);

		// Evaluate the board
		score += boardScore(myAgentPods, state, player.getColor() == "red" ? BOARD_VALUES_RED : BOARD_VALUES_BLACK);
		score -= boardScore(opponentsPods, state, player.getColor() == "red" ? BOARD_VALUES_BLACK : BOARD_VALUES_RED);

		// Evaluate the jump actions
		if (state.getCurrentPlayer().getPlayerId() == player.getPlayerId()) {
			score += jumpActions(state.getJumpActions(myAgentPods), state);
		} else {
			score -= jumpActions(state.getJumpActions(opponentsPods), state);
		}

		if (state.isTerminal()) {

			if (state.getWinner() == this.player.getPlayerId()) {
				score += 10000;
			} else {
				score -= 10000;
			}
		}
		int depthScore = (int) (depthLimit - depth) + 1;
		return ((int) score / depthScore);
	}

	private int distance(List<Pod> pods, OctiBoard board, Point[] bases) {

		int eval = 6000;

		for (Pod pod : pods) {
			int shortestDist = Integer.MAX_VALUE;
			Point point = board.getPositionFromPod(pod);

			for (Point base : bases) {
				int baseX = base.x();
				int baseY = base.y();

				int podX = point.x();
				int podY = point.y();

				double diffY = Math.abs(baseY - podY);
				double diffX = Math.abs(baseX - podX);

				int distance = (int) Math.sqrt((diffY * diffY) + (diffX * diffX));

				if (distance < shortestDist) {
					shortestDist = distance;
				}
			}

			eval = (int) (eval / (shortestDist + 1));

		}

		return eval;
	}

	private int jumpActions(List<JumpAction> actions, OctiState state) {
		int eval = 0;

		for (JumpAction action : actions) {

			List<JumpActionElement> elements = action.getJumpActionElements();
			for (JumpActionElement element : elements) {
				if (element.isCapturePod()) {
					eval += 500;
				}

			}

			eval += 100 * elements.size();
		}

		return eval;
	}

	private int boardScore(List<Pod> pods, OctiState state, int[][] boardValues) {
		int eval = 0;

		for (Pod pod : pods) {
			Point point = state.getBoard().getPositionFromPod(pod);

			eval += boardValues[point.y()][point.x()];

		}
		return eval;

	}
}
