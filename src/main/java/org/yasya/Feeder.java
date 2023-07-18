package org.yasya;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class Feeder implements DataSetIterator {
	private int size;
	private int cursor = 0;
	private int batchSize;
	private List<double[]> inputList = new ArrayList<>();
	private List<double[]> outputPList = new ArrayList<>();
	private List<double[]> outputVList = new ArrayList<>();

	public Feeder(int batchSize) {
		this.batchSize = batchSize;
	}

	public void addInput(double[] input) {
		inputList.add(input);
		size = inputList.size();
	}

	public void addOutputP(double[] outputP) {
		outputPList.add(outputP);
	}

	public void addOutputV(double[] outputV, int count) {
		for(int i = 0; i < count; i++) {
			outputVList.add(outputV);
		}
	}

	public void train (MultiLayerNetwork net) {
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
	public DataSet next(int num) {
		if (cursor >= size) {
			throw new NoSuchElementException();
		}

		int from = cursor;
		int to = Math.min(cursor + num, size);
		cursor += num;

		double[][] batchInputs = new double[to - from][Constants.INPUT_SIZE];
		double[][] batchOutputs = new double[to - from][Constants.OUTPUT_SIZE];
		for (int i = 0; i < to - from; i++) {
			batchInputs[i] = inputList.get(from + i);
			batchOutputs[i] =  new double[Constants.OUTPUT_SIZE];
			for(int k = 0; k < Constants.ACTIONS_COUNT; k++) {
				batchOutputs[i][k] = outputPList.get(from + i)[k];
			}
			batchOutputs[i][Constants.OUTPUT_SIZE - 1] = outputVList.get(from + i)[0];
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
		return Constants.INPUT_SIZE;
	}

	@Override
	public DataSet next() {
		return next(batchSize);
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
		return Constants.OUTPUT_SIZE;
	}

}
