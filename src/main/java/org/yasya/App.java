package org.yasya;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.yasya.Game.Position;

public class App {
	public static Random random = new Random();

	public static void main ( String[] args ) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
		Tile.GenerateTiles();
		//Tile.DrawTiles(10, 20);
		//Train();
		testT1();
		//explain();
	}	

	public static void Train() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
		MultiLayerNetwork nnet = loadNetFromFile("T1.data");
		if(nnet == null) {
			nnet = buildRobot();
		}
		Feeder babyFeeder = loadFeederFromFile("babyFeeder.data");
		if(babyFeeder == null) {
			babyFeeder = new Feeder(100);
		}
		Feeder feeder = new Feeder(100);
		for(int i = 0; i < Constants.GAMES_PER_TRAIN; i++) {
			System.out.println("Game No " + i);
			Game game = new Game(nnet);
			Game.Position position = game.getStartPosition();
			int moves = 0;
			int moveNumber = random.nextInt(15);
			String wall = "";
			while(!position.isFinal) {
				//System.out.println("Game No" + i + ", move No " + moves);
				feeder.addInput(position.getInputs());
				MCTS mcts = new MCTS(game, position);
				double[] output = mcts.treeSearch();
				feeder.addOutputP(output);
				if(moves == moveNumber) {
					wall = "vasyl was here";
					babyFeeder.addInput(position.getInputs());
					babyFeeder.addOutputP(output);
				}
				int actionIndex = getRandomIndex(output);
				position = game.getNextPosition(position, Game.allActions[actionIndex]);
				moves++;
			}
			if(wall.equals("vasyl was here")) {
				babyFeeder.addOutputV(new double[] {position.score}, 1);
			}
			feeder.addOutputV(new double[] {position.score}, moves);
			System.out.println(position.toString());
		}
		while(feeder.hasNext()){
			nnet.fit(feeder);
			double error = nnet.score();
			System.out.println("Error: " + error);
		}
		saveNetToFile(nnet, "T1.data");
		saveFeederToFile(babyFeeder, "babyFeeder.data");
	}

	public static int getRandomIndex(double[] p) {
		double r = random.nextDouble();
		int i = 0;
		for(double s = p[0]; s < r; s += p[++i]);
		return i;
	}

	public static int getBestIndex(double[] p) {
		double maxValue = -1;
		int count = 0;
		for(int i = 0; i < p.length; i++) {
			if(p[i] > maxValue) {
				maxValue = p[i];
				count = 1;
			}
			else if(p[i] == maxValue) {
				count++;
			}
		}
		int r = random.nextInt(count);
		count = 0;
		for(int i = 0; i < p.length; i++) {
			if (p[i] == maxValue) {
				if (count == r) {
					return i;
				}
				count++;
			}
		}
		return -1;
	}

	public static MultiLayerNetwork buildRobot() {
		double[] multi = new double[Constants.OUTPUT_SIZE];
		for (int i = 0; i < Constants.OUTPUT_SIZE; i++) {
			if(i == Constants.OUTPUT_SIZE - 1) {
				multi[i] = 150;
			}
			else {
				multi[i] = 1;
			}
		}
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
			.weightInit(WeightInit.XAVIER)
			.updater(new Nesterovs(0.1, 0.9))
			.list()
			.layer( new DenseLayer
				.Builder()
				.nIn(Constants.INPUT_SIZE)
				.nOut(500)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new DenseLayer
				.Builder()
				.nIn(500)
				.nOut(1000)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new DenseLayer
				.Builder()
				.nIn(1000)
				.nOut(2000)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new OutputLayer
				.Builder(LossFunctions.LossFunction.MSE)
				.nIn(2000)
				.nOut(Constants.ACTIONS_COUNT + 1)
				.activation(Activation.IDENTITY)
				.build()
			)
			.validateOutputLayerConfig(false)
			.build();

		//LossFunction weightedLoss = new WeightedLossFunction(new LossMSE(), new double[]{20, 1, 1, 1, 1, 1, 1, 1, 1, 1});
		
		MultiLayerNetwork net = new MultiLayerNetwork(config);
		net.init();

		return net;
	}

	public static void gameTest() throws NoSuchAlgorithmException {
		MultiLayerNetwork nnet = buildRobot();
		Game game = new Game(nnet);
		Position position = game.getStartPosition();
		System.out.println(position.toString());
		position.saveToImg(10, "gif\\position0.png");
		for (int k = 0; k < 10; k++) {
			double[] output = position.calculate();
			int maxIndex = -1;
			double maxValue = -100;
			for(int i = 0; i < output.length; i++) {
				Game.Action a = Game.allActions[i];
				if(position.tiles[a.tileIndex()] > 0) {
					if(output[i] > maxValue) {
						maxValue = output[i];
						maxIndex = i;
					}
				}
			}
			Game.Action action = Game.allActions[maxIndex];
			position = game.getNextPosition(position, action);
			position.saveToImg(10, "gif\\position" + (k + 1) + ".png");
			System.out.println(position.toString());
		}
		GifMaker.make("gif\\", "game.gif", 500);
	}

	public static void saveNetToFile(MultiLayerNetwork net, String fileName) throws IOException {
		File file = new File(fileName);
		ModelSerializer.writeModel(net, file, true);
	}

	public static MultiLayerNetwork loadNetFromFile(String fileName) throws IOException {
		MultiLayerNetwork net = null;
		try {
			File file = new File(fileName);
			net = ModelSerializer.restoreMultiLayerNetwork(file, true);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return net;
	}

	public static void saveFeederToFile(Feeder feeder, String fileName) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(feeder);
		}
	}

	public static Feeder loadFeederFromFile(String fileName) throws IOException, ClassNotFoundException {
		Feeder feeder = null;
		File file = new File(fileName);
		if (!file.exists()){
			return feeder;
		}
		try (FileInputStream fileInputStream = new FileInputStream(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			feeder = (Feeder) objectInputStream.readObject();
		}
		return feeder;
	}

	public static double fightOnce(MultiLayerNetwork net1, MultiLayerNetwork net2) throws NoSuchAlgorithmException {
		Game game1 = new Game(net1);
		Game game2 = new Game(net2);
		Game.Position position1 = game1.getStartPosition();
		Game.Position position2 = game2.setStartPosition(position1);
		while(!position1.isFinal) {
			MCTS mcts = new MCTS(game1, position1);
			double[] output = mcts.treeSearch();
			int actionIndex = getBestIndex(output);
			position1 = game1.getNextPosition(position1, Game.allActions[actionIndex]);
		}
		double score1 = position1.score;

		while(!position2.isFinal) {
			MCTS mcts = new MCTS(game2, position2);
			double[] output = mcts.treeSearch();
			int actionIndex = getBestIndex(output);
			position2 = game2.getNextPosition(position2, Game.allActions[actionIndex]);
		}
		double score2 = position2.score;
		System.out.println("" + score1 + " " + score2);
		return score1 - score2;
	}

	public static void fight(MultiLayerNetwork net1, int count) throws NoSuchAlgorithmException {
		for (int k = 0; k < count; k++) {
			MultiLayerNetwork net2 = buildRobot();
			double delta = fightOnce(net2, net1);
			if(delta > 0) {
				System.out.println("wins first");
			}
			else if(delta == 0) {
				System.out.println("draw");
			}
			else{
				System.out.println("wins second");
			}
		}
	}

	public static void testT1() throws IOException, NoSuchAlgorithmException {
		MultiLayerNetwork net1 = loadNetFromFile("T1.data");
		fight(net1, 20);
	}

	public static void explain() throws IOException, NoSuchAlgorithmException {
		MultiLayerNetwork net1 = loadNetFromFile("T1.data");
		Game game1 = new Game(net1);
		Game.Position position1 = game1.getStartPosition();
		System.out.println(position1.toString());
		while(!position1.isFinal) {
			MCTS mcts = new MCTS(game1, position1);
			double[] output = mcts.treeSearch();
			for(int i = 0; i < Constants.ACTIONS_COUNT; i++) {
				if (output[i] == 0) continue;
				System.out.println("* " + Game.allActions[i].tileIndex() + " " + Game.allActions[i].x() + " " + Game.allActions[i].y() + " " + output[i]);
			}
			int actionIndex = getBestIndex(output);
			System.out.println("chosen: " + Game.allActions[actionIndex].tileIndex() + " " + Game.allActions[actionIndex].x() + " " + Game.allActions[actionIndex].y() + " " + output[actionIndex]);
			position1 = game1.getNextPosition(position1, Game.allActions[actionIndex]);
			System.out.println(position1.toString());
		}
		double score1 = position1.score;
		System.out.println(score1);

	}

}
