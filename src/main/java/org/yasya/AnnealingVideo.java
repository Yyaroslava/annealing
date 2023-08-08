package org.yasya;

import java.awt.Color;

public class AnnealingVideo {
	public static Color[] colors;

	public static void initColors(SolutionAnnealing solution) {
		colors = new Color[solution.tiles.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(100 + App.random.nextInt(50), 100 + App.random.nextInt(50), 100 + App.random.nextInt(50));
		}
	}

	public static void saveToImg(SolutionAnnealing solution, int squareWidth, String fileName) {
		int[][] area = solution.coloredArea();
		PNGMaker.make(area, squareWidth, fileName, colors);
	}

	public static void makeVideo() {
		App.clearDirectory("video/src");
		var witness = new Annealing.FireWitness() {
			private int frameNumber;

			@Override
			public boolean checkStop() {
				return false;
			}

			@Override
			public void afterStart(Annealing.MarkovChain s) {
				initColors((SolutionAnnealing)s);
				frameNumber = 0;
			}

			@Override
			public void afterNewSolution(Annealing.MarkovChain s, int bestScore, double t) {
				saveToImg((SolutionAnnealing)s, 20, String.format("video/src/%06d.png", frameNumber++));
			}

			@Override
			public void beforeFinish(Annealing.MarkovChain last, Annealing.MarkovChain best) {
				
			}

			@Override
			public boolean checkJump(Annealing.MarkovChain chain, Annealing.MarkovChain bestChain, int bestScore) {
				return false;
			}	
		};
		SolutionAnnealing s = SolutionAnnealing.startSolution();
		Annealing.fire(s, witness, 0.5, 100000);
		AviMaker avi = new AviMaker()
			.setImagesFolder("video/src")
			.setOutputVideoPath("video/annealing.avi");
		avi.create();
	}
}
