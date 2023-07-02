package org.yasya;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class Feeder implements DataSetIterator {
	private final int size = 100000;
	private int cursor = 0;
	private final int batchSize = 100;
	public double [][] inputs;
	public double [][] outputs;

	public Feeder () {
		this.inputs = new double [size][3];
		this.outputs = new double [size][5];
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			inputs[i][0] = random.nextDouble();
			inputs[i][1] = random.nextDouble();
			inputs[i][2] = random.nextDouble();

			outputs[i][0] = inputs[i][0] + inputs[i][1] + inputs[i][2];
			outputs[i][1] = inputs[i][0] + inputs[i][1] - inputs[i][2];
			outputs[i][2] = inputs[i][0] - inputs[i][1] - inputs[i][2];
			outputs[i][3] = 10 * inputs[i][0];
			outputs[i][4] = -2 * inputs[i][1];
		}
	}

	public void Train (MultiLayerNetwork net) {
		while (hasNext()) {
			net.fit(this);
			double score = net.score();
			System.out.println("train ended epoch. error: " + score + " cursor: " + cursor + " size: " + size);
		}
	}

	@Override
	public boolean hasNext() {
		return cursor < size;
	}

	@Override
	public DataSet next() {
		if (cursor >= size) {
			throw new NoSuchElementException();
		}

		int from = cursor;
		int to = Math.min(cursor + batchSize, size);
		cursor += batchSize;

		double[][] batchInputs = new double[to - from][3];
		double[][] batchOutputs = new double[to - from][5];
		for (int i = 0; i < to - from; i++) {
			batchInputs[i] = inputs[from + i];
			batchOutputs[i] = outputs[from + i];
		}

		// Преобразование входных данных и меток в INDArray
		org.nd4j.linalg.api.ndarray.INDArray featureArray = Nd4j.create(batchInputs);
		org.nd4j.linalg.api.ndarray.INDArray labelArray = Nd4j.create(batchOutputs);

		System.out.println("cursor: " + cursor);
		return new DataSet(featureArray, labelArray);
	}

	@Override
	public boolean asyncSupported() {
		return false;
	}

	@Override
	public int batch() {
		return batchSize;
	}

	@Override
	public List<String> getLabels() {
		return null;
	}

	@Override
	public DataSetPreProcessor getPreProcessor() {
		return null;
	}

	@Override
	public int inputColumns() {
		return 3;
	}

	@Override
	public DataSet next(int arg0) {
		return null;
	}

	@Override
	public void reset() {}

	@Override
	public boolean resetSupported() {
		return false;
	}

	@Override
	public void setPreProcessor(DataSetPreProcessor arg0) {}

	@Override
	public int totalOutcomes() {
		return 5;
	}

}
