package terraingen.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for image operations via {@link java.awt.image.BufferedImage}
 */
public class ImageUtils {
	private static Log log = LogFactory.getLog(ImageUtils.class);

	public static BufferedImage readImage(File file) throws IOException {
		if (!FileHelper.isReadableFile(file))
			throw new IOException("File unavailable.");

		BufferedImage image = ImageIO.read(file);
		return image;
	}

	public static BufferedImage readInternalImage(String path) throws IOException {
		if (path == null) {
			log.warn("Non-null path excepted.");
			return null;
		}
		InputStream inputStream = ImageUtils.class.getResourceAsStream(path);
		if (inputStream == null) {
			throw new IOException("Resource unavailable.");
		}
		BufferedImage image = ImageIO.read(inputStream);
		return image;
	}
}
