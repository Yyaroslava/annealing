package org.yasya;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Game {
	private Map<String, Position> positions = new HashMap<>();
	public static Action[] allActions = new Action[Constants.ACTIONS_COUNT];
	public MultiLayerNetwork nnet;
	
	static {
		int k = 0;
		for (int i = 0; i < Tile.allTiles.length; i++) {
			Tile tile = Tile.allTiles[i];
			for (int x = 0; x <= Constants.BOARD_WIDTH - tile.width; x++) {
				for (int y = 0; y <= Constants.BOARD_HEIGHT - tile.height; y++) {
					allActions[k++] = new Action(i, x, y);
				}
			}
		}
		System.out.println("allActions length: " + allActions.length);
		System.out.println("tiles count: " + Tile.allTiles.length);
	}

	Game(MultiLayerNetwork nnet) {
		this.nnet = nnet;
	}

	public class Position {
		public MCTS.Node node = null;
		public int[][] area = null;
		public int[] tiles = null;
		public boolean isFinal;
		public double score;
		public int[] validActions = null;

		private Position(){}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
				for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
					sb.append(area[x][y]);
				}
				sb.append("\n");
			}
			sb.append("\n\n");
			for (int k = 0; k < Constants.TILES_COUNT; k++) {
				if(tiles[k] > 0) {
					sb.append("" + k + ": " + tiles[k] + "\n");
				}
			}
			sb.append("valid actions: " + Arrays.toString(validActions));
			return sb.toString();
		}

		public double[] calculate() {
			double [] input = new double [Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT + Constants.TILES_COUNT];
			int k = 0;
			for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
					input[k++] = area[x][y];
				}
			}
			for (int i = 0; i < Constants.TILES_COUNT; i++) {
				input[k++] = tiles[i];
			}
			INDArray inputNet = Nd4j.create(input, new int[] {1, input.length});
			INDArray outputNet = nnet.output(inputNet);
			double[] output = outputNet.toDoubleVector();
			return output;
		}

		public void calculateNewNode(MCTS.Node node) {
			if(isFinal) {
				node.v = score;
			}
			else {
				double[] output = calculate();
				for(int i = 0; i < Constants.ACTIONS_COUNT; i++) {
					node.Pa[i] = output[i];
				}
				node.v = output[Constants.ACTIONS_COUNT];
			}
		}

		public void saveToImg(int squareWidth, String fileName) {
			int imgWidth = (Constants.BOARD_WIDTH + 2) * squareWidth;
			int imgHeight = (Constants.BOARD_HEIGHT + 2) * squareWidth;

			// Создание объекта BufferedImage
			BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

			// Получение контекста графики
			Graphics graphics = image.getGraphics();

			// Рисование на изображении
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, imgWidth, imgHeight);

			//Color rnc = Field.getRandomColor();
			Color rnc = Color.CYAN;
			for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
					Color c = area[x][y] == 1 ? rnc : Color.PINK;
					graphics.setColor(c);
					graphics.fillRect(squareWidth * (x + 1), squareWidth * (y + 1), squareWidth, squareWidth);
				}
			}

			// Сохранение изображения в файл
			try {
				File outputFile = new File(fileName);
				ImageIO.write(image, "png", outputFile);
			} catch (Exception e) {
				System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
			}

			// Освобождение ресурсов
			graphics.dispose();
		}

	}

	public Position getStartPosition() throws NoSuchAlgorithmException {
		Map<Integer, Integer> tileSize = new HashMap<>();
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		int[] tiles = new int[Constants.TILES_COUNT];
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
				for (int x = 0; x < tileWidth; x++) {
					for (int y = 0; y < tileHeight; y++) {
						tileArea[x][y] = area[left[n] + x][up[n] + y] == n ? 1 : 0;
					}
				}
				int tileIndex = Tile.getTileIndex(tileWidth, tileHeight, tileArea);
				tiles[tileIndex]++;
			}
		}

		String hash = Game.getPositionHash(area, tiles);
		if (positions.containsKey(hash)) {
			return positions.get(hash);
		}
		Position nextPosition = new Position();
		nextPosition.area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		nextPosition.tiles = tiles;
		positions.put(hash, nextPosition);

		nextPosition.isFinal = Arrays.stream(tiles).allMatch(element -> element == 0);
		if(nextPosition.isFinal) {
			int sum = Arrays.stream(area)
				.flatMapToInt(row -> Arrays.stream(row))
				.sum();
			nextPosition.score = (double)sum / (Constants.BOARD_HEIGHT * Constants.BOARD_WIDTH);
		}
		else {
			nextPosition.validActions = IntStream.range(0, Constants.ACTIONS_COUNT)
				.filter(actionIndex -> nextPosition.tiles[Game.allActions[actionIndex].tileIndex] > 0)
				.toArray();
		}
		return nextPosition;
	}

	Position getNextPosition(Position previousPosition, Action action) throws NoSuchAlgorithmException{
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		int[] tiles = new int[Constants.TILES_COUNT];
		for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
			for (int y = 0; y < Constants.BOARD_HEIGHT ; y++) {
				area[x][y] = previousPosition.area[x][y];
			}
		}
		for (int k = 0; k < Constants.TILES_COUNT; k++) {
			tiles[k] = previousPosition.tiles[k];
		}
		Tile tile = Tile.allTiles[action.tileIndex];
		for (int x = 0; x < tile.width; x++) {
			for (int y = 0; y < tile.height; y++) {
				if (tile.area[x][y] == 1) {
					area[x + action.x][y + action.y] = 1;
				}
			}
		}
		tiles[action.tileIndex]--;
		String hash = Game.getPositionHash(area, tiles);
		if (positions.containsKey(hash)) {
			return positions.get(hash);
		}
		Position nextPosition = new Position();
		nextPosition.area = area;
		nextPosition.tiles = tiles;
		positions.put(hash, nextPosition);
		nextPosition.isFinal = Arrays.stream(tiles).allMatch(element -> element == 0);
		if(nextPosition.isFinal) {
			int sum = Arrays.stream(area)
				.flatMapToInt(row -> Arrays.stream(row))
				.sum();
			nextPosition.score = (double)sum / (Constants.BOARD_HEIGHT * Constants.BOARD_WIDTH);
		}
		else {
			nextPosition.validActions = IntStream.range(0, Constants.ACTIONS_COUNT)
				.filter(actionIndex -> nextPosition.tiles[Game.allActions[actionIndex].tileIndex] > 0)
				.toArray();
		}
		return nextPosition;
	}

	public record Action(int tileIndex, int x, int y) {}

	public static String getPositionHash(int[][] area, int[] tiles) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String str = Arrays.deepToString(area) + Arrays.toString(tiles);
		byte[] byteHash = md.digest(str.getBytes(StandardCharsets.UTF_8));
		String hash = Base64.getEncoder().encodeToString(byteHash);
		return hash;
	}

}
