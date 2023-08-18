package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TetrisPNG {

	synchronized public static void saveArea(int[][] area, int squareWidth, String fileName, Color[] colors) {
		int imgWidth = squareWidth * 2 + Constants.TETRIS_BOARD_WIDTH * (squareWidth + 1) + 1;
		int imgHeight = squareWidth * 2 + Constants.TETRIS_BOARD_HEIGHT * (squareWidth + 1) + 1;
		
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
				graphics.fillRect(squareWidth + x * (squareWidth + 1), squareWidth + y * (squareWidth + 1), squareWidth + 2, squareWidth + 2);
			}
		}

		graphics.setColor(Color.LIGHT_GRAY);
		for (int x = 0; x < Constants.TETRIS_BOARD_WIDTH - 1; x++) {
			for (int y = 0; y < Constants.TETRIS_BOARD_HEIGHT; y++) {
				if(area[x][y] != area[x + 1][y]) {
					graphics.drawLine(
						squareWidth + (squareWidth + 1) * (x + 1),
						squareWidth + (squareWidth + 1) * y,
						squareWidth + (squareWidth + 1) * (x + 1),
						squareWidth + (squareWidth + 1) * y + squareWidth + 1
					);
				}
			}
		}

		for (int x = 0; x < Constants.TETRIS_BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.TETRIS_BOARD_HEIGHT - 1; y++) {
				if(area[x][y] != area[x][y + 1]) {
					graphics.drawLine(
						squareWidth + (squareWidth + 1) * x,
						squareWidth + (squareWidth + 1) * (y + 1),
						squareWidth + (squareWidth + 1) * x + squareWidth + 1,
						squareWidth + (squareWidth + 1) * (y + 1)
					);
				}
			}
		}

		graphics.drawLine(
			squareWidth,
			squareWidth + 1,
			squareWidth,
			(squareWidth + 1) * (Constants.TETRIS_BOARD_HEIGHT + 1)
		);
		graphics.drawLine(
			(squareWidth + 1) * (Constants.TETRIS_BOARD_WIDTH + 1),
			squareWidth + 1,
			(squareWidth + 1) * (Constants.TETRIS_BOARD_WIDTH + 1),
			(squareWidth + 1) * (Constants.TETRIS_BOARD_HEIGHT + 1)
		);
		graphics.drawLine(
			squareWidth,
			squareWidth,
			(squareWidth + 1) * (Constants.TETRIS_BOARD_WIDTH + 1),
			squareWidth
		);
		graphics.drawLine(
			squareWidth,
			(squareWidth + 1) * (Constants.TETRIS_BOARD_HEIGHT + 1),
			(squareWidth + 1) * (Constants.TETRIS_BOARD_WIDTH + 1),
			(squareWidth + 1) * (Constants.TETRIS_BOARD_HEIGHT + 1)
		);

		try {
			File outputFile = new File(fileName + "_");
			ImageIO.write(image, "png", outputFile);
			Files.move(Paths.get(fileName+"_"), Paths.get(fileName), StandardCopyOption.ATOMIC_MOVE);
		} catch (Exception e) {
			System.out.println("error when saving image: " + e.getMessage());
		}

		graphics.dispose();
	}
}
