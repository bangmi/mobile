package com.martin.mobileguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOHelper {
	public static String formatToString(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buff = new byte[1024];
		while ((len = is.read(buff)) != -1) {
			baos.write(buff, 0, len);
		}
		return baos.toString();
	}

}
