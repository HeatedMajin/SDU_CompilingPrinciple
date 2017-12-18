package xyz.majin.utils;

import java.util.HashMap;

/**
 * 给运算符，返回OPR对应的a的值
 * @author majin
 *
 */
public class YunSuanTable {
	private static HashMap<String, Integer> map = new HashMap<>();
	static{
		map.put("+", 2);
		map.put("-", 3);
		map.put("*", 4);
		map.put("/", 5);
//		map.put("", 1);
//		map.put("", 1);
//		map.put("", 1);
//		map.put("", 1);
//		map.put("", 1);
//		map.put("", 1);
//		map.put("", 1);
	}
	public static int getOPR_a(String code){
		return map.get(code);
	}
}
