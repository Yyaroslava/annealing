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
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.yasya.Annealing.FireWitness;

public class UI {
	
	public static void run() {
		ImageIcon areaIcon = new ImageIcon("bestAnnealingGreedy.png");
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setForeground(Color.GREEN);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.BLACK);
		JFrame frame = new JFrame("Super solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel areaLabel = new JLabel(areaIcon);
		areaLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		areaLabel.setName("area");

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("Launch");

		JMenuItem launchHybridAnnealingItem = new JMenuItem("Hybrid Annealing");

		launchHybridAnnealingItem.addActionListener(e -> {
			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
				@Override
				protected Void doInBackground() throws Exception {
					publish(0);
					Annealing.FireWitness witness = new FireWitness() {
						public Color[] colors;
						public SolutionHybrid bestSolution = null;
						public int bestScore = 999;
						public boolean stop = false;

						@Override
						public void afterStart(Annealing.MarkovChain s) {
							colors = Utils.getPalette(((SolutionHybrid)s).tiles.length);
						}

						@Override
						public void afterNewSolution(Annealing.MarkovChain s, int score, double t) {
							if(score < bestScore) {
								setBest((SolutionHybrid)s, score);
								System.out.printf("better solution found: %4d %8.5f \n", bestScore, t);
								saveBestSolution();
								areaIcon.getImage().flush();
								areaLabel.repaint();
							}
						}

						@Override
						public void beforeFinish(Annealing.MarkovChain last, Annealing.MarkovChain best) {}	

						public void saveBestSolution() {
							int[][] area = (bestSolution).greedy(true);
							PNGMaker.make(area, 20, "bestAnnealingGreedy.png", colors);
						}

						public synchronized void setBest(SolutionHybrid newSolution, int newScore) {
							bestScore = newScore;
							bestSolution = newSolution.copy();
							if(bestScore == 0) stop = true;
						}

						public synchronized boolean checkStop() {
							return stop;
						}

						@Override
						public boolean checkJump(Annealing.MarkovChain chain, Annealing.MarkovChain bestChain, int bestScore) {
							if(bestScore > this.bestScore) {
								chain.jump(bestScore, (SolutionHybrid)bestChain);
							}
							return false;
						}

						@Override
						public void onProgress(int progress) {
							publish(progress);
						}
					};
					SolutionHybrid.algorythmHybrid(witness);
					publish(100);
					return null;
				}

				@Override
				protected void process(java.util.List<Integer> chunks) {
					int latestProgress = chunks.get(chunks.size() - 1);
					progressBar.setValue(latestProgress);
				}

				@Override
				protected void done() {
					areaIcon.getImage().flush();
					areaLabel.repaint();
					
				}
			};

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
