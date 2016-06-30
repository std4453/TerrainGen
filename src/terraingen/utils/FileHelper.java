package terraingen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHelper {
	private static Log	log	= LogFactory.getLog(FileHelper.class);

	public static String readFile(File file) throws IOException {
		if (file == null || file.isDirectory())
			throw new IOException("File unavailable!");
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(file);
		while (fis.available() > 0)
			sb.append((char) fis.read());
		fis.close();
		return sb.toString();
	}
}
