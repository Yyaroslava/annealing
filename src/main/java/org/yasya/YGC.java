package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class YGC {

	public static RoundButton getRoundButton(String label) {
		return (new YGC()).new RoundButton(label);
	}


	class RoundButton extends JButton {

		public RoundButton(String label) {
			super(label);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setOpaque(false);
			setForeground(Color.WHITE);
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		}
	
		@Override
		protected void paintComponent(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(Color.PINK);
			} else {
				g.setColor(Color.RED);
			}
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
	
			super.paintComponent(g);
		}
	
		@Override
		protected void paintBorder(Graphics g) {
			//g.setColor(getForeground());
			//g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}
	
		@Override
		public boolean contains(int x, int y) {
			Shape shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
			return shape.contains(x, y);
		}

	}

}
