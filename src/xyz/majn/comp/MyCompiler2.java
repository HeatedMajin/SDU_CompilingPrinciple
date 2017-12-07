//package xyz.majn.comp;
//
//import java.io.EOFException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//
//import xyz.majin.excep.SyntaxError;
//import xyz.majin.utils.Kind;
//import xyz.majin.utils.NameItem;
//import xyz.majin.utils.Utils;
//
//public class MyCompiler2 {
//	// 每个单词的类别，用户自定义变量名为-1，常数为-2，系统定义的见@xyz.majn.comp.SYMTable
//	public static LinkedList<Integer> SYM = new LinkedList<>();
//	// -1在SYM表中的位置为key，用户自定义变量的名字为value
//	public static HashMap<Integer, String> ID = new HashMap<>();
//	// -2在SYM表中的位置为key，用户自定义变量的名字为value
//	public static HashMap<Integer, Integer> NUM = new HashMap<>();
//
//	/************************************* 词法分析 ***********************************************/
//	private static String word;
//
//	/**
//	 * 获取字符表
//	 */
//	public static void getSym(boolean print) {
//		try {
//			char c = ' ';
//			while (true) {
//				word = "";
//				while (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
//					c = Utils.getChar();
//				}
//				if (Utils.isLetter(c)) {// 以字母开头
//					word += c;
//					c = Utils.getChar();
//					while (Utils.isLetter(c) || Utils.isDigit(c)) {// 后跟任意多个字母和数字
//						word += c;
//						c = Utils.getChar();
//					}
//					int sysCode = SYMTable.getSysCode(word);
//					SYM.add(sysCode);
//					if (sysCode == -1) {// 用户标识符
//						ID.put(SYM.size() - 1, word);
//					}
//				} else if (Utils.isDigit(c)) {// 以数字开头
//					word += c;
//					c = Utils.getChar();
//					while (Utils.isDigit(c)) {// 后跟任意多数字
//						word += c;
//						c = Utils.getChar();
//					}
//					int sysCode = SYMTable.getSysCode(word);
//					SYM.add(sysCode);
//					NUM.put(SYM.size() - 1, Integer.parseInt(word));
//				} else if (Utils.isLianYunsuan(c)) {// 连运算 运算符
//					word += c;
//					c = Utils.getChar();
//					if (c == ':') {
//						// System.out.println();
//					}
//					if (c == '=') {
//						word += c;
//					}
//					int sysCode = SYMTable.getSysCode(word);
//					SYM.add(sysCode);
//					if (c == '=') {
//						c = ' ';
//					}
//				} else if (Utils.isSuanFu(c)) {// 运算符
//
//					int sysCode = SYMTable.getSysCode(c + "");
//					SYM.add(sysCode);
//					c = ' ';
//				}
//
//			}
//		} catch (EOFException e) {
//			System.out.println("词法分析结束");
//		}
//		if (print) {
//			System.out.println("SYM表:" + SYM);
//			System.out.println("ID表:" + ID);
//			System.out.println("NUM表:" + NUM);
//		}
//	}
//
//	/**************************************** 递归下降的语法分析 ***************************************/
//	// 要求中TABLE
//	public static ArrayList<NameItem> nameTable = new ArrayList<>();
//
//	// 语法分析
//	public static void Block(boolean printNameTable) {
//		try {
//			SubProgram(0);
//
//		} catch (SyntaxError e) {
//			throw new RuntimeException(e);
//		}
//		if (advance == SYM.size()) {
//			System.out.println("语法分析，没错误");
//		} else {
//			System.out.println("语法分析，有错误");
//		}
//		if (printNameTable) {
//			for (NameItem item : nameTable) {
//				System.out.println(item);
//			}
//		}
//	}
//
//	// 当前遍历的SYM表位置
//	private static int advance = 0;
//
//	private static boolean isCurrentCode(String word) {
//		return SYM.get(advance) == SYMTable.getSysCode(word);
//	}
//
//	/**
//	 * 检查子程序
//	 * 
//	 * @param hasProc
//	 *            是不是含有过程声明,过程的子程序中不含有过程声明
//	 * @throws SyntaxError
//	 */
//	private static void SubProgram(int level) throws SyntaxError {
//		CONST_SM();
//		VAR_SM(level);
//		if (level < 3) {
//			PROC_SM(level + 1);
//		}
//		YUJU(level);
//	}
//
//	/**
//	 * 检查语句
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void YUJU(int level) throws SyntaxError {
//		// 赋值语句
//		// or 复合语句
//		// or 条件
//		// or 循环
//		// or 读
//		// or 写
//		// or 调用
//		try {
//			if (advance == SYM.size())
//				return;
//			FuZhi(level);
//		} catch (Exception e) {
//			try {
//				if (advance == SYM.size())
//					return;
//				FuHe(level);
//			} catch (Exception e1) {
//				if (advance == SYM.size())
//					return;
//				TiaoJianJieGou(level);
//				// TiaoJian();
//			}
//		}
//
//	}
//
//	/**
//	 * 检查条件结构
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void TiaoJianJieGou(int level) throws SyntaxError {
//		// if
//		// 条件：TiaoJian()
//		// then
//		// 语句：YUJU()
//		if (isCurrentCode("if")) {
//			advance++;
//			TiaoJian();
//			if (isCurrentCode("then")) {
//				advance++;
//				YUJU(level);
//			}
//		}
//	}
//
//	/**
//	 * 检查条件语句
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void TiaoJian() throws SyntaxError {
//		// 表达式
//		// 关系运算
//		// 表达式
//		BiaoDaShi();
//		if (isCurrentCode(">") || isCurrentCode(">=") || isCurrentCode("<") || isCurrentCode("<=")
//				|| isCurrentCode("==")) {
//			advance++;
//			BiaoDaShi();
//		} else {
//			throw new SyntaxError();
//		}
//
//	}
//
//	/**
//	 * 检查复合语句
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void FuHe(int level) throws SyntaxError {
//		// begin
//		// 语句
//		// 子复合
//		if (isCurrentCode("begin")) {
//			advance++;
//			YUJU(level);
//			SubFuHe(level);
//		} else {
//			throw new SyntaxError();
//		}
//	}
//
//	/**
//	 * 检查子复合
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void SubFuHe(int level) throws SyntaxError {
//		// ;
//		// 语句
//		// 子复合
//
//		// or end
//
//		if (isCurrentCode(";")) {
//			advance++;
//			YUJU(level);
//			SubFuHe(level);
//		} else if (isCurrentCode("end")) {
//			advance++;
//		} else {
//			throw new SyntaxError();
//		}
//	}
//
//	/**
//	 * 检查赋值语句
//	 * 
//	 */
//	private static void FuZhi(int level) throws SyntaxError {
//		// 标识符
//		// :=
//		// 表达式（woc？）
//		if (SYM.get(advance) == -1) {//
//			advance++;
//			if (isCurrentCode(":=")) {//
//				advance++;
//				BiaoDaShi();
//			} else {
//				throw new SyntaxError();
//			}
//			// System.out.println("赋值语句");
//		} else {
//			throw new SyntaxError();
//		}
//
//	}
//
//	/**
//	 * 检查表达式
//	 */
//	private static void BiaoDaShi() {
//		// 项
//		// 子表达式
//		Item();
//		BiaoDaShi_P();
//	}
//
//	/**
//	 * 检查子表达式
//	 */
//	private static void BiaoDaShi_P() {
//		// +|-
//		// 项
//		// 子表达式
//
//		// or 空
//
//		if (isCurrentCode("+") || isCurrentCode("-")) {
//			advance++;
//			Item();
//			BiaoDaShi_P();
//		}
//	}
//
//	/**
//	 * 判断项
//	 */
//	private static void Item() {
//		// 因子
//		// 子项
//
//		Factor();
//		SubItem();
//	}
//
//	/**
//	 * 判断子项
//	 */
//	private static void SubItem() {
//		// *|/
//		// 因子
//		// 子项
//
//		// or 空
//
//		if (isCurrentCode("*") || isCurrentCode("/")) {
//			advance++;
//			Factor();
//			SubItem();
//		}
//	}
//
//	/**
//	 * 判断因子
//	 */
//	private static void Factor() {
//		// 标识符
//		// or 数字
//		// or 表达式
//		int code = SYM.get(advance);
//		if (code == -1 || code == -2) {
//			advance++;
//		}
//	}
//
//	/**
//	 * 检查过程声明
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void PROC_SM(int level) throws SyntaxError {
//		// 声明头
//		// 子程序
//
//		PROC_SM_P(level);
//		SubProgram(level);
//	}
//
//	/**
//	 * 检查过程声明的首部
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void PROC_SM_P(int level) throws SyntaxError {
//		// procedure
//		// 标识符
//		// ;f
//		if (isCurrentCode("procedure")) {//
//			advance++;
//			if (SYM.get(advance) == -1) {//
//				advance++;
//				if (isCurrentCode(";")) {//
//					String pname = ID.get(advance - 1);
//					// System.out.println("produce :"+pname+"
//					// level:"+(level-1));
//					nameTable.add(new NameItem(pname, Kind.PROCEDURE, level - 1, ""));
//					advance++;
//				} else {
//					throw new SyntaxError();
//				}
//			} else {
//				throw new SyntaxError();
//			}
//		}
//	}
//
//	/**
//	 * 检查变量声明
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void VAR_SM(int level) throws SyntaxError {
//		// VAR
//		// SY
//		// VAR_SM_P()
//		// ;
//
//		if (SYM.get(advance) == SYMTable.getSysCode("var")) {// 检查是关键字 var
//			advance++;
//			if (SYM.get(advance) == -1) {// 检查是标识符
//				String varname = ID.get(advance);
//				// System.out.println("var name : "+varname+" level:"+level);
//				nameTable.add(new NameItem(varname, Kind.VARIABLE, level, 3 + ""));
//				advance++;
//				VAR_SM_P(level, 4);
//				if (SYM.get(advance) == SYMTable.getSysCode(";")) {// 检查是不是;结尾
//					advance++;
//				} else {
//					throw new SyntaxError();
//				}
//			} else {
//				throw new SyntaxError();
//			}
//		}
//	}
//
//	/**
//	 * 检查变量声明的子部
//	 * 
//	 */
//	private static void VAR_SM_P(int level, int ADR) throws SyntaxError {
//		// ,
//		// VAR_SM_P()
//
//		// 或空
//		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
//			advance++;
//			if (SYM.get(advance) == -1) {
//				String varname = ID.get(advance);
//				// System.out.println("var name : "+varname+" level:"+level);
//				nameTable.add(new NameItem(varname, Kind.VARIABLE, level, ADR + ""));
//				advance++;
//				VAR_SM_P(level, ADR + 1);
//			} else {
//				throw new SyntaxError();
//			}
//		}
//	}
//
//	/**
//	 * 检查常量声明
//	 * 
//	 */
//	private static void CONST_SM() throws SyntaxError {
//		// const
//		// CONST_DY()
//		// CONST_SM_P()
//		// ;
//		if (SYM.get(advance) == SYMTable.getSysCode("const")) {
//			advance++;
//			CONST_DY();
//			CONST_SM_P();
//			if (SYM.get(advance) == SYMTable.getSysCode(";")) {
//				advance++;
//			} else {
//				throw new SyntaxError();
//			}
//		}
//	}
//
//	/**
//	 * 检查常量的定义
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void CONST_DY() throws SyntaxError {
//		// SY
//		// =
//		// NUM
//
//		if (SYM.get(advance) == -1) {// 用户标识符
//			advance++;
//			if (SYM.get(advance) == SYMTable.getSysCode("=")) {// 等号
//				advance++;
//				if (SYM.get(advance) == -2) {// 数字
//					String constname = ID.get(advance - 2);
//					Integer value = NUM.get(advance);
//					// System.out.println(constname+" value:"+value);
//					nameTable.add(new NameItem(constname, Kind.CONSTANT, value, ""));
//					advance++;
//				} else {
//					throw new SyntaxError();
//				}
//			} else {
//				throw new SyntaxError();
//			}
//		} else {
//			throw new SyntaxError();
//		}
//	}
//
//	/**
//	 * 检查常量声明的子部
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void CONST_SM_P() throws SyntaxError {
//		// ,
//		// CONST_DY();
//		// CONST_SM_P();
//
//		// 或空
//
//		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
//			advance++;
//			CONST_DY();
//			CONST_SM_P();
//		}
//	}
//
//	/********************************* 语义分析和中间代码生成 *************************************/
//	public static void GEN() {
//
//		//SYM index
//		int index = 0;
//		
//		for (; index < SYM.size(); index++) {
//			
//			//SYM item
//			int item = SYM.get(index);
//			
//			if (item == -2) {
//				// 产生LIT
//			} else if (item == -1) {
//				//
//			}
//		}
//
//	}
//
//}
