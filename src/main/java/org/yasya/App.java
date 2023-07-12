package org.yasya;

import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.yasya.Game.Position;
import java.io.File;
import java.io.IOException;
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

public class App {
	public static Random random = new Random();

	public static void main ( String[] args ) throws NoSuchAlgorithmException, IOException {
		Tile.GenerateTiles();
		Tile.DrawTiles(10, 20);
		Train();
		
	}	

	public static void Train() throws NoSuchAlgorithmException, IOException {
		MultiLayerNetwork nnet = loadFromFile("T1.data");
		if(nnet == null) {
			nnet = buildRobot();
		}
		Feeder feeder = new Feeder(100);
		for(int i = 0; i < Constants.GAMES_PER_TRAIN; i++) {
			System.out.println("Game No " + i);
			Game game = new Game(nnet);
			Game.Position position = game.getStartPosition();
			int moves = 0;
			while(!position.isFinal) {
				//System.out.println("Game № " + i + ", move № " + moves);
				feeder.addInput(position.getInputs());
				MCTS mcts = new MCTS(game, position);
				double[] output = mcts.treeSearch();
				feeder.addOutputP(output);
				int actionIndex = getRandomIndex(output);
				position = game.getNextPosition(position, Game.allActions[actionIndex]);
				moves++;
			}
			feeder.addOutputV(new double[] {position.score}, moves);
			System.out.println(position.toString());
		}
		while(feeder.hasNext()){
			nnet.fit(feeder);
			double error = nnet.score();
			System.out.println("Error: " + error);
		}
		saveToFile(nnet, "T1.data");
	}

	public static int getRandomIndex(double[] p) {
		double r = random.nextDouble();
		int i = 0;
		for(double s = p[0]; s < r; s += p[++i]);
		return i;
	}

	public static MultiLayerNetwork buildRobot() {
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
				.Builder(LossFunctions.LossFunction.MCXENT)
				.nIn(2000)
				.nOut(Constants.ACTIONS_COUNT + 1)
				.activation(Activation.SIGMOID)
				.build()
			)
			.validateOutputLayerConfig(false)
			.build();
		
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

	public static void saveToFile(MultiLayerNetwork net, String fileName) throws IOException {
		File file = new File(fileName);
		ModelSerializer.writeModel(net, file, true);
	}

	public static MultiLayerNetwork loadFromFile(String fileName) throws IOException {
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

}
