package org.yasya;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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

	public class Position {
		public int[][] area = null;
		public int[] tiles = null;
		public boolean isFinal;
		public double score;

		private Position(){}

		public double[] calculate(MultiLayerNetwork net) {
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
			INDArray outputNet = net.output(inputNet);
			double[] output = outputNet.toDoubleVector();
			
			return output;
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

	public Position getStartPosition() {
		Position position = new Position();
		position.area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		position.tiles = new int[Constants.TILES_COUNT];
		return position;
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
