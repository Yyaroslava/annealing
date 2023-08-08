package org.yasya;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI {
	
	public static void run() {
		JFrame frame = new JFrame("Непростое оконное приложение");
		//frame.setSize(800, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setResizable(false);
		
		JLabel areaLabel = new JLabel(new ImageIcon("bestAnnealingGreedy.png"));
		areaLabel.setName("area");

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("Launch");

		JMenuItem launchHybridAnnealingItem = new JMenuItem("Hybrid Annealing");

		/*
		
		launchHybridAnnealingItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(
					() -> {
						SolutionHybrid.algorythmHybrid();
						SwingUtilities.invokeLater(
							() -> {
								areaLabel.setIcon(new ImageIcon("bestAnnealingGreedy.png"));
							}
						);
					}
				);
			}
		}); 
		
		*/
		
		launchHybridAnnealingItem.addActionListener(e -> {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					SolutionHybrid.algorythmHybrid();
					return null;
				}

				@Override
				protected void done() {
					//areaLabel.setIcon(new ImageIcon("bestAnnealingGreedy.png"));
					//areaLabel.revalidate();
					//areaLabel.repaint();
					frame.getContentPane().remove(areaLabel);
					JLabel areaLabel = new JLabel(new ImageIcon("bestAnnealingGreedy.png"));
					frame.getContentPane().add(areaLabel, BorderLayout.LINE_START);
					frame.revalidate();
					frame.repaint();
				}
			};

			worker.execute();
		});

		JMenuItem saveItem = new JMenuItem("Сохранить");
		JMenuItem exitItem = new JMenuItem("Выход");

		fileMenu.add(launchHybridAnnealingItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(areaLabel, BorderLayout.LINE_START);
		frame.pack();


		frame.setVisible(true);

		
	}
}
