package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SalesmanPNG {

	public record Road(int start, int end, double distance){};

	public class RoadComparator implements Comparator<Road> {
		@Override
		public int compare(Road o1, Road o2) {
			return -Double.compare(o2.distance, o1.distance);
		}
	}

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
			for (int i = 0; i <= Salesman.Config.TOWNS_COUNT; i++) {
				int start = secondPath[i % Salesman.Config.TOWNS_COUNT];
				int end = secondPath[(i + 1) % Salesman.Config.TOWNS_COUNT];
				graphics.drawLine(
					shiftX + (int)Math.round(squareWidth * (townMaxX - towns[start][0]) / (townMaxX - townMinX)),
					shiftY + (int)Math.round(squareWidth * (townMaxY - towns[start][1]) / (townMaxY - townMinY)),
					shiftX + (int)Math.round(squareWidth * (townMaxX - towns[end][0]) / (townMaxX - townMinX)),
					shiftY + (int)Math.round(squareWidth * (townMaxY - towns[end][1]) / (townMaxY - townMinY))
				);
			}
		}

		graphics.setColor(Color.GREEN);
		for (int i = 0; i <= Salesman.Config.TOWNS_COUNT; i++) {
			int start = path[i % Salesman.Config.TOWNS_COUNT];
			int end = path[(i + 1) % Salesman.Config.TOWNS_COUNT];
			graphics.drawLine(
				shiftX + (int)Math.round(squareWidth * (townMaxX - towns[start][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * (townMaxY - towns[start][1]) / (townMaxY - townMinY)),
				shiftX + (int)Math.round(squareWidth * (townMaxX - towns[end][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * (townMaxY - towns[end][1]) / (townMaxY - townMinY))
			);
		}

		PriorityQueue<Road> maxHeap = new PriorityQueue<>(Salesman.Config.LONGEST_COUNT, new SalesmanPNG().new RoadComparator());
		for (int i = 0; i <= Salesman.Config.TOWNS_COUNT; i++) {
			int start = path[i % Salesman.Config.TOWNS_COUNT];
			int end = path[(i + 1) % Salesman.Config.TOWNS_COUNT];
			double distance = Utils.distance(towns[start][0], towns[start][1], towns[end][0], towns[end][1]);
			maxHeap.offer(new Road(start, end, distance));
			if (maxHeap.size() > Salesman.Config.LONGEST_COUNT) {
				maxHeap.poll();
			}
		}
		graphics.setColor(Color.YELLOW);
		while (!maxHeap.isEmpty()) {
			Road road = maxHeap.poll();
			graphics.drawLine(
				shiftX + (int)Math.round(squareWidth * (townMaxX - towns[road.start][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * (townMaxY - towns[road.start][1]) / (townMaxY - townMinY)),
				shiftX + (int)Math.round(squareWidth * (townMaxX - towns[road.end][0]) / (townMaxX - townMinX)),
				shiftY + (int)Math.round(squareWidth * (townMaxY - towns[road.end][1]) / (townMaxY - townMinY))
			);
		}
		
		graphics.setColor(Color.BLUE);
		for (int i = 0; i < Salesman.Config.TOWNS_COUNT; i++) {
			graphics.drawOval(
				shiftX + (int)Math.round(squareWidth * (townMaxX - towns[i][0]) / (townMaxX - townMinX)) - 1,
				shiftY + (int)Math.round(squareWidth * (townMaxY - towns[i][1]) / (townMaxY - townMinY)) - 1,
				3,
				3
			);
		}

		graphics.dispose();

		return image;
	}
}
