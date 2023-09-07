package org.yasya;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Utils {
	public static Random random = new Random();

	public static JFreeChart updateChart(double[] history) {
		double min = 9999999;
		double max = -1;
		for(int i = 0; i < history.length; i++) {
			if(history[i] == 0) continue;
			if(history[i] < min) min = history[i];
			if(history[i] > max) max = history[i] + 0.0001;
		}
		int[] statistic = new int[UI.Config.CHART_COLUMNS_COUNT];
		for(int i = 0; i < history.length; i++) {
			if(history[i] == 0) continue;
			int index = (int)((history[i] - min) / (max - min) * UI.Config.CHART_COLUMNS_COUNT);
			statistic[index]++;
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < UI.Config.CHART_COLUMNS_COUNT; i++){
			double value = min + ((max - min) / UI.Config.CHART_COLUMNS_COUNT) * (i + 0.5);
			dataset.addValue(statistic[i], "x", String.format("%.1f", value));
		}
		JFreeChart chart = ChartFactory.createBarChart(
			"the distribution of the scores of the last 1 million solutions",
			"score",
			"count",
			dataset,
			PlotOrientation.VERTICAL,
			false,
			true,
			false
		);

		return chart;
	}
	
	public static int[] smash(int sum) {
		int n1 = -1;
		for(int i = (sum + 2); i > 2; i--) {
			if(random.nextInt(i) <= 1) {
				n1 = sum + 2 - i;
				break;
			};
		}
		int n2 = random.nextInt(sum - n1 + 1);
		int n3 = sum - n1 - n2;
		return new int[] {n1, n2, n3};
	}

	public static Color[] getPalette(int size) {
		Color[] colors = new Color[size];
		for(int i = 0; i < colors.length; i++) {
			int index = i % Tetris.Config.PALETTE.length;
			colors[i] = new Color(Tetris.Config.PALETTE[index][0], Tetris.Config.PALETTE[index][1], Tetris.Config.PALETTE[index][2]);
		}
		return colors;
	}

	public static double fire(Chainable chain) {
		double t = UI.currentTemperature;
		ThreadLocalRandom localRandom = ThreadLocalRandom.current();
		chain.afterStart(chain);
		double score = chain.score();
		double bestScore = score + 1;
		Chainable bestChain = null;
		int i = 0;
		do{
			Chainable newChain = chain.next();
			double newScore = newChain.score();
			if(newScore <= score) {
				chain.afterJump(true, newScore);
				score = newScore;
				chain = newChain;
				if(bestScore > score) {
					bestScore = score;
					bestChain = chain;
				}
				chain.afterBetterSolutionFound(chain, bestScore, t);
			}
			else{
				double p = Math.exp(-(double)(newScore - score) / t);
				if(localRandom.nextDouble() < p) {
					chain.afterJump(true, newScore);
					score = newScore;
					chain = newChain;
					chain.afterBetterSolutionFound(chain, bestScore, t);
				}
				else{
					chain.afterJump(false, newScore);
				}
			}
			
			if(i++ % 1000 == 0) {
				Object[] params = new Object[] {false, t};
				chain.handleEvents(params);
				if((boolean)params[0]) break;
				t = (double)params[1];
			}

		}while(true);
		chain.beforeFinish(chain, bestChain);
		return bestScore;
	}

	public static int[] randomPermutation(int length) {
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
		}
		for (int i = length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			int temp = permutation[i];
			permutation[i] = permutation[j];
			permutation[j] = temp;
		}

		return permutation;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	} 
}
