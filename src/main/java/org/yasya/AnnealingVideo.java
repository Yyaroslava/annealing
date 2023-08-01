package org.yasya;

import java.io.File;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class AnnealingVideo {
	public static Color[] colors;

	public static void initColors(Solution solution) {
		colors = new Color[solution.tiles.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(100 + App.random.nextInt(50), 100 + App.random.nextInt(50), 100 + App.random.nextInt(50));
		}
	}

	public static void saveToImg(Solution solution, int squareWidth, String fileName) {
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

	public static int fire() {
		double initialT = 0.5;
		int STEP_COUNT = 50000;
		Solution s = Solution.startSolution();
		initColors(s);
		int score = s.score();
		double t = initialT;
		int bestScore = 9999;
		int frameNumber = 0;
		for(int i = 0; i < STEP_COUNT; i++) {
			Solution newS = s.next();
			int newScore = newS.score();
			if(newScore <= score) {
				score = newScore;
				s = newS;
				if(bestScore > score) {
					bestScore = score;
				}
				saveToImg(s, 20, String.format("video/src/%04d.png", frameNumber++));
			}
			else {
				double p = Math.exp(-(double)(newScore - score) / t);
				if(App.random.nextDouble() < p) {
					score = newScore;
					s = newS;
					saveToImg(s, 20, String.format("video/src/%04d.png", frameNumber++));
				}
			}
			t -= initialT / STEP_COUNT;
		}
		return bestScore;
	}

	public static void makeVideo() {
		App.clearDirectory("video\src");
		fire();
		AviMaker avi = new AviMaker()
			.setImagesFolder("video/src")
			.setOutputVideoPath("video/annealing.avi");
		avi.create();
	}
}
