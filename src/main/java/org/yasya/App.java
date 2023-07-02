package org.yasya;

import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j; 

public class App 
{
	public static void main ( String[] args ) {
		Field field = Field.getRandom();
		field.show();
		field.saveToImg(30);
		field.showStatistic();
		
		Tile.GenerateTiles();
		Tile.DrawTiles(10, 20);

		////netTest();
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
}
