package org.yasya;

import java.awt.Color;

public class Utils {
	
	public static int[] smash(int sum) {
		int n1 = -1;
		for(int i = (sum + 2); i > 2; i--) {
			if(App.random.nextInt(i) <= 1) {
				n1 = sum + 2 - i;
				break;
			};
		}
		int n2 = App.random.nextInt(sum - n1 + 1);
		int n3 = sum - n1 - n2;
		System.out.printf("%4d %4d %4d \n", n1, n2, n3);
		return new int[] {n1, n2, n3};
	}

	public static Color[] getPalette(int size) {
		Color[] colors = new Color[size];
		for(int i = 0; i < colors.length; i++) {
			int[] c = Utils.smash(240);
			colors[i] = new Color(c[0], c[1], c[2]);
		}
		return colors;
	}
}
