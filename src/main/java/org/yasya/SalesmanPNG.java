package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SalesmanPNG {

	synchronized public static BufferedImage getAreaImage(int imgWidth, int imgHeight, double[][] towns, int[] path, int[] secondPath) {
		int min = Math.min(imgWidth, imgHeight);
		int shiftX = (imgWidth - min) / 2 + 20;
		int shiftY = (imgHeight - min) / 2 + 20;
		int squareWidth = min - 40;
		double townMinX = towns[0][0];
		double townMaxX = towns[0][0];
		double townMinY = towns[0][1];
		double townMaxY = towns[0][1];
		for(int i = 0; i < towns.length; i++){
			if(towns[i][0] < townMinX) townMinX = towns[i][0];
			if(towns[i][0] > townMaxX) townMaxX = towns[i][0];
			if(towns[i][1] < townMinY) townMinY = towns[i][1];
			if(towns[i][1] > townMaxY) townMaxY = towns[i][1];
		}

		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		if(secondPath != null){
			graphics.setColor(Color.RED);
			for (int i = 0; i < Salesman.Config.TOWNS_COUNT; i++) {
				int start = secondPath[i];
				int end = secondPath[(i + 1) % Salesman.Config.TOWNS_COUNT];
				graphics.drawLine(
					shiftX + (int)Math.round(squareWidth * ((townMaxX - townMinX) - towns[start][0]) / (townMaxX - townMinX)),
					shiftY + (int)Math.round(squareWidth * ((townMaxY - townMinY) - towns[start][1]) / (townMaxY - townMinY)),
					shiftX + (int)Math.round(squareWidth * ((townMaxX - townMinX) - towns[end][0]) / (townMaxX - townMinX)),
					shiftY + (int)Math.round(squareWidth * ((townMaxY - townMinY) - towns[end][1]) / (townMaxY - townMinY))
				);
			}
		}

		graphics.setColor(Color.GREEN);
		for (int i = 0; i < Salesman.Config.TOWNS_COUNT; i++) {
			int start = path[i];
			int end = path[(i + 1) % Salesman.Config.TOWNS_COUNT];
			graphics.drawLine(
				shiftX + (int)Math.round(squareWidth * ((townMaxX - townMinX) - towns[start][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * ((townMaxY - townMinY) - towns[start][1]) / (townMaxY - townMinY)),
				shiftX + (int)Math.round(squareWidth * ((townMaxX - townMinX) - towns[end][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * ((townMaxY - townMinY) - towns[end][1]) / (townMaxY - townMinY))
			);
		}
		
		graphics.setColor(Color.BLUE);
		for (int i = 0; i < Salesman.Config.TOWNS_COUNT; i++) {
			graphics.drawOval(
				shiftX + (int)Math.round(squareWidth * ((townMaxX - townMinX) - towns[i][0]) / (townMaxX - townMinX)) - 1,
				shiftY + (int)Math.round(squareWidth * ((townMaxY - townMinY) - towns[i][1]) / (townMaxY - townMinY)) - 1,
				3,
				3
			);
		}

		graphics.dispose();

		return image;
	}
}
