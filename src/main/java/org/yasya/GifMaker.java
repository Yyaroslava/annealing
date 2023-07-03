package org.yasya;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifMaker {
	public static void make(String srcFolder, String fileName, int delayBetweenFrames) {
		String imageFolder = srcFolder;
		List<BufferedImage> images = loadImagesFromFolder(imageFolder);
		createGif(images, fileName, delayBetweenFrames);
	}

	private static List<BufferedImage> loadImagesFromFolder(String folderPath) {
		List<BufferedImage> images = new ArrayList<>();
		File folder = new File(folderPath);

		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				try {
					BufferedImage image = ImageIO.read(file);
					images.add(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return images;
	}

	private static void createGif(List<BufferedImage> images, String outputFilePath, int delayBetweenFrames) {
		try {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
			ImageWriteParam writeParam = writer.getDefaultWriteParam();

			ImageOutputStream outputStream = new FileImageOutputStream(new File(outputFilePath));
			writer.setOutput(outputStream);
			writer.prepareWriteSequence(null);

			IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(images.get(0)), writeParam);
			String metaFormatName = metadata.getNativeMetadataFormatName();
			IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
			IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

			graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delayBetweenFrames / 10)); // Задержка указывается в сотых долях секунды
			
			metadata.setFromTree(metaFormatName, root);

			for (BufferedImage image : images) {
				IIOImage iioImage = new IIOImage(image, null, metadata);
				writer.writeToSequence(iioImage, writeParam);
			}

			writer.endWriteSequence();
			outputStream.close();
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int numChildren = rootNode.getLength();
		for (int i = 0; i < numChildren; i++) {
			if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
				return (IIOMetadataNode) rootNode.item(i);
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return node;
	}
}