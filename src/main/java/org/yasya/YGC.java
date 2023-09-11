package org.yasya;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class YGC extends Application {

	@Override
	public void start(Stage primaryStage) {
		// Создаем сферу
		Sphere sphere = new Sphere(100);
		sphere.setTranslateX(300);
		sphere.setTranslateY(300);
		sphere.setTranslateZ(0);
		sphere.setMaterial(new javafx.scene.paint.PhongMaterial(Color.BLUE));

		// Создаем группу и добавляем сферу
		Group root = new Group(sphere);

		// Создаем сцену
		Scene scene = new Scene(root, 600, 600, Color.WHITE);

		// Устанавливаем сцену на окно
		primaryStage.setScene(scene);

		// Устанавливаем заголовок окна
		primaryStage.setTitle("Простая 3D Графика на Java");

		// Отображаем окно
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}