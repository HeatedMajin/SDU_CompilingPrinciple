package xyz.majin.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

public class Utils {
	public static String filePath = "src/programmer.txt";
	private static DataInputStream datain = null;
	private static FileInputStream in = null;
	static {
		try {
			// 初始化文件流
			in = new FileInputStream(filePath);
			datain = new DataInputStream(in);
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				if (datain != null)
					datain.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	/**
	 * 读取文件中的一个字符
	 * 
	 * @return
	 * @throws EOFException
	 */
	public static char getChar() throws EOFException {
		try {
			return (char) datain.readByte();
		} catch (EOFException e) {
			throw new EOFException();
		} catch (Exception e1) {
			try {
				if (datain != null)
					datain.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw new RuntimeException(e1);
		}
	}
	
	
	public static boolean isSuanFu(char c) {
		if (c == '>' || c == '<' || c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == ';' || c == ','
				|| c == ':') {
			return true;
		}
		return false;
	}

	public static boolean isLianYunsuan(char c) {
		if (c == '>' || c == '<' || c == ':') {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符是不是数字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isDigit(char c) {
		// System.out.println((int) c);
		if (c <= 57 && c >= 48) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 判断字符是不是英文字母
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c) {

		if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
			return true;
		} else {
			return false;
		}

	}
}
