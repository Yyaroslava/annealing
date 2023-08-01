package org.yasya;

public final class Constants {
	public static final int BOARD_WIDTH = 16;
	public static final int BOARD_HEIGHT = 16;
	public static final int MAX_FIGURE_SIZE = 4;
	public static int TILES_COUNT;
	public static int ACTIONS_COUNT;
	public static final double C_PUCT = 0.1; //Polynomial Upper Confidence Trees aka curiousity
	public static final int SEARCHES_PER_MOVE = 5000;
	public static final int INPUT_SIZE = BOARD_WIDTH * BOARD_HEIGHT + TILES_COUNT;
	public static final int OUTPUT_SIZE = ACTIONS_COUNT + 1;
	public static final int GAMES_PER_PLAY = 10000;
	public static final int FIGHTS_PER_COMPETITION = 20;
	public static final int WINS_FOR_VICTORY = 12;
}
