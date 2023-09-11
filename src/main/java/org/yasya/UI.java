package org.yasya;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.json.JSONObject;

public class UI {
	public static ImageIcon areaIcon = null;
	public static JLabel areaLabel = null;
	public static JLabel scoreLabel = null;
	public static JProgressBar progressBar = null;
	public static double currentTemperature = 1;
	public static boolean stop = false;
	public static boolean save = false;
	public static JFreeChart chart = null;
	public static ChartPanel chartPanel = null;
	public static JLabel temperatureLabel = null;
	public static JTextArea descriptionLabel = null;

	public class Config {
		public static final int CHART_COLUMNS_COUNT = 10;
	}

	public static void setAreaImage(BufferedImage image) {
		SwingUtilities.invokeLater(()->{
			areaIcon.setImage(image);
			areaLabel.repaint();
		});
	}

	public static void setScoreLabel(double bestScore) {
		SwingUtilities.invokeLater(()->{
			scoreLabel.setText(String.format("current best solution score: %6.1f", bestScore));
			scoreLabel.repaint();
		});
	}

	public static void setChart(JFreeChart chart) {
		SwingUtilities.invokeLater(()->{
			chartPanel.setSize(725, 400);
			chartPanel.setChart(chart);
		});
	}

	public static void setDescriptionLabel(String description) {
		SwingUtilities.invokeLater(()->{
			descriptionLabel.setText(description);
			descriptionLabel.repaint();
		});
	}

	public static boolean checkStop() {
		return stop;
	}

	public static void run() {

		InputStream uiStream = UI.class.getResourceAsStream("/UI.json");
		if (uiStream == null) {
			System.out.println("resource not found: UI.json");
			return;
		}
		Scanner scanner = new Scanner(uiStream).useDelimiter("\\A");
		String ui_txt = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		JSONObject ui_json = new JSONObject(ui_txt);
		System.out.println(ui_json);

		//area
		areaIcon = new ImageIcon();
		areaLabel = new JLabel(areaIcon);
		areaLabel.setBounds(20, 20, 400, 400);

		//chart
		chartPanel = new ChartPanel(null);
		chartPanel.setBounds(440, 20, 725, 400);	
		chartPanel.setDoubleBuffered(true);
		chartPanel.setChart(null);

		//score label
		scoreLabel = new JLabel("-");
		scoreLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setFont(new Font(null, 0, 36));
		scoreLabel.setBounds(450, 660, 700, 40);
				
		//stop button
		JButton stopButton = new JButton("STOP");
		stopButton.setBorder(new EmptyBorder(10, 20, 10, 20));
		stopButton.addActionListener(e -> {
			stop = true;
		});
		stopButton.setBounds(20, 660, 100, 40);

		//save button
		JButton saveButton = new JButton("SAVE");
		saveButton.setBorder(new EmptyBorder(10, 20, 10, 20));
		saveButton.addActionListener(e -> {
			save = true;
		});
		saveButton.setBounds(1065, 660, 100, 40);


		//progress bar
		progressBar = new JProgressBar(0, 100);
		progressBar.setForeground(Color.GREEN);
		progressBar.setBounds(0, 720, 1190, 20);
		
		//temperature label;
		temperatureLabel = new JLabel("t = " + Double.toString(currentTemperature));
		temperatureLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		temperatureLabel.setForeground(Color.WHITE);
		temperatureLabel.setFont(new Font(null, 0, 36));
		temperatureLabel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				currentTemperature = Math.max(0, Math.round((currentTemperature + (double)notches * 0.01) * 100) / (double)100);
				temperatureLabel.setText("t = " + Double.toString(currentTemperature));
			}
		});
		temperatureLabel.setBounds(160, 660, 250, 40);

		//description label
		descriptionLabel = new JTextArea("");
		descriptionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		descriptionLabel.setBackground(Color.DARK_GRAY);
		descriptionLabel.setForeground(Color.WHITE);
		descriptionLabel.setFont(new Font(null, 0, 18));
		descriptionLabel.setBounds(20, 440, 1145, 200);
		descriptionLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);
		descriptionLabel.setEditable(false);

		//TETRIS
		JMenuItem launchTetrisItem = new JMenuItem("Tetris");
		launchTetrisItem.addActionListener(e -> {
			stop = false;
			Tetris worker = new Tetris();
			worker.execute();
		});
		

		//SALESMAN
		JMenuItem launchSalesmanItem = new JMenuItem("Salesman");
		launchSalesmanItem.addActionListener(e -> {
			stop = false;
			Salesman worker = new Salesman();
			worker.execute();
		});
		

		//SYLLABUS
		JMenuItem launchSyllabusItem = new JMenuItem("Syllabus");
		launchSyllabusItem.addActionListener(e -> {
			stop = false;
			Syllabus worker = new Syllabus();
			worker.execute();
		});

		//CHEM
		JMenuItem launchChemItem = new JMenuItem("Chem");
		launchChemItem.addActionListener(e -> {
			stop = false;
			Chem worker = new Chem();
			worker.execute();
		});
		
		//3d panel
		YGC panel3D = new YGC();
		panel3D.setBounds(20, 20, 400, 400);

		//menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(launchTetrisItem);
		menuBar.add(launchSalesmanItem);
		menuBar.add(launchSyllabusItem);
		menuBar.add(launchChemItem);

		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBackground(Color.BLACK);
		mainPanel.setBounds(0, 0, 1190, 740);
		mainPanel.add(areaLabel);
		mainPanel.add(chartPanel);
		mainPanel.add(scoreLabel);
		mainPanel.add(stopButton);
		mainPanel.add(saveButton);
		mainPanel.add(progressBar);
		mainPanel.add(temperatureLabel);
		mainPanel.add(descriptionLabel);
		mainPanel.add(panel3D);

		//frame
		JFrame frame = new JFrame("Super solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(mainPanel);
		frame.setSize(1200, 800);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
