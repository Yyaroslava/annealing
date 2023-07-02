package org.yasya;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Field {
		public int [][] area;
		public Map<Integer, Integer> areaSize = new HashMap<>();
 		
		public Field() {}

		public static Field getRandom() {
			Field field = new Field();
			field.area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
			for(int x = 0; x < Constants.BOARD_WIDTH; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
					field.area[x][y] = 1 + x + Constants.BOARD_WIDTH * y;
					field.areaSize.put(1 + x + Constants.BOARD_WIDTH * y, 1);
				}
			}
			int actionsCount = (Constants.BOARD_HEIGHT - 1) * Constants.BOARD_WIDTH + Constants.BOARD_HEIGHT * (Constants.BOARD_WIDTH - 1);
			int [][] actions = new int [actionsCount][4];

			int currentAction = 0; 
			for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT - 1; y++) {
					actions[currentAction][0] = x;
					actions[currentAction][1] = y;
					actions[currentAction][2] = x;
					actions[currentAction][3] = y + 1;
					currentAction++;
				}
			}

			for (int x = 0; x < Constants.BOARD_WIDTH - 1; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
					actions[currentAction][0] = x;
					actions[currentAction][1] = y;
					actions[currentAction][2] = x + 1;
					actions[currentAction][3] = y;
					currentAction++;
				}
			}

			Random random = new Random();
			while (actionsCount > 0) {
				int randomAction = random.nextInt(actionsCount);
				int x1 = actions[randomAction][0];
				int y1 = actions[randomAction][1];
				int x2 = actions[randomAction][2];
				int y2 = actions[randomAction][3];
				int n1 = field.area[x1][y1];
				int n2 = field.area[x2][y2];
				int size1 = field.areaSize.get(n1);
				int size2 = field.areaSize.get(n2);
				if(n1 != n2 && size1 + size2 <= Constants.MAX_FIGURE_SIZE) {
					for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
						for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
							if (field.area[x][y] == n2) {
								field.area[x][y] = n1;
							}
						}
					}
					field.areaSize.put(n1, size1 + size2);
					field.areaSize.put(n2, 0);
				}
				actions[randomAction][0] = actions[actionsCount - 1][0];
				actions[randomAction][1] = actions[actionsCount - 1][1];
				actions[randomAction][2] = actions[actionsCount - 1][2];
				actions[randomAction][3] = actions[actionsCount - 1][3];
				actionsCount--;
			}
			return field;
		}

		
		public void show() {
			for (int y = 0; y < Constants.BOARD_HEIGHT ; y++) {
				for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
					System.out.print(String.format("%4d", this.area[x][y]));
				}
				System.out.println("");
			}
			System.out.println("");
		}

		public static Color getRandomColor() {
			Random random = new Random();
			int red = random.nextInt(256);
			int green = random.nextInt(256);
			int blue = random.nextInt(256);
			return new Color(red, green, blue);
		}

		public void saveToImg(int squareWidth) {
			int imgWidth = (Constants.BOARD_WIDTH + 2) * squareWidth;
			int imgHeight = (Constants.BOARD_HEIGHT + 2) * squareWidth;

			// Создание объекта BufferedImage
			BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

			// Получение контекста графики
			Graphics graphics = image.getGraphics();

			// Рисование на изображении
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, imgWidth, imgHeight);

			Map<Integer, Color> squareColor = new HashMap<>();

			for (int i = 0; i < Constants.BOARD_WIDTH * Constants.BOARD_HEIGHT; i++) {
				squareColor.put(i, Field.getRandomColor());
			}

			for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
					int n = this.area[x][y];
					Color c = squareColor.get(n);
					graphics.setColor(c);
					graphics.fillRect(squareWidth * (x + 1), squareWidth * (y + 1), squareWidth, squareWidth);
				}
			}

			// Сохранение изображения в файл
			try {
				File outputFile = new File("image.png");
				ImageIO.write(image, "png", outputFile);
				System.out.println("Изображение сохранено");
			} catch (Exception e) {
				System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
			}

			// Освобождение ресурсов
			graphics.dispose();
		}

		public void showStatistic() {
			int count4 = 0;
			for (int x = 0; x < Constants.BOARD_WIDTH - 1; x++) {
				for (int y = 0; y < Constants.BOARD_HEIGHT - 1; y++) {
					int a00 = this.area[x][y];
					int a01 = this.area[x][y + 1];
					int a11 = this.area[x + 1][y + 1];
					int a10 = this.area[x + 1][y];
					if (a00 != a01 && a00 != a10 && a00 != a11 && a01 != a10 && a01 != a11 && a10 != a11) {
						count4++;
					}
				}
			}
			System.out.println("4-кратних точок: " + count4);
		}	
	
	}