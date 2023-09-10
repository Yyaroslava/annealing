package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ChemPNG {

	synchronized public static BufferedImage getAreaImage(int imgWidth, int imgHeight, Chem.Atom[] atoms, int[][] links, double[][] coordinates) {
		int boardMaxSize = Math.max(Tetris.Config.BOARD_WIDTH, Tetris.Config.BOARD_HEIGHT);
		int min = Math.min(imgWidth, imgHeight);
		
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		graphics.dispose();

		return image;
	}
}

