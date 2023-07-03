package org.yasya;

import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.yasya.Game.Position;

import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j; 

public class App {
	public static void main ( String[] args ) throws NoSuchAlgorithmException {
		Field field = Field.getRandom();
		field.show();
		field.saveToImg(30);
		field.showStatistic();
		
		Tile.GenerateTiles();
		Tile.DrawTiles(10, 20);

		//netTest();

		gameTest();
	}	

	public static void netTest () {
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
			.weightInit(WeightInit.XAVIER)
			.updater(new Nesterovs(0.1, 0.9))
			.list()
			.layer( new DenseLayer
				.Builder()
				.nIn(3)
				.nOut(10)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new OutputLayer
				.Builder(LossFunctions.LossFunction.L2)
				.nIn(10)
				.nOut(5)
				.activation(Activation.IDENTITY)
				.build()
			)
			.build();
		
		MultiLayerNetwork net = new MultiLayerNetwork(config);
		net.init();

		Feeder feeder = new Feeder();
		feeder.Train(net);

		double [] input = new double [] {0, 1, 2};
		INDArray inputNet = Nd4j.create(input, new int[] {1, input.length});
		INDArray outputNet = net.output(inputNet);
		double [] output = outputNet.toDoubleVector();
		for (int i = 0; i < output.length; i++) {
			System.out.print("" + output[i] + " ");
		}
	}

	public static MultiLayerNetwork buildRobot() {
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
			.weightInit(WeightInit.XAVIER)
			.updater(new Nesterovs(0.1, 0.9))
			.list()
			.layer( new DenseLayer
				.Builder()
				.nIn(Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT + Constants.TILES_COUNT)
				.nOut(1000)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new DenseLayer
				.Builder()
				.nIn(1000)
				.nOut(5000)
				.activation(Activation.SIGMOID)
				.build()
			)
			.layer( new OutputLayer
				.Builder(LossFunctions.LossFunction.L2)
				.nIn(5000)
				.nOut(Constants.ACTIONS_COUNT)
				.activation(Activation.IDENTITY)
				.build()
			)
			.build();
		
		MultiLayerNetwork net = new MultiLayerNetwork(config);
		net.init();

		return net;

		/*
		Feeder feeder = new Feeder();
		feeder.Train(net);

		double [] input = new double [] {0, 1, 2};
		INDArray inputNet = Nd4j.create(input, new int[] {1, input.length});
		INDArray outputNet = net.output(inputNet);
		double [] output = outputNet.toDoubleVector();
		for (int i = 0; i < output.length; i++) {
			System.out.print("" + output[i] + " ");
		}
		*/
	}

	public static void gameTest () throws NoSuchAlgorithmException {
		Game game = new Game();
		Position position = game.getStartPosition();
		position.saveToImg(10, "position.png");
		MultiLayerNetwork net = buildRobot();
		for (int k = 0; k < 10; k++) {
			double[] output = position.calculate(net);
			int maxIndex = IntStream.range(0, output.length)
				.reduce((i, j) -> output[i] > output[j] ? i : j)
				.orElse(-1);
			Game.Action action = Game.allActions[maxIndex];
			position = game.getNextPosition(position, action);
			position.saveToImg(10, "position" + (k + 1) + ".png");
		}
	}
}
