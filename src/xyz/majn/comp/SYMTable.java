package xyz.majn.comp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SYMTable {
	static Pattern p = Pattern.compile("^(\\d)+$");

	/**
	 * ���뵥�ʣ�����ϵͳ����
	 *  -1 ϵͳδʹ�õĵ��� 
	 *  -2 ����
	 * 
	 * @param word
	 * @return
	 */
	public static int getSysCode(String word) {
		int flag = -1; // ����ϵͳ����Ĺؼ���
		if (word.equals("const")) {
			flag = 1;
		} else if (word.equals("var")) {
			flag = 2;
		} else if (word.equals("procedure")) {
			flag = 3;
		} else if (word.equals("begin")) {
			flag = 4;
		} else if (word.equals("end")) {
			flag = 5;
		} else if (word.equals("odd")) {
			flag = 6;
		} else if (word.equals("is")) {
			flag = 7;
		} else if (word.equals("then")) {
			flag = 8;
		} else if (word.equals("call")) {
			flag = 9;
		} else if (word.equals("while")) {
			flag = 10;
		} else if (word.equals("read")) {
			flag = 11;
		} else if (word.equals("write")) {
			flag = 12;
		} else if (word.equals(">") || word.equals("<") || word.equals("+") || word.equals("-") || word.equals("*")
				|| word.equals("/") || word.equals("=") || word.equals(";") || word.equals(",") || word.equals(":")) {
			char arr[] = word.toCharArray();
			char c = arr[0];
			flag =  c + 0;
		}else if(word.equals(">=")){
			flag  = 100;
		}else if(word.equals("<=")){
			flag  = 101;
		}else if(word.equals(":=")){
			flag  = 102;
		} else {
			Matcher matcher = p.matcher(word);
			if (matcher.find()) {
				flag = -2;
			}
		}
		return flag;
	}
}
