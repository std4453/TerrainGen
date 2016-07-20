package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Lloyd;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.random.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.Edge;
import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.ProcessorNode;
import terraingen.backend.nodegraph.Statement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Dynamically view each iteration of the
 * <a href="https://en.wikipedia.org/wiki/Lloyd%27s_Algorithm"><i>Lloyd Algorithm</i></a>
 */
public class LloydTest2 {
	private static final VoronoiRenderer.Conf renderConf =
			new VoronoiRenderer.Conf(false, true, true, true);

	private static final int width = 600;
	private static final int height = 600;

	protected JLabel imageLabel;
	protected BufferedImage image;

	public LloydTest2() {
		JFrame frame = new JFrame("Lloyd Test 2");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());
		this.imageLabel = new JLabel();
		panel.add(this.imageLabel, BorderLayout.CENTER);
		this.imageLabel.setBorder(new CompoundBorder(new EmptyBorder(20, 20, 20, 20), new
				LineBorder(Color.BLACK)));
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ImageIcon icon = new ImageIcon(this.image);
		this.imageLabel.setIcon(icon);
		Graphics g = this.image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		frame.setResizable(false);
		placeFrame(frame);
		frame.setVisible(true);

		while (frame.isVisible())
			loop();
	}

	protected void placeFrame(JFrame frame) {
		frame.pack();

		int width = frame.getWidth();
		int height = frame.getHeight();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screen.width;
		int screenHeight = screen.height;

		frame.setLocation(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2);
	}

	protected void update() {
		this.imageLabel.imageUpdate(this.image, ImageObserver.ALLBITS, 0, 0, width,
				height);
	}

	public void loop() {
		final int points = 100;
		final long seed = 4453;
		Boundaries boundaries = new Boundaries(0, 100, 0, 100);

		ProcessorNode<Long, PointBox> initial = new ProcessorNode<>(
				new PointsWhiteNoise(boundaries, points));
		ProcessorNode<PointBox, VoronoiBox> voronoi = new ProcessorNode<>(new Fortune());
		new Edge<>(initial.getOutput(), voronoi.getInput());
		VoronoiBox box = Executor.execute(new Statement<>(initial.getInput(),
				voronoi.getOutput()), seed);

		ProcessorNode<VoronoiBox, PointBox> lloyd = new ProcessorNode<>(new Lloyd());
		ProcessorNode<PointBox, VoronoiBox> voronoi2 = new ProcessorNode<>(new Fortune());
		new Edge<>(lloyd.getOutput(), voronoi2.getInput());
		Statement<VoronoiBox, VoronoiBox> lloyd2 = new Statement<>(lloyd.getInput(),
				voronoi2.getOutput());

		for (int i = 0; i < 50; ++i) {
			VoronoiRenderer.renderToImage(renderConf, box, this.image);
			this.update();

			box = Executor.execute(lloyd2, box);
			try {
				Thread.sleep(250);
			} catch (InterruptedException ignored) {
			}
		}
	}

	public static void main(String[] args) {
		new LloydTest2();
	}
}
