package terraingen.frontend.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * Utility class for dialogs.
 */
public class DialogUtils {
	private static Log log = LogFactory.getLog(DialogUtils.class);

	public static void waitUntilFrameIsClosed(JFrame frame) {
		if (frame == null) {
			log.warn("Not-null frame expected.");
			return;
		}
		while (frame.isVisible()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Sleep interrupted while waiting for frame to be closed.", e);
				return;
			}
		}
		log.trace("Frame closed.");
	}
}
