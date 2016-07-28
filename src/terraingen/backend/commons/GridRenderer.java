package terraingen.backend.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static terraingen.utils.MathUtils.round;

/**
 * Renders the {@link Grid} to a {@link BufferedImage}<br /><br />
 * It actually renders the {@link Grid} with a simple linear interpolation from a given
 * minimum value and a maximum value to two given colors. For further control of the
 * image, it should be processed afterwards.
 */
public class GridRenderer implements IProcessor<Grid, BufferedImage> {
	private static final Log log = LogFactory.getLog(GridRenderer.class);

	private static final int DEFAULT_IMAGE_WIDTH = 600;
	private static final int DEFAULT_IMAGE_HEIGHT = 600;

	public GridRenderer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public GridRenderer() {
		this(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
	}

	protected int width;
	protected int height;

	@Override
	public BufferedImage process(Grid input) {
		BufferedImage image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);
		renderToImage(input, image);
		return image;
	}

	public static void renderToImage(Grid input, BufferedImage image) {
		if (input != null)
			renderToImage(input.getMin(), input.getMax(), input, image);
		else
			log.warn("Null grid given.");
	}

	public static void renderToImage(double min, double max, Grid input,
									 BufferedImage image) {
		renderToImage(min, max, null, null, input, image);
	}

	public static void renderToImage(double min, double max, Color color1, Color color2,
									 Grid input, BufferedImage image) {
		if (image == null) {
			log.warn("Null image given.");
			return;
		}
		if (input == null) {
			log.warn("Null grid given.");
			return;
		}

		if (color1 == null)
			color1 = Color.BLACK;
		if (color2 == null)
			color2 = Color.WHITE;

		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int width = input.getWidth();
		int height = input.getHeight();
		Boundaries imageBoundaries = new Boundaries(image.getWidth(), image.getHeight());
		Boundaries inputBoundaries = new Boundaries(width, height);

		// algorithm 1: render a rectangle for each grid point
		double rWidth = image.getWidth() / width;
		double rHeight = image.getHeight() / height;
		for (int i = 0; i < width; ++i)
			for (int j = 0; j < height; ++j) {
				Point renderCenter = MathUtils.transform(inputBoundaries, new Point(i, j)
						, imageBoundaries);
				double value = input.get(i, j);
				Color color = interpolate(min, max, value, color1, color2);
				g.setColor(color);

				int x = round(renderCenter.x);
				int y = round(renderCenter.y);
				// plus-1 hack so that there won't be black stripes
				int w = round(renderCenter.x + rWidth - x) + 1;
				int h = round(renderCenter.y + rHeight - y) + 1;
				g.fillRect(x, y, w, h);
			}
	}

	protected static Color interpolate(double min, double max, double value, Color
			color1, Color color2) {
		double t = (value - min) / (max - min);
		double t2 = 1 - t;
		return new Color((int) (color2.getRed() * t + color1.getRed() * t2),
				(int) (color2.getGreen() * t + color1.getGreen() * t2),
				(int) (color2.getBlue() * t + color1.getBlue() * t2));
	}
}
