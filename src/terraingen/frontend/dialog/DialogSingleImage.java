package terraingen.frontend.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/** A simple dialog that displays an image using a {@code BufferedImage} */
public class DialogSingleImage implements IDialog {
	private static Log log = LogFactory.getLog(DialogSingleImage.class);
	public static DialogSingleImage instance = new DialogSingleImage();

	/** Just there to get rid of magic numbers */
	private static int IMAGE_MARGIN = 30;

	protected JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle("Image");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);

		return frame;
	}

	/** Add the image label to the frame */
	protected JLabel populateFrame(JFrame frame, ImageIcon icon) {
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(icon);
		label.setBorder(
				new EmptyBorder(IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN));
		panel.add(label);

		return label;
	}

	/**
	 * Places the frame in the middle of the screen.<br />
	 * ( screen size obtained using {@code Toolkit.getDefaultToolkit().getScreenSize()} )
	 */
	protected void placeFrame(JFrame frame) {
		frame.pack();
		Dimension size = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double x = screenSize.getWidth() / 2 - size.getWidth() / 2;
		double y = screenSize.getHeight() / 2 - size.getHeight() / 2;
		frame.setLocation((int) x, (int) y);
	}

	@Override
	public Object show(Object obj) {
		if (obj == null || !(obj instanceof BufferedImage)) {
			log.warn("Non-null instance of BufferedImage expected.");
			return null;
		}
		BufferedImage image = ((BufferedImage) obj);
		ImageIcon icon = new ImageIcon(image);
		JFrame frame = createFrame();
		JLabel imageLabel = populateFrame(frame, icon);
		placeFrame(frame);
		frame.setVisible(true);
		DialogUtils.waitUntilFrameIsClosed(frame);

		return null;
	}
}
