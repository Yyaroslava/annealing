package org.yasya;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SolutionAnnealing implements Annealing.MarkovChain {
	public Tile[] tiles;
	public Map<String, TilePosition> tilePositions = new HashMap<>();
	public int[] posX;
	public int[] posY;

	public record TilePosition(int x, int y) {}

	public SolutionAnnealing(Tile[] tiles, int[] posX, int[] posY) {
		this.tiles = tiles;
		this.posX = posX;
		this.posY = posY;
	}

	public static SolutionAnnealing startSolution() {
		Tile[] tiles = Tile.randomSmash();
		int[] posX = new int[tiles.length];
		int[] posY = new int[tiles.length];
		for(int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i]; 
			posX[i] = App.random.nextInt(Constants.BOARD_WIDTH - tile.width + 1);
			posY[i] = App.random.nextInt(Constants.BOARD_HEIGHT - tile.height + 1);
		}
		SolutionAnnealing solution = new SolutionAnnealing(tiles, posX, posY);
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

	public SolutionAnnealing copy() {
		Tile[] tilesCopy = Arrays.stream(tiles)
			.map(obj -> ((Tile)obj).copy())
			.toArray(Tile[]::new);
		int[] posXCopy = Arrays.copyOf(posX, posX.length);
		int[] posYCopy = Arrays.copyOf(posY, posY.length);
		SolutionAnnealing s = new SolutionAnnealing(tilesCopy, posXCopy, posYCopy);
		return s;
	}

	public SolutionAnnealing next() {
		SolutionAnnealing s = copy();
		int randomIndex = App.random.nextInt(s.tiles.length);
		Tile randomTile = s.tiles[randomIndex];
		int newX = App.random.nextInt(Constants.BOARD_WIDTH - randomTile.width + 1);
		int newY = App.random.nextInt(Constants.BOARD_HEIGHT - randomTile.height + 1);
		s.posX[randomIndex] = newX;
		s.posY[randomIndex] = newY;
		return s;
	}

}
