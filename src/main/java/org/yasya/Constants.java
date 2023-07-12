package org.yasya;

public final class Constants {
	public static final int BOARD_WIDTH = 5;
	public static final int BOARD_HEIGHT = 5;
	public static final int MAX_FIGURE_SIZE = 3;
	public static final int TILES_COUNT = 9;
	public static final int ACTIONS_COUNT = 159;
	public static final double C_PUCT = 0.1; //Polynomial Upper Confidence Trees aka curiousity
	public static final int SEARCHES_PER_MOVE = 5000;
	public static final int INPUT_SIZE = BOARD_WIDTH * BOARD_HEIGHT + TILES_COUNT;
	public static final int OUTPUT_SIZE = ACTIONS_COUNT + 1;
	public static final int GAMES_PER_TRAIN = 20;
}
