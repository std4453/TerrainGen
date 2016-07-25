package terraingen.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.commons.Grid;
import terraingen.backend.commons.GridMapper;
import terraingen.backend.commons.GridRenderer;
import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.Statement;
import terraingen.frontend.dialog.DialogSingleImage;
import terraingen.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;

/**
 * Some code copied from {@link GridRendererTest}.<br />
 * It will render a inverted Mona Lisa in greyscale.
 */
public class GridMapperTest {
	private static final Log log = LogFactory.getLog(GridMapperTest.class);

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		BufferedImage image = ImageUtils.readInternalImage
				("/assets/images/test/monalisa.jpg");
		if (image == null) {
			log.error("Image not found.");
			return;
		}
		int width = image.getWidth();
		int height = image.getHeight();
		int sampling = 5;
		int sWidth = (width - 1) / sampling;
		int sHeight = (height - 1) / sampling;
		int x = 1, y;
		double data[][] = new double[sWidth][sHeight];
		for (int i = 0; i < sWidth; ++i, x += sampling) {
			y = 1;
			for (int j = 0; j < sHeight; ++j, y += sampling) {
				Color color = new Color(image.getRGB(x, y));
				data[i][j] = (color.getRed() + color.getGreen() + color.getBlue()) / 3f;
			}
		}

		Grid grid = new Grid(data);
		GridRenderer renderer = new GridRenderer();
		DialogSingleImage.instance.show(Executor.execute((Statement<Grid,
				BufferedImage>) embrace(
				create(new GridMapper((Statement<Double, Double>) embrace(
						create((Double n) -> -n)
				))),
				create(renderer)
		), grid));
	}
}
