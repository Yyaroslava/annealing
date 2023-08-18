package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SalesmanPNG {

	synchronized public static void saveArea(double[][] towns, int[] path, String fileName) {
		int imgWidth = 340;
		int imgHeight = 340;
		
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		graphics.setColor(Color.GREEN);
		for (int i = 0; i < Constants.SALESMAN_TOWNS_COUNT; i++) {
			graphics.drawLine(
				20 + (int)Math.round(towns[i][0]),
				20 + (int)Math.round(towns[i][1]),
				20 + (int)Math.round(towns[(i + 1) % Constants.SALESMAN_TOWNS_COUNT][0]),
				20 + (int)Math.round(towns[(i + 1) % Constants.SALESMAN_TOWNS_COUNT][1])
			);
		}
		
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
