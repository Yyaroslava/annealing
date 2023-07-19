package org.yasya;

import java.security.NoSuchAlgorithmException;

public class MCTS {
	public Game game;
	public Game.Position startPosition;

	MCTS(Game game, Game.Position startPosition) {
		this.game = game;
		this.startPosition = startPosition;
	}

	public double search(Game.Position position) throws NoSuchAlgorithmException {
		Node node = position.node;
		if(node == null) {
			node = new Node(position);
			position.node = node;
			return node.v;
		}
		if(position.isFinal) {
			return position.score;
		}
		for(int a : position.validActions) {
			if(node.Na[a] == 0) {
				Game.Position nextPosition = game.getNextPosition(position, Game.allActions[a]);
				double v = search(nextPosition);
				node.Qa[a] = (node.Na[a] * node.Qa[a] + v) / (node.Na[a] + 1);
				node.Na[a]++;
				node.N++;
				return v;
			}
		}
		double bestReward = -100;
		int bestAction = -1;
		for(int a : position.validActions) {
			double maybeBestReward = node.Qa[a] + Constants.C_PUCT * node.Pa[a] * Math.sqrt(node.N) / (1 + node.Na[a]);
			if(maybeBestReward > bestReward) {
				bestReward = maybeBestReward;
				bestAction = a;
			}
		}
		Game.Position nextPosition = game.getNextPosition(position, Game.allActions[bestAction]);
		double v = search(nextPosition);
		node.Qa[bestAction] = (node.Na[bestAction] * node.Qa[bestAction] + v) / (node.Na[bestAction] + 1);
		node.Na[bestAction]++;
		node.N++;
		return v;
	}

	public double[] treeSearch() throws NoSuchAlgorithmException {
		for(int i = 0; i < Constants.SEARCHES_PER_MOVE; i++) {
			search(startPosition);
		}
		Node node = startPosition.node;
		double[] improvedPolicy = new double[Constants.ACTIONS_COUNT];
		for(int i = 0; i < Constants.ACTIONS_COUNT; i++) {
			improvedPolicy[i] = (double)node.Na[i] / (double)node.N;
		}
		return improvedPolicy;
	}

	public class Node {
		public Game.Position position;
		double[] Qa; // every action 'a' reward from current position
		double[] Pa; // probabilities for every action
		int[] Na; // count of action 'a' from current position
		int N; // number of times this position was visited
		double v; // current position estimation

		Node(Game.Position position) {
			this.position = position;
			Qa = new double[Constants.ACTIONS_COUNT];
			Pa = new double[Constants.ACTIONS_COUNT];
			Na = new int[Constants.ACTIONS_COUNT];
			position.calculateNewNode(this); // fill Pa and v
		}

	}
}
