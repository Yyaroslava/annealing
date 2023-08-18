package org.yasya;

public final class Constants {
	public static final int TETRIS_BOARD_WIDTH = 16;
	public static final int TETRIS_BOARD_HEIGHT = 16;
	public static final int TETRIS_MAX_TILE_SIZE = 7;
	public static int TETRIS_TILES_COUNT;
	public static final int TETRIS_PARALLEL = 14;
	public static final int TETRIS_STEP_COUNT = 100000;
	public static final double TETRIS_INITIAL_T = 0.5;
	public static final int[][] TETRIS_PALETTE = new int[][] {
		{255, 0, 0},
		{255, 165, 0},
		{255, 255, 0},
		{0, 128, 0},
		{0, 0, 255},
		{75, 0, 130},
		{238, 130, 238}
	};

	public static final int SALESMAN_TOWNS_COUNT = 10;
	public static final int SALESMAN_PARALLEL = 14;
	public static final int SALESMAN_STEP_COUNT = 1000000;
	public static final double SALESMAN_INITIAL_T = 1000;
}
