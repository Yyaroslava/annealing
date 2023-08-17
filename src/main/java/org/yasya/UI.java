package org.yasya;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class UI {
	public static ImageIcon areaIcon = null;
	public static JLabel areaLabel = null;
	public static JProgressBar progressBar = null;

	public static void run() {
		areaIcon = new ImageIcon("bestAnnealingGreedy.png");

		progressBar = new JProgressBar(0, 100);
		progressBar.setForeground(Color.GREEN);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.BLACK);
		
		JFrame frame = new JFrame("Super solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		areaLabel = new JLabel(areaIcon);
		areaLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		areaLabel.setName("area");

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("Launch");

		JMenuItem launchHybridAnnealingItem = new JMenuItem("Hybrid Annealing");

		launchHybridAnnealingItem.addActionListener(e -> {
			SolutionHybrid worker = new SolutionHybrid();
			worker.execute();
		});

		JMenuItem saveItem = new JMenuItem("Сохранить");
		JMenuItem exitItem = new JMenuItem("Выход");

		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);
		menuBar.add(launchHybridAnnealingItem);
		
		panel.add(areaLabel, BorderLayout.CENTER);
		panel.add(panel2);
		panel.add(progressBar, BorderLayout.PAGE_END);
		panel.setBackground(Color.BLACK);
		
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(panel);
		frame.setSize(800, 650);
		frame.setVisible(true);
	}
}
