package org.yasya;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Solution {
	public Tile[] tiles;
	public Map<String, TilePosition> tilePositions = new HashMap<>();
	public int[] posX;
	public int[] posY;

	public record TilePosition(int x, int y) {}

	public Solution(Tile[] tiles, int[] posX, int[] posY) {
		this.tiles = tiles;
		this.posX = posX;
		this.posY = posY;

	}

	public static Solution startSolution() {
		Map<Integer, Integer> tileSize = new HashMap<>();
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		List<Tile> tilesList = new ArrayList<>();
		int k = 0;
		for(int x = 0; x < Constants.BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
				area[x][y] = k;
				tileSize.put(k, 1);
				k++;
			}
		}
		int bordersCount = (Constants.BOARD_HEIGHT - 1) * Constants.BOARD_WIDTH + Constants.BOARD_HEIGHT * (Constants.BOARD_WIDTH - 1);
		int[][] borders = new int[bordersCount][4];
		k = 0; 
		for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT - 1; y++) {
				borders[k][0] = x;
				borders[k][1] = y;
				borders[k][2] = x;
				borders[k][3] = y + 1;
				k++;
			}
		}
		for (int x = 0; x < Constants.BOARD_WIDTH - 1; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
				borders[k][0] = x;
				borders[k][1] = y;
				borders[k][2] = x + 1;
				borders[k][3] = y;
				k++;
			}
		}

		Random random = new Random();
		for (k = bordersCount - 1; k >= 0; k--) {
			int randomBorder = random.nextInt(k + 1);
			int x1 = borders[randomBorder][0];
			int y1 = borders[randomBorder][1];
			int x2 = borders[randomBorder][2];
			int y2 = borders[randomBorder][3];
			int n1 = area[x1][y1];
			int n2 = area[x2][y2];
			int size1 = tileSize.get(n1);
			int size2 = tileSize.get(n2);
			if(n1 != n2 && size1 + size2 <= Constants.MAX_FIGURE_SIZE) {
				for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
					for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
						if (area[x][y] == n2) {
							area[x][y] = n1;
						}
					}
				}
				tileSize.put(n1, size1 + size2);
				tileSize.put(n2, 0);
			}
			borders[randomBorder][0] = borders[k][0];
			borders[randomBorder][1] = borders[k][1];
			borders[randomBorder][2] = borders[k][2];
			borders[randomBorder][3] = borders[k][3];
		}

		int[] up = new int[Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT];
		int[] down = new int[Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT];
		int[] left = new int[Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT];
		int[] right = new int[Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT];

		Arrays.fill(up, Constants.BOARD_HEIGHT - 1);
		Arrays.fill(down, 0);
		Arrays.fill(left, Constants.BOARD_WIDTH - 1);
		Arrays.fill(right, 0);

		for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
				int n = area[x][y];
				up[n] = Math.min(up[n], y);
				down[n] = Math.max(down[n], y);
				left[n] = Math.min(left[n], x);
				right[n] = Math.max(right[n], x);
			}
		}
		for (int n = 0; n < Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT; n++) {
			if (up[n] <= down[n] && left[n] <= right[n]) {
				int tileWidth = right[n] - left[n] + 1;
				int tileHeight = down[n] - up[n] + 1;
				int[][] tileArea = new int[tileWidth][tileHeight];
				int size = 0;
				for (int x = 0; x < tileWidth; x++) {
					for (int y = 0; y < tileHeight; y++) {
						tileArea[x][y] = area[left[n] + x][up[n] + y] == n ? 1 : 0;
						if(tileArea[x][y] == 1) size++;
					}
				}
				Tile newTile = new Tile(tileWidth, tileHeight, size, tileArea);
				Integer tileIndex = Tile.tileIndexMap.get(newTile.toString());
				if(tileIndex == null) {
					System.out.println(Tile.toString(tileArea, tileWidth, tileHeight));
				}
				tilesList.add(newTile);
			}
		}
		Tile[] tiles = tilesList.toArray(Tile[]::new);
		int[] posX = new int[tiles.length];
		int[] posY = new int[tiles.length];
		for(int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i]; 
			posX[i] = App.random.nextInt(Constants.BOARD_WIDTH - tile.width + 1);
			posY[i] = App.random.nextInt(Constants.BOARD_HEIGHT - tile.height + 1);
		}
		Solution solution = new Solution(tiles, posX, posY);
		return solution;
	}

	public int score() {
		int s = 0;
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		for(int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i]; 
			tile.stamp(area, posX[i], posY[i]);
		}
		for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
			for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
				if(area[x][y] == 0) s++;
			}
		}
		return s;
	}

	public Solution copy() {
		Tile[] tilesCopy = Arrays.stream(tiles)
			.map(obj -> ((Tile)obj).copy())
			.toArray(Tile[]::new);
		int[] posXCopy = Arrays.copyOf(posX, posX.length);
		int[] posYCopy = Arrays.copyOf(posY, posY.length);
		Solution s = new Solution(tilesCopy, posXCopy, posYCopy);
		return s;
	}

	public Solution next() {
		Solution s = copy();
		int randomIndex = App.random.nextInt(s.tiles.length);
		Tile randomTile = s.tiles[randomIndex];
		int newX = App.random.nextInt(Constants.BOARD_WIDTH - randomTile.width + 1);
		int newY = App.random.nextInt(Constants.BOARD_HEIGHT - randomTile.height + 1);
		s.posX[randomIndex] = newX;
		s.posY[randomIndex] = newY;
		return s;
	}

}
