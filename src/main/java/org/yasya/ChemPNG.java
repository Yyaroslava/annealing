package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ChemPNG {

	synchronized public static BufferedImage getAreaImage(int imgWidth, int imgHeight, int[][] area, Color[] colors) {
		int boardMaxSize = Math.max(Tetris.Config.BOARD_WIDTH, Tetris.Config.BOARD_HEIGHT);
		int min = Math.min(imgWidth, imgHeight);
		int squareWidth = (min - 40) / boardMaxSize - 1; 

		int shiftX = (imgWidth - ((squareWidth + 1) * Tetris.Config.BOARD_WIDTH + 1)) / 2;
		int shiftY = (imgHeight - ((squareWidth + 1) * Tetris.Config.BOARD_HEIGHT + 1)) / 2;
		
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		for (int x = 0; x < Tetris.Config.BOARD_WIDTH; x++) {
			for (int y = 0; y < Tetris.Config.BOARD_HEIGHT; y++) {
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
				graphics.fillRect(
					shiftX + x * (squareWidth + 1),
					shiftY + y * (squareWidth + 1),
					squareWidth + 2,
					squareWidth + 2
				);
			}
		}

		graphics.setColor(Color.GRAY);
		for (int x = 0; x < Tetris.Config.BOARD_WIDTH - 1; x++) {
			for (int y = 0; y < Tetris.Config.BOARD_HEIGHT; y++) {
				if(area[x][y] != area[x + 1][y]) {
					graphics.drawLine(
						shiftX + (squareWidth + 1) * (x + 1),
						shiftY + (squareWidth + 1) * y,
						shiftX + (squareWidth + 1) * (x + 1),
						shiftY + (squareWidth + 1) * y + squareWidth + 1
					);
				}
			}
		}

		for (int x = 0; x < Tetris.Config.BOARD_WIDTH; x++) {
			for (int y = 0; y < Tetris.Config.BOARD_HEIGHT - 1; y++) {
				if(area[x][y] != area[x][y + 1]) {
					graphics.drawLine(
						shiftX + (squareWidth + 1) * x,
						shiftY + (squareWidth + 1) * (y + 1),
						shiftX + (squareWidth + 1) * x + squareWidth + 1,
						shiftY + (squareWidth + 1) * (y + 1)
					);
				}
			}
		}

		graphics.drawLine(
			shiftX,
			shiftY + 1,
			shiftX,
			shiftY + (squareWidth + 1) * Tetris.Config.BOARD_HEIGHT
		);
		graphics.drawLine(
			shiftX + (squareWidth + 1) * Tetris.Config.BOARD_WIDTH,
			shiftY + 1,
			shiftX + (squareWidth + 1) * Tetris.Config.BOARD_WIDTH,
			shiftY + (squareWidth + 1) * Tetris.Config.BOARD_HEIGHT
		);
		graphics.drawLine(
			shiftX,
			shiftY,
			shiftX + (squareWidth + 1) * Tetris.Config.BOARD_WIDTH,
			shiftY
		);
		graphics.drawLine(
			shiftX,
			shiftY + (squareWidth + 1) * Tetris.Config.BOARD_HEIGHT,
			shiftX + (squareWidth + 1) * Tetris.Config.BOARD_WIDTH,
			shiftY + (squareWidth + 1) * Tetris.Config.BOARD_HEIGHT
		);

		graphics.dispose();

		return image;
	}
}

