package org.yasya;

import java.io.File;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Tile {
	public static Tile [] allTiles;
	public int size;
	public int width;
	public int height;
	public int [][] area;

	public Tile Copy() {
		Tile newTile = new Tile(this.size, this.width, this.height);
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height ; y++) {
				newTile.area[x][y] = this.area[x][y];
			}
		}
		return newTile;
	}

	public static boolean Contains (ArrayList<Tile> tiles, Tile tile) {
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		Tile tile = (Tile)obj;
		if (this.width != tile.width || this.height != tile.height) {
			return false;
		}

		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height ; y++) {
				if (this.area[x][y] != tile.area[x][y]) {
					return false;
				}
			}
		}
		return true;
	}

	public static void GenerateTiles() {
		ArrayList<Tile> tiles = new ArrayList<>();
		Tile uno = new Tile(1, 1, 1);
		uno.area[0][0] = 1;
		tiles.add(uno);
		for (int size = 1; size < Constants.MAX_FIGURE_SIZE; size++) {
			ArrayList<Tile> tilesCopy = new ArrayList<>(tiles);
			for (Tile tile : tilesCopy) {
				if (tile.size == size) {
					for (int x = 0; x < tile.width; x++) {
						for (int y = 0; y < tile.height; y++) {
							if (tile.area[x][y] == 1) {
								// вставляем квадрат сверху
								if (y == 0) {
									Tile newTile = new Tile(size + 1, tile.width, tile.height + 1);
									newTile.area[x][0] = 1;
									for (int xx = 0; xx < tile.width; xx++) {
										for (int yy = 0; yy < tile.height; yy++) {
											newTile.area[xx][yy + 1] = tile.area[xx][yy];
										}
									}
									if (!Contains(tiles, newTile)) {
										tiles.add(newTile);
									}
								}
								else {
									if (tile.area[x][y - 1] == 0) {
										Tile newTile = tile.Copy();
										newTile.area[x][y - 1] = 1;
										if (!Contains(tiles, newTile)) {
											tiles.add(newTile);
										}
									}
								}
								// вставляем квадрат справа
								if (x == tile.width - 1) {
									Tile newTile = new Tile(size + 1, tile.width + 1, tile.height);
									newTile.area[x + 1][y] = 1;
									for (int xx = 0; xx < tile.width; xx++) {
										for (int yy = 0; yy < tile.height; yy++) {
											newTile.area[xx][yy] = tile.area[xx][yy];
										}
									}
									if (!Contains(tiles, newTile)) {
										tiles.add(newTile);
									}
								}
								else {
									if (tile.area[x + 1][y] == 0) {
										Tile newTile = tile.Copy();
										newTile.area[x + 1][y] = 1;
										if (!Contains(tiles, newTile)) {
											tiles.add(newTile);
										}
									}
								}
								// вставляем квадрат снизу
								if (y == tile.height - 1) {
									Tile newTile = new Tile(size + 1, tile.width, tile.height + 1);
									newTile.area[x][y + 1] = 1;
									for (int xx = 0; xx < tile.width; xx++) {
										for (int yy = 0; yy < tile.height; yy++) {
											newTile.area[xx][yy] = tile.area[xx][yy];
										}
									}
									if (!Contains(tiles, newTile)) {
										tiles.add(newTile);
									}
								}
								else {
									if (tile.area[x][y + 1] == 0) {
										Tile newTile = tile.Copy();
										newTile.area[x][y + 1] = 1;
										if (!Contains(tiles, newTile)) {
											tiles.add(newTile);
										}
									}
								}
								// вставляем квадрат слева
								if (x == 0) {
									Tile newTile = new Tile(size + 1, tile.width + 1, tile.height);
									newTile.area[0][y] = 1;
									for (int xx = 0; xx < tile.width; xx++) {
										for (int yy = 0; yy < tile.height; yy++) {
											newTile.area[xx + 1][yy] = tile.area[xx][yy];
										}
									}
									if (!Contains(tiles, newTile)) {
										tiles.add(newTile);
									}
								}
								else {
									if (tile.area[x - 1][y] == 0) {
										Tile newTile = tile.Copy();
										newTile.area[x - 1][y] = 1;
										if (!Contains(tiles, newTile)) {
											tiles.add(newTile);
										}
									}
								}
							}
						}

					}
				}
			}
		}
 		Tile.allTiles = tiles.toArray(new Tile[tiles.size()]);
 		System.out.println("Length:" + Tile.allTiles.length);
	}

	public static void DrawTiles (int squareWidth, int tilesInARow) {
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
			System.out.println("Изображение сохранено");
		} catch (Exception e) {
			System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
		}

		// Освобождение ресурсов
		graphics.dispose();
	}
}
