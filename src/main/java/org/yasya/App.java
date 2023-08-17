package org.yasya;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
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

	public static void main ( String[] args ) throws NoSuchAlgorithmException, IOException, ClassNotFoundException, InterruptedException {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
		Tile.init();
		Game.generateActions();
		
		if(args.length > 0) {
			switch (args[0]) {
				case "play":
					play("T1.data", null);
					break;
				case "trainAll":
					trainAll();
					break;
				case "trainBaby":
					tryBaby();
					break;
				case "testT1":
					testT1();
					break;
				case "explain":
					explain();
					break;
				case "drawTiles":
					Tile.drawTiles(10, 20);
					break;
				case "thread":
					thread();
					break;
				case "video":
					video();
					break;
				case "algorythmRandom":
					algorythmRandom();
					break;
				case "algorythmGreedy":
					//Greedy.algorythmGreedy();
					break;
				case "algorythmAnnealing":
					algorythmAnnealing();
					break;
				case "AnnealingVideo":
					//AnnealingVideo.makeVideo();
					break;
				case "UI":
					UI.run();
					break;
				default:
			}
		}
	}	

	public static void algorythmAnnealing() {
		double START_T = 0.5;
		double END_T = 0.5;
		int STEPS = 1;
		System.out.printf("%8s %4s \n", "t", "score");
		for(int i = 0; i <= STEPS; i++) {
			double t = START_T + i * (END_T - START_T) / STEPS;
			double score = 0;
			for(int k = 0; k < 50; k++) {
				score += Annealing.fire(SolutionAnnealing.startSolution(), t, 100000);
			}
			score = score / 50;
			System.out.printf("%8.2f %4.2f \n", t, score);
		}
	}

	

	public static void clearDirectory(String path) {
		File directory = new File(path);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						file.delete();
					}
				}
			}
		}
	}

	public static int intersect(int[][] area, Tile tile, int deltaX, int deltaY) {
		int count = 0;
		for(int y = 0; y < tile.height; y++) {
			for(int x = 0; x < tile.width; x++) {
				if(tile.area[x][y] == 1 && area[x + deltaX][y + deltaY] == 1) {
					count++;
				}
			}
		}
		return count;
	}

	public static void video() {
		AviMaker avi = new AviMaker()
			.setImagesFolder("video/src")
			.setOutputVideoPath("video/greedy.avi");
		avi.create();
	}

	public static void thread() throws InterruptedException {
		YThread thread1 = new YThread("T1.data", "F1.data");
		YThread thread2 = new YThread("T1.data", "F2.data");
		YThread thread3 = new YThread("T1.data", "F3.data");
		thread1.start();
		thread2.start();
		thread3.start();
		Thread.sleep(10);
		System.out.printf("\n");
		try {
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void tryBaby() throws ClassNotFoundException, IOException, NoSuchAlgorithmException {
		MultiLayerNetwork t2 = trainBaby("babyFeeder.data");
		saveNetToFile(t2, "T2.data");
		play("T2.data", null);
		MultiLayerNetwork t1 = loadNetFromFile("T1.data");
		fight(t1, t2, Constants.FIGHTS_PER_COMPETITION);
	}

	public static void play(String netDataFileName, String feederDataFileName) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
		MultiLayerNetwork nnet = loadNetFromFile(netDataFileName);
		if(nnet == null) {
			nnet = buildRobot();
		}
		Feeder feeder = new Feeder(100);
		for(int i = 0; i < Constants.GAMES_PER_PLAY; i++) {
			System.out.println("* * * * * * * * * * * *");
			System.out.println("Game No " + i);
			Game game = new Game(nnet);
			Game.Position position = game.getStartPosition();
			int moves = 0;
			while(!position.isFinal) {
				feeder.addInput(position.getInputs());
				MCTS mcts = new MCTS(game, position);
				double[] output = mcts.treeSearch();
				feeder.addOutputP(output);
				int actionIndex = getBestIndex(output);
				position = game.getNextPosition(position, Game.allActions[actionIndex]);
				moves++;
			}
			feeder.addOutputV(new double[] {position.score}, moves);
			System.out.println(position.toString());
			System.out.println("score: " + position.score);
		}
		if(feederDataFileName != null) {
			saveFeederToFile(feeder, feederDataFileName);
		}
	}

	public static void algorythmRandom() throws NoSuchAlgorithmException {
		double scoreSum = 0;
		for(int i = 0; i < Constants.GAMES_PER_PLAY; i++) {
			Game game = new Game(null);
			Game.Position position = game.getStartPosition();
			while(!position.isFinal) {
				double[] output = new double[Constants.ACTIONS_COUNT];
				for(int k = 0; k < Constants.ACTIONS_COUNT; k++) {
					if(position.tiles[Game.allActions[k].tileIndex()] > 0) {
						output[k] = 1;
					}
					else {
						output[k] = 0;
					}
				}
				int actionIndex = getBestIndex(output);
				position = game.getNextPosition(position, Game.allActions[actionIndex]);
			}
			scoreSum += position.score;
		}
		System.out.printf("average score: %f \n", scoreSum / Constants.GAMES_PER_PLAY);

		double scoreMax = 0;
		Game game0 = new Game(null);
		Game.Position position0 = game0.getStartPosition();
		for(int i = 0; i < Constants.GAMES_PER_PLAY; i++) {
			Game game = new Game(null);
			Game.Position position = game.setStartPosition(position0);
			while(!position.isFinal) {
				double[] output = new double[Constants.ACTIONS_COUNT];
				for(int k = 0; k < Constants.ACTIONS_COUNT; k++) {
					if(position.tiles[Game.allActions[k].tileIndex()] > 0) {
						output[k] = 1;
					}
					else {
						output[k] = 0;
					}
				}
				int actionIndex = getBestIndex(output);
				position = game.getNextPosition(position, Game.allActions[actionIndex]);
			}
			if(scoreMax < position.score) {
				scoreMax = position.score;
			}
		}
		System.out.printf("max score: %f \n", scoreMax);

	}

	public static void train(String netDataFileName, String feederDataFileName) throws ClassNotFoundException, IOException {
		MultiLayerNetwork nnet = loadNetFromFile(netDataFileName);
		if(nnet == null) {
			nnet = buildRobot();
		}
		Feeder feeder = loadFeederFromFile(feederDataFileName);
		while(feeder.hasNext()){
			nnet.fit(feeder);
			double error = nnet.score();
			System.out.println("Error: " + error);
		}
		saveNetToFile(nnet, netDataFileName);

	}

	public static void trainAll() throws ClassNotFoundException, IOException {
		train("T1.data", "F1.data");
		train("T1.data", "F2.data");
		train("T1.data", "F3.data");
	}

	public static MultiLayerNetwork trainBaby(String babyDataFileName) throws ClassNotFoundException, IOException {
		MultiLayerNetwork net = buildRobot();
		Feeder babyFeeder = loadFeederFromFile(babyDataFileName);
		if(babyFeeder == null) {
			return net;
		}
		while(babyFeeder.hasNext()){
			net.fit(babyFeeder);
			double error = net.score();
			System.out.println("Error: " + error);
		}
		return net;
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

	public static void fight(MultiLayerNetwork net1, MultiLayerNetwork net2, int count) throws NoSuchAlgorithmException, IOException {
		int winsFirst = 0;
		int winsSecond = 0;
		int draw = 0;
		for (int k = 0; k < count; k++) {
			double delta = fightOnce(net2, net1);
			if(delta > 0) {
				System.out.println("wins first");
				winsFirst++;
			}
			else if(delta == 0) {
				System.out.println("draw");
				draw++;
			}
			else{
				System.out.println("wins second");
				winsSecond++;
			}
		}
		System.out.println("First wins " + winsFirst + " times, " + "draw " + draw + " times, " + "second wins " + winsSecond + " times.");
		if(winsSecond >= Constants.WINS_FOR_VICTORY) {
			System.out.println("We have a champ from a new generation!!");
			saveNetToFile(net2, "T1.data");
			File file = new File("babyFeeder.data");
			file.delete();
		}
	}

	public static void testT1() throws IOException, NoSuchAlgorithmException {
		MultiLayerNetwork net1 = loadNetFromFile("T1.data");
		MultiLayerNetwork net2 = buildRobot();
		fight(net1, net2, Constants.FIGHTS_PER_COMPETITION);
	}

	public static void explain() throws IOException, NoSuchAlgorithmException {
		MultiLayerNetwork net1 = loadNetFromFile("T1.data");
		Game game1 = new Game(net1);
		Game.Position position1 = game1.getStartPosition();
		int frameNumber = 0;
		for(int n = 0; n < 25; n++) {
			position1.saveToImg(20, String.format("video/src/%04d.png", frameNumber++));
		}
		System.out.println(position1.toString());
		while(!position1.isFinal) {
			MCTS mcts = new MCTS(game1, position1);
			double[] output = mcts.treeSearch();
			for(int i = 0; i < Constants.ACTIONS_COUNT; i++) {
				if (output[i] == 0) continue;
				System.out.printf(
					"chosen: %4d %4d %4d %.5f \n", 
					Game.allActions[i].tileIndex(),
					Game.allActions[i].x(),
					Game.allActions[i].y(),
					output[i]
				);
			}
			int actionIndex = getBestIndex(output);
			System.out.printf(
				"chosen: %4d %4d %4d %.5f \n", 
				Game.allActions[actionIndex].tileIndex(),
				Game.allActions[actionIndex].x(),
				Game.allActions[actionIndex].y(),
				output[actionIndex]
			); 
			position1 = game1.getNextPosition(position1, Game.allActions[actionIndex]);
			System.out.println(position1.toString());
			for(int n = 0; n < 25; n++) {
				position1.saveToImg(20, String.format("video/src/%04d.png", frameNumber++));
			}
		}
		double score1 = position1.score;
		System.out.println(score1);

	}

}
