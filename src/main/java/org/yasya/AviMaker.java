package org.yasya;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jcodec.api.awt.AWTSequenceEncoder;

public class AviMaker {
	public String imagesFolder = null;
	public String outputVideoPath = null;
	private int width = 640;
	private int height = 480;

	public AviMaker setImagesFolder(String imagesFolder) {
		this.imagesFolder = imagesFolder;
		return this;
	}

	public AviMaker setOutputVideoPath(String outputVideoPath) {
		this.outputVideoPath = outputVideoPath;
		return this;
	}
		
	public void create() {
		if (imagesFolder == null || outputVideoPath == null) {
			System.out.println("file is not defined");
			return;
		}
		try {
			AWTSequenceEncoder enc = AWTSequenceEncoder.create25Fps(new File(outputVideoPath));
			File[] imageFiles = new File(imagesFolder).listFiles();
			if (imageFiles != null) {
				File outputVideoFile = new File(outputVideoPath);
				ImageOutputStream output = new FileImageOutputStream(outputVideoFile);
				for (int i = 0; i < imageFiles.length; i++) {
					BufferedImage image = ImageIO.read(imageFiles[i]);
					BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = scaledImage.createGraphics();
					g2d.drawImage(image, 0, 0, width, height, null);
					g2d.dispose();
					enc.encodeImage(scaledImage);
					System.out.println("imageFiles[i]" + imageFiles[i]);
				}
				enc.finish();
				output.close();
				System.out.println("video is created successfully!!");
			} else {
				System.out.println("no pictures found");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
