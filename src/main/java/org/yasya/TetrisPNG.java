package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TetrisPNG {
	synchronized public static void saveArea(int[][] area, int squareWidth, String fileName, Color[] colors) {
		int imgWidth = (Constants.TETRIS_BOARD_WIDTH + 2) * squareWidth;
		int imgHeight = (Constants.TETRIS_BOARD_HEIGHT + 2) * squareWidth;
		
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		for (int x = 0; x < Constants.TETRIS_BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.TETRIS_BOARD_HEIGHT; y++) {
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

		try {
			File outputFile = new File(fileName);
			ImageIO.write(image, "png", outputFile);
		} catch (Exception e) {
			System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
		}

		graphics.dispose();
	}
}
