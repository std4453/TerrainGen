package terraingen.frontend.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** A simple dialog that displays an image using a {@code BufferedImage} */
public class DialogSingleImage implements IDialog {
	private static Log log = LogFactory.getLog(DialogSingleImage.class);
	public static DialogSingleImage instance = new DialogSingleImage();

	/** Just there to get rid of magic numbers */
	private static int IMAGE_MARGIN = 30;

	protected JFrame frame;
	protected JLabel imageLabel;

	protected DialogSingleImage() {
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.frame.setResizable(false);
		JPanel panel = new JPanel();
		this.frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());
		this.imageLabel = new JLabel("");
		this.imageLabel.setBorder(
				new EmptyBorder(IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN));
		panel.add(this.imageLabel, BorderLayout.CENTER);
	}

	/**
	 * Places the frame in the middle of the screen.<br />
	 * ( screen size obtained using {@code Toolkit.getDefaultToolkit().getScreenSize()} )
	 */
	protected void placeFrame() {
		this.frame.pack();
		Dimension size = this.frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double x = screenSize.getWidth() / 2 - size.getWidth() / 2;
		double y = screenSize.getHeight() / 2 - size.getHeight() / 2;
		this.frame.setLocation((int) x, (int) y);
	}

	@Override
	public Object show(Object obj) {
		if (obj == null || !(obj instanceof BufferedImage)) {
			log.warn("Non-null instance of BufferedImage expected.");
			return null;
		}
		BufferedImage image = ((BufferedImage) obj);
		ImageIcon icon = new ImageIcon(image);
		this.imageLabel.setIcon(icon);
		placeFrame();
		this.frame.setVisible(true);
		DialogUtils.waitUntilFrameIsClosed(this.frame);

		return null;
	}

	public static void main(String[] args) throws IOException {
		DialogSingleImage dialog = DialogSingleImage.instance;
		BufferedImage image = ImageUtils.readInternalImage("/assets/images/test/image1" +
				".jpg");
		dialog.show(image);
	}
}
