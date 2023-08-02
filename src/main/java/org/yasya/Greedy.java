package org.yasya;

import java.security.NoSuchAlgorithmException;

public class Greedy {

	public static void toad() {
		//получить случайное разбиение пазла на тайлы и взять случайный порядок
	}

	/* 
	public static void algorythmGreedy() throws NoSuchAlgorithmException {
		clearDirectory("video/src");
		Game game = new Game(null);
		Game.Position position = game.getStartPosition();
		int frameNumber = 0;
		for(int n = 0; n < 25; n++) {
			position.saveToImg(20, String.format("video/src/%04d.png", frameNumber++));
		}
		while(!position.isFinal) {
			Tile bestTile = null;
			int bestX = -1;
			int bestY = -1;
			int bestInersect = Constants.MAX_FIGURE_SIZE + 1;
			int bestTileIndex = -1;
			int bestSize = 0;
			for(int q = Constants.MAX_FIGURE_SIZE; q > 0; q--) {
				for(int tileIndex = 0; tileIndex < Constants.TILES_COUNT; tileIndex++) {
					if(position.tiles[tileIndex] > 0 && Tile.allTiles[tileIndex].size == q) {
						Tile currentTile = Tile.allTiles[tileIndex];
						for( int y = 0; y < Constants.BOARD_HEIGHT - currentTile.height + 1; y++) {
							for(int x = 0; x < Constants.BOARD_WIDTH - currentTile.width + 1; x++) {
								int currentIntersect = intersect(position.area, currentTile, x, y);
								//System.out.printf("tile index: %d, x: %d, y: %d, width: %d, height: %d \n", tileIndex, x, y, currentTile.width, currentTile.height);
								if(bestTile == null) {
									bestTile = currentTile;
									bestX = x;
									bestY = y;
									bestInersect = currentIntersect;
									bestTileIndex = tileIndex;
									bestSize = currentTile.size;
								}
								else if(currentTile.size > bestTile.size) {
									bestTile = currentTile;
									bestX = x;
									bestY = y;
									bestInersect = currentIntersect;
									bestTileIndex = tileIndex;
									bestSize = currentTile.size;
								}
								else if(currentTile.size == bestTile.size && bestInersect > currentIntersect) {
									bestTile = currentTile;
									bestX = x;
									bestY = y;
									bestInersect = currentIntersect;
									bestTileIndex = tileIndex;
									bestSize = currentTile.size;
								}
							}
						}
					}
				}
			}
			int actionIndex = Game.findAction(bestTileIndex, bestX, bestY);
			//System.out.printf("tile index: %d, x: %d, y: %d, size: %d, intersect: %d \n", bestTileIndex, bestX, bestY, bestSize, bestInersect);
			position = game.getNextPosition(position, Game.allActions[actionIndex]);
			for(int n = 0; n < 25; n++) {
				position.saveToImg(20, String.format("video/src/%06d.png", frameNumber++));
			}
			System.out.println(position.toString());
		}
		System.out.println(position.toString());
		System.out.printf("position score: %f", position.score);
	}
	*/
}
