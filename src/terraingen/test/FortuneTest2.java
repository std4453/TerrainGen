package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
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
	protected Graphics2D g;
	protected JLabel imageLabel;

	protected Statement<PointBox, VoronoiBox> voronoi;
	protected PointBox pointBox;
	protected terraingen.backend.commons.Point additionalPoint;

	public static void main(String[] args) {
		new FortuneTest2();
	}

	public FortuneTest2() {
		final int initial = 80;
		final long seed = 4453;

		Boundaries boundaries = new Boundaries(0, 10, 0, 10);
		ProcessorNode<Long, PointBox> pointGenerator = new ProcessorNode<>(new
				PointsWhiteNoise(boundaries, initial));
		ProcessorNode<PointBox, PointBox> addSamePoint = new ProcessorNode<>(this);
		new Edge<>(pointGenerator.getOutput(), addSamePoint.getInput());
		Statement<Long, PointBox> pointGenerator2 = new Statement<>(
				pointGenerator.getInput(), addSamePoint.getOutput());
		this.pointBox = Executor.execute(pointGenerator2, seed);
		ProcessorNode<PointBox, VoronoiBox> voronoi = new ProcessorNode<>(new Fortune());
		this.voronoi = new Statement<>(voronoi);


		JFrame frame = new JFrame("Fortune Test 2");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());
		this.imageLabel = new JLabel();
		panel.add(this.imageLabel, BorderLayout.CENTER);
		this.imageLabel.setBorder(new CompoundBorder(new EmptyBorder(20, 20, 20, 20), new
				LineBorder(Color.black)));

		this.image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);
		this.g = this.image.createGraphics();
		generate();

		ImageIcon icon = new ImageIcon(this.image);
		this.imageLabel.setIcon(icon);

		this.imageLabel.addMouseMotionListener(this);
		this.imageLabel.addMouseListener(this);

		refreshFrame(frame);
		frame.setVisible(true);
	}

	protected void refreshFrame(JFrame frame) {
		frame.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation((int) (screenSize.getWidth() / 2 - frameSize.getWidth() / 2),
				(int) (screenSize.getHeight() / 2 - frameSize.getHeight() / 2));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		terraingen.backend.commons.Point p = this.getPoint(e);
		if (this.additionalPoint == null) {
			this.additionalPoint = new Point();
			this.pointBox.addPoint(this.additionalPoint);
		}
		this.additionalPoint.set(p);
		generate();
	}

	protected void update() {
		this.imageLabel.imageUpdate(this.image, ImageObserver.ALLBITS, 0, 0, this.width,
				this.height);
	}

	protected void generate() {
		VoronoiBox voronoiBox = Executor.execute(this.voronoi, this.pointBox);
		VoronoiRenderer.renderToImage(voronoiBox, this.image);
		this.update();
	}

	protected terraingen.backend.commons.Point getPoint(MouseEvent event) {
		Insets insets = this.imageLabel.getInsets();
		int x = event.getX() - insets.top;
		int y = event.getY() - insets.left;

		return transfer(x, y);
	}

	protected terraingen.backend.commons.Point transfer(double x, double y) {
		Boundaries boundaries = this.pointBox.getBoundaries();
		double bWidth = boundaries.right - boundaries.left;
		double bHeight = boundaries.bottom - boundaries.top;
		return new terraingen.backend.commons.Point(
				x / this.width * bWidth + boundaries.left,
				y / this.height * bHeight + boundaries.top);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.additionalPoint = null;
		generate();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public PointBox process(PointBox input) {
//		java.util.List<Point> points = input.getPoints();
//		double minY = Double.POSITIVE_INFINITY;
//		for (Point point : points)
//			if (point.y < minY)
//				minY = point.y;
//
//		input.addPoint(new Point(1, minY));
		return input;
	}
}
