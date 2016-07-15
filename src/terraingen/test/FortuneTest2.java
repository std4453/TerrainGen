package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.random.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class FortuneTest2 implements MouseMotionListener, MouseListener,
		IProcessor<PointBox, PointBox> {
	protected int width = 600;
	protected int height = 600;
	protected BufferedImage image;
	protected JLabel imageLabel;
	protected JFrame frame;

	protected Statement<PointBox, VoronoiBox> voronoi;
	protected PointBox pointBox;
	protected terraingen.backend.commons.Point additionalPoint;

	public static void main(String[] args) {
		new FortuneTest2();
	}

	public FortuneTest2() {
		initData();
		initUI();
		showFrame();
	}

	protected void initData() {
		final int pointCount = 20;
		final long seed = 4453;

		// generate initial points
		Boundaries boundaries = new Boundaries(0, 10, 0, 10);
		ProcessorNode<Long, PointBox> initialPoints = new ProcessorNode<>(new
				PointsWhiteNoise(boundaries, pointCount));
		ProcessorNode<PointBox, PointBox> testEdgeCase = new ProcessorNode<>(this);
		new Edge<>(initialPoints.getOutput(), testEdgeCase.getInput());
		Statement<Long, PointBox> pointGenerator = new Statement<>(
				initialPoints.getInput(), testEdgeCase.getOutput());
		this.pointBox = Executor.execute(pointGenerator, seed);

		// voronoi generator pipeline
		ProcessorNode<PointBox, VoronoiBox> voronoi = new ProcessorNode<>(new Fortune());
		this.voronoi = new Statement<>(voronoi);

		// voronoi renderer pipeline
		this.image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);

		// process and render initial PointBox
		generate();
	}

	protected void initUI() {
		// swing components
		this.frame = new JFrame("Fortune Test 2");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		this.frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());
		this.imageLabel = new JLabel();
		panel.add(this.imageLabel, BorderLayout.CENTER);
		this.imageLabel.setBorder(new CompoundBorder(new EmptyBorder(20, 20, 20, 20), new
				LineBorder(Color.black)));
		// ImageIcon in JLabel
		ImageIcon icon = new ImageIcon(this.image);
		this.imageLabel.setIcon(icon);
		updateImage();

		// add input listeners
		this.imageLabel.addMouseMotionListener(this);
		this.imageLabel.addMouseListener(this);
	}

	protected void showFrame() {
		this.frame.pack();

		// place frame in the center of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.frame.getSize();
		this.frame.setLocation(
				(int) (screenSize.getWidth() / 2 - frameSize.getWidth() / 2),
				(int) (screenSize.getHeight() / 2 - frameSize.getHeight() / 2));

		this.frame.setVisible(true);
	}

	/**
	 * process and render the current {@link PointBox}
	 */
	protected void generate() {
		VoronoiBox voronoiBox = Executor.execute(this.voronoi, this.pointBox);
		VoronoiRenderer.renderToImage(voronoiBox, this.image);
	}

	/**
	 * update the image in the label
	 */
	protected void updateImage() {
		this.imageLabel.imageUpdate(this.image, ImageObserver.ALLBITS, 0, 0, this.width,
				this.height);
	}

	/**
	 * Transfer from screen coords to map coords
	 *
	 * @param event
	 * 		{@link MouseEvent} containing screen coords
	 *
	 * @return map coords
	 */
	protected terraingen.backend.commons.Point getPoint(MouseEvent event) {
		Insets insets = this.imageLabel.getInsets();
		double x = event.getX() - insets.top;
		double y = event.getY() - insets.left;

		Boundaries boundaries = this.pointBox.getBoundaries();
		double bWidth = boundaries.right - boundaries.left;
		double bHeight = boundaries.bottom - boundaries.top;
		return new terraingen.backend.commons.Point(
				x / this.width * bWidth + boundaries.left,
				y / this.height * bHeight + boundaries.top);
	}

	/**
	 * Test edge case where first two points have the same y coord
	 *
	 * @param input
	 * 		input
	 *
	 * @return input with point added
	 */
	@Override
	public PointBox process(PointBox input) {
		java.util.List<terraingen.backend.commons.Point> points = input.getPoints();
		double minY = Double.POSITIVE_INFINITY;
		for (terraingen.backend.commons.Point point : points)
			if (point.y < minY)
				minY = point.y;

		input.addPoint(new terraingen.backend.commons.Point(10, minY));
		return input;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		terraingen.backend.commons.Point p = this.getPoint(e);
		if (this.additionalPoint == null) {    // new point to add
			this.additionalPoint = new terraingen.backend.commons.Point();
			this.pointBox.addPoint(this.additionalPoint);
		}
		this.additionalPoint.set(p);
		generate();
		updateImage();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// as additionalPoint is already added to the PointMap, setting it to null will
		// only let mouseMoved() to create a new point, as if a point is permanently
		// inserted into the PointMax
		this.additionalPoint = null;
		generate();
		updateImage();
	}

	@Override
	public void mouseDragged(MouseEvent unused) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
