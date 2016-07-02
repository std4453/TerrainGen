package terraingen.test;

import terraingen.frontend.dialog.DialogSingleImage;
import terraingen.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

/** Test for {@link terraingen.frontend.dialog.DialogSingleImage} */
public class DialogSingleImageTest {
	public static void main(String[] args) throws IOException {
		DialogSingleImage dialog = DialogSingleImage.instance;

		BufferedImage image = ImageUtils.readInternalImage("/assets/images/test/image1" +
				".jpg");
		// you should see the image shown now
		dialog.show(image);
		// close the dialog
		
		BufferedImage image2 = ImageUtils.readInternalImage("/assets/images/test/image2" +
				".jpg");
		// you should see another image here
		dialog.show(image2);
	}
}
