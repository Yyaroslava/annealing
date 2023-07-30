package org.yasya;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Tile {
	public static Tile[] allTiles;
	public static Map<String, Tile> allTilesMap;
	public static Map<String, Integer> tileIndexMap;
	public int size;
	public int width;
	public int height;
	public int[][] area;

	public static String toString(int[][] tileArea, int width, int height) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sb.append(tileArea[x][y]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(width);
		sb.append("|");
		sb.append(height);
		sb.append("|");
		sb.append(size);
		sb.append("|");
		for ( int x = 0; x < width; x++ ) {
			for ( int y = 0; y < height; y++ ) {
				sb.append(area[x][y]);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static String getTileCode(int tileWidth, int tileHeight, int[][] tileArea) {
		StringBuilder sb = new StringBuilder();
		sb.append(tileWidth);
		sb.append(tileHeight);
		for (int x = 0; x < tileWidth; x++) {
			for (int y = 0; y < tileHeight ; y++) {
				sb.append(tileArea[x][y]);
			}
		}
		return sb.toString();
	}

	public Tile copy() {
		Tile newTile = new Tile(this.size, this.width, this.height);
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height ; y++) {
				newTile.area[x][y] = this.area[x][y];
			}
		}
		return newTile;
	}

	public static boolean contains (ArrayList<Tile> tiles, Tile tile) {
		for (Tile testTile : tiles) {
			if (testTile.equals(tile)) {
				return true;
			}
		}
		return false;
	}

	public Tile (int size, int width, int height) {
		this.size = size;
		this.width = width;
		this.height = height;
		this.area = new int [width][height];
	};

	public Tile(int width, int height, int size, int[][] area) {
		this.width = width;
		this.height = height;
		this.size = size;
		this.area = Arrays.stream(area)
			.map(obj -> Arrays.copyOf((int[])obj, ((int[]) obj).length))
			.toArray(int[][]::new);
	}

	public Tile(int[][] area, int deltaX, int deltaY, int width, int height) {
		this.width = width;
		this.height = height;
		int size = 0;
		this.area = new int[width][height];
		for ( int x = 0; x < width; x++ ) {
			for ( int y = 0; y < height; y++ ) {
				this.area[x][y] = area[x+deltaX][y+deltaY];
				if (this.area[x][y] == 1) size++;
			}
		}
		this.size = size;
	}

	public void stamp(int[][] area, int deltaX, int deltaY) {
		for ( int x = 0; x < this.width; x++ ) {
			for ( int y = 0; y < this.height; y++ ) {
				area[x+deltaX][y+deltaY] = Math.max(area[x+deltaX][y+deltaY], this.area[x][y]);
			}
		}
	}

	public static void init() {
		allTilesMap = new HashMap<>();
		List<Tile> allTilesList = new LinkedList<>();
		Queue<Tile> queue = new LinkedList<>();
		queue.offer(new Tile(1, 1, 1, new int[][]{{1}}));
		while (!queue.isEmpty()) {
			Tile base = queue.poll();
			allTilesList.add(base);
			if (base.size == Constants.MAX_FIGURE_SIZE) continue;
			int[][] area = new int[base.width + 2][base.height + 2];
			base.stamp(area, 1, 1);

			for ( int x = 0; x < base.width + 2; x++ ) {
				for ( int y = 0; y < base.height + 2; y++ ) {
					
					if ( ( x == 0 || x == base.width + 1 ) && ( y == 0 || y == base.height + 1 ) ) continue;

					Tile newTile;

					if ( x == 0 ) {
						if ( area[x+1][y] == 0 ) continue;
						newTile = new Tile(area, 0, 1, base.width + 1, base.height);
						newTile.area[x][y-1] = 1;
					}
					else if ( x == base.width + 1 ) {
						if ( area[x-1][y] == 0 ) continue;
						newTile = new Tile(area, 1, 1, base.width + 1, base.height);
						newTile.area[x-1][y-1] = 1;
					}
					else if ( y == 0 ) {
						if ( area[x][y+1] == 0 ) continue;
						newTile = new Tile(area, 1, 0, base.width, base.height + 1);
						newTile.area[x-1][y] = 1;
					}
					else if ( y == base.height + 1 ) {
						if ( area[x][y-1] == 0 ) continue;
						newTile = new Tile(area, 1, 1, base.width, base.height + 1);
						newTile.area[x-1][y-1] = 1;
					}
					else {
						if ( area[x][y] == 1 ) continue;
						if ( area[x+1][y] == 0 && area[x-1][y] == 0 && area[x][y+1] == 0 && area[x][y-1] == 0 ) continue;
						newTile = new Tile(area, 1, 1, base.width, base.height);
						newTile.area[x-1][y-1] = 1;
					}
					newTile.size++;
					String newTileCode = newTile.toString();
					if (allTilesMap.containsKey(newTileCode)) continue;
					allTilesMap.put(newTileCode, newTile);
					queue.offer(newTile);
					//System.out.println(newTile);

				}
			}
		}

		allTiles = allTilesList.toArray(Tile[]::new);

		tileIndexMap = new HashMap<>();
		for (int i = 0; i < allTiles.length; i++) {
			tileIndexMap.put(allTiles[i].toString(), i);
		}
		Constants.TILES_COUNT = allTiles.length;
		System.out.printf("all tiles: %d\n", allTiles.length);
	}

	public static void drawTiles (int squareWidth, int tilesInARow) {
		int imgWidth = squareWidth * ((Constants.MAX_FIGURE_SIZE + 1) * tilesInARow + 1);
		int rows = allTiles.length / tilesInARow + (allTiles.length % tilesInARow == 0 ? 0 : 1);
		int imgHeight = squareWidth * ((Constants.MAX_FIGURE_SIZE + 1) * rows + 1);

		// Создание объекта BufferedImage
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		// Получение контекста графики
		Graphics graphics = image.getGraphics();

		// Рисование на изображении
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		for (int i = 0; i < allTiles.length; i++) {
			int row = i / tilesInARow;
			int column = i % tilesInARow;
			int x0 = squareWidth + column * (Constants.MAX_FIGURE_SIZE + 1) * squareWidth;
			int y0 = squareWidth + row * (Constants.MAX_FIGURE_SIZE + 1) * squareWidth;
			Tile tile = allTiles[i];
			for (int sx = 0; sx < tile.width; sx++) {
				for (int sy = 0; sy < tile.height; sy++) {
					if (tile.area[sx][sy] == 1) {
						graphics.setColor(Color.GRAY);
						graphics.fillRect(x0 + sx * squareWidth, y0 + sy * squareWidth, squareWidth, squareWidth);
					}
				}
			}
		}

		// Сохранение изображения в файл
		try {
			File outputFile = new File("tiles.png");
			ImageIO.write(image, "png", outputFile);
		} catch (Exception e) {
			System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
		}

		// Освобождение ресурсов
		graphics.dispose();
	}
}
