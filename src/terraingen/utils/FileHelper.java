package terraingen.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHelper {
	private static Log log = LogFactory.getLog(FileHelper.class);

	public static boolean isReadableFile(File file) {
		return file != null && file.isFile() && file.exists() && file.canRead();
	}

	public static String readFile(File file) throws IOException {
		if (!isReadableFile(file))
			throw new IOException("File unavailable.");
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(file);
		while (fis.available() > 0)
			sb.append((char) fis.read());
		fis.close();
		return sb.toString();
	}
}
