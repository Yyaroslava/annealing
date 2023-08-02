package org.yasya;

import java.io.File;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class AnnealingVideo {
	public static Color[] colors;

	public static void initColors(SolutionAnnealing solution) {
		colors = new Color[solution.tiles.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(100 + App.random.nextInt(50), 100 + App.random.nextInt(50), 100 + App.random.nextInt(50));
		}
	}

	public static void saveToImg(SolutionAnnealing solution, int squareWidth, String fileName) {
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		for(int i = 0; i < solution.tiles.length; i++) {
			Tile tile = solution.tiles[i]; 
			tile.stampColor(area, solution.posX[i], solution.posY[i], i + 1);
		}
		int imgWidth = (Constants.BOARD_WIDTH + 2) * squareWidth;
		int imgHeight = (Constants.BOARD_HEIGHT + 2) * squareWidth;

		// Создание объекта BufferedImage
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		// Получение контекста графики
		Graphics graphics = image.getGraphics();

		// Рисование на изображении
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
				Color c;
				switch (area[x][y]) {
					case -1:
						c = Color.BLACK;
						break;
					case 0:
						c = Color.WHITE;
						break;
					default:
						c = colors[area[x][y] - 1];
				}
				graphics.setColor(c);
				graphics.fillRect(squareWidth * (x + 1), squareWidth * (y + 1), squareWidth, squareWidth);
			}
		}

		// Сохранение изображения в файл
		try {
			File outputFile = new File(fileName);
			ImageIO.write(image, "png", outputFile);
		} catch (Exception e) {
			System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
		}

		// Освобождение ресурсов
		graphics.dispose();
	}

	public static void makeVideo() {
		App.clearDirectory("video/src");
		var witness = new Annealing.FireWitness() {
			private int frameNumber;

			@Override
			public void afterStart(Annealing.MarkovChain s) {
				initColors((SolutionAnnealing)s);
				frameNumber = 0;
			}

			@Override
			public void afterNewSolution(Annealing.MarkovChain s) {
				saveToImg((SolutionAnnealing)s, 20, String.format("video/src/%06d.png", frameNumber++));
			}

			@Override
			public void beforeFinish(Annealing.MarkovChain last, Annealing.MarkovChain best) {
				
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
