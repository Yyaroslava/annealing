package org.yasya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class UI {
	public static ImageIcon areaIcon = null;
	public static JLabel areaLabel = null;
	public static JProgressBar progressBar = null;
	public static double currentTemperature = 1;
	public static boolean stop = false;
	public static JFreeChart chart = null;
	public static ChartPanel chartPanel = null;

	public static void run() {
		areaIcon = new ImageIcon("area.png");

		progressBar = new JProgressBar(0, 100);
		progressBar.setForeground(Color.GREEN);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(Color.BLACK);
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.BLACK);
		
		JFrame frame = new JFrame("Super solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		areaLabel = new JLabel(areaIcon);
		areaLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		areaLabel.setName("area");

		JMenuBar menuBar = new JMenuBar();

		JButton stopButton = new JButton("STOP");
		stopButton.setBorder(new EmptyBorder(10, 20, 10, 20));
		stopButton.addActionListener(e -> {
			stop = true;
		});

		JLabel temperatureLabel = new JLabel("t = " + Double.toString(currentTemperature));
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

		chartPanel = new ChartPanel(null);

		//TETRIS
		JMenuItem launchTetrisItem = new JMenuItem("Tetris");
		launchTetrisItem.addActionListener(e -> {
			stop = false;
			Tetris worker = new Tetris();
			worker.execute();
		});
		menuBar.add(launchTetrisItem);

		//SALESMAN
		JMenuItem launchSalesmanItem = new JMenuItem("Salesman");
		launchSalesmanItem.addActionListener(e -> {
			stop = false;
			Salesman worker = new Salesman();
			worker.execute();
		});
		menuBar.add(launchSalesmanItem);

		//SYLLABUS
		JMenuItem launchSyllabusItem = new JMenuItem("Syllabus");
		launchSyllabusItem.addActionListener(e -> {
			stop = false;
			Syllabus worker = new Syllabus();
			worker.execute();
		});
		menuBar.add(launchSyllabusItem);

		mainPanel.add(panel);
		mainPanel.add(panel2);
		mainPanel.add(progressBar, BorderLayout.PAGE_END);

		panel.add(areaLabel, BorderLayout.CENTER);
		panel.add(chartPanel);

		panel2.add(stopButton);
		panel2.add(temperatureLabel);
		panel2.setBackground(Color.BLACK);
		
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(mainPanel);
		//frame.getContentPane().add(panel2);
		frame.setSize(800, 650);
		frame.setVisible(true);
	}
}
