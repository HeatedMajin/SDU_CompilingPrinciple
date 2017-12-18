package xyz.majn.comp;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import xyz.majin.excep.ChangeConstError;
import xyz.majin.excep.NameNotExist;
import xyz.majin.excep.SyntaxError;
import xyz.majin.utils.NameItem;
import xyz.majin.utils.NameKind;
import xyz.majin.utils.OPKind;
import xyz.majin.utils.Utils;
import xyz.majin.utils.YunSuanTable;

public class MyCompiler {
	// 每个单词的类别，用户自定义变量名为-1，常数为-2，系统定义的见@xyz.majn.comp.SYMTable
	public static LinkedList<Integer> SYM = new LinkedList<>();
	// -1在SYM表中的位置为key，用户自定义变量的名字为value
	public static HashMap<Integer, String> ID = new HashMap<>();
	// -2在SYM表中的位置为key，常量值为value
	public static HashMap<Integer, Integer> NUM = new HashMap<>();

	// SYM的String格式
	public static LinkedList<String> SYMSTR = new LinkedList<>();
	/************************************* 词法分析 ***********************************************/
	private static String word;

	/**
	 * 获取字符表
	 */
	public static void getSym(boolean print) {
		try {
			char c = ' ';
			while (true) {
				word = "";
				while (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
					c = Utils.getChar();
				}
				if (Utils.isLetter(c)) {// 以字母开头
					word += c;
					c = Utils.getChar();
					while (Utils.isLetter(c) || Utils.isDigit(c)) {// 后跟任意多个字母和数字
						word += c;
						c = Utils.getChar();
					}
					int sysCode = SYMTable.getSysCode(word);
					SYM.add(sysCode);
					if (sysCode == -1) {// 用户标识符
						ID.put(SYM.size() - 1, word);
					} else if (sysCode > 0) {// 系统标志符
						SYMSTR.add(word.toUpperCase() + "SYM");
					}
				} else if (Utils.isDigit(c)) {// 以数字开头
					word += c;
					c = Utils.getChar();
					while (Utils.isDigit(c)) {// 后跟任意多数字
						word += c;
						c = Utils.getChar();
					}
					int sysCode = SYMTable.getSysCode(word);
					SYM.add(sysCode);
					NUM.put(SYM.size() - 1, Integer.parseInt(word));
				} else if (Utils.isLianYunsuan(c)) {// 连运算 运算符
					word += c;
					c = Utils.getChar();
					if (c == ':') {
						// System.out.println();
					}
					if (c == '=') {
						word += c;
					}
					int sysCode = SYMTable.getSysCode(word);
					SYM.add(sysCode);
					SYMSTR.add(word);
					if (c == '=') {
						c = ' ';
					}
				} else if (Utils.isSuanFu(c)) {// 运算符

					int sysCode = SYMTable.getSysCode(c + "");
					SYM.add(sysCode);
					SYMSTR.add(c + "");
					c = ' ';
				}

			}
		} catch (EOFException e) {
			System.out.println("词法分析结束");
		}
		if (print) {
			System.out.println("SYM\t\t ID\t NUM\t\t");
			int SYMSTRIndex = 0;
			for (int i = 0; i < SYM.size(); i++) {
				int item = SYM.get(i);

				if (item == -1) {// 标识符
					System.out.println("IDENT\t\t" + ID.get(i) + "\t" + "\t");
				} else if (item == -2) {
					System.out.println("NUMBER\t\t\t" + NUM.get(i) + "\t");
				} else {
					System.out.println(SYMSTR.get(SYMSTRIndex) + "\t");
					SYMSTRIndex++;
				}
			}
			/*
			 * System.out.println(SYMSTR); System.out.println("SYM表:" + SYM);
			 * System.out.println("ID表:" + ID); System.out.println("NUM表:" +
			 * NUM);
			 */
		}
	}

	/**************************************** 递归下降的语法分析 ***************************************/
	// 要求中TABLE
	public static ArrayList<NameItem> nameTable = new ArrayList<>();
	// 记录每一层的位置 ；第i个元素表示第i层在table的位置
	public static ArrayList<Integer> levelIndex = new ArrayList<>();
	static {
		levelIndex.add(0);
	}

	// 语法分析
	public static void Block(boolean printNameTable) {
		try {
			SubProgram(0);

		} catch (SyntaxError e) {
			throw new RuntimeException(e);
		}
		if (advance == SYM.size()) {
			System.out.println("语法分析结束，没错误");
		} else {
			System.out.println("语法分析结束，有错误");
		}
		if (printNameTable) {
			for (int i = 0; i < nameTable.size(); i++) {
				if (levelIndex.contains(i)) {
					System.out.println(levelIndex.indexOf(i) + "\t" + nameTable.get(i));
				} else {
					System.out.println("\t" + nameTable.get(i));
				}
			}

		}
	}

	// 当前遍历的SYM表位置
	private static int advance = 0;

	private static boolean isCurrentCode(String word) {
		return SYM.get(advance) == SYMTable.getSysCode(word);
	}

	/**
	 * 检查子程序
	 * 
	 * @param hasProc
	 *            是不是含有过程声明,过程的子程序中不含有过程声明
	 * @throws SyntaxError
	 */
	private static void SubProgram(int level) throws SyntaxError {
		if (level == 0) {

			CONST_SM();
		}
		VAR_SM(level);

		if (level < 3) {
			PROC_SM(level + 1);
		}

		// 获取当前层所有的的变量数，开辟栈
		/////// 进入子程序，开辟栈///////////

		// 根据level获取 所在层声明变量的数量
		int levelStart = levelIndex.get(level);

		// 当前层的结束位置
		int levelEnd;
		if (level == levelIndex.size() - 1) {
			levelEnd = nameTable.size();
		} else {
			levelEnd = levelIndex.get(level + 1);
		}
		// 记录变量的数量
		int count = 0;
		for (int i = levelStart; i < levelEnd; i++) {
			if (nameTable.get(i).getKind() == NameKind.VARIABLE) {
				count++;
			}
		}
		int codes_start = codes.size();
		procs.add(codes_start);

		CodeBean cBean = new CodeBean(OPKind.INT, 0, count + 3);
		codes.add(cBean);
		// cBean.setA();

		YUJU(level);

		/////// 程序结束，退出栈/////////////
		codes.add(new CodeBean(OPKind.OPR, 0, 0));
	}

	/**
	 * 检查语句
	 * 
	 * @throws SyntaxError
	 */
	private static boolean YUJU(int level) throws SyntaxError {
		// 赋值语句
		// or 复合语句
		// or 条件
		// or 循环
		// or 读
		// or 写
		// or 调用

		boolean res = false;
		if (advance != SYM.size() && FuZhi(level)) {
			res = true;
		} else if (advance != SYM.size() && FuHe(level)) {
			res = true;
		} else if (advance != SYM.size() && TiaoJianJieGou(level)) {
			res = true;
		} else if (advance != SYM.size() && CallJieGou(level)) {
			res = true;
		} else if (advance != SYM.size() && Xunhuan(level)) {
			res = true;
		} else if (advance != SYM.size() && readJiegou(level)) {
			res = true;
		} else if (advance != SYM.size() && writeJiegou(level)) {
			res = true;
		}
		if (advance == SYM.size()) {
			return true;
		}
		if (!isCurrentCode(";")) {
			res = false;
		} else {
			advance++;
		}
		return res;
	}

	private static boolean writeJiegou(int level) {
		// write
		// 表达式
		if (isCurrentCode("write")) {
			advance++;
			if (BiaoDaShi(level)) {// 标识符
				////////////////////////////////// 写结构 //////////////////////////////////

				codes.add(new CodeBean(OPKind.OPR, 0, 15));// write
				System.out.println("写结构");
				return true;

			}
		}
		return false;
	}

	private static boolean readJiegou(int level) {
		// read
		// 标识符
		if (isCurrentCode("read")) {
			advance++;
			if (SYM.get(advance) == -1) {// 标识符
				int var_index = advance;
				advance++;

				////////////////////////////////// 读结构
				////////////////////////////////// //////////////////////////////////
				// 找到标识符
				int res[] = null;
				try {
					res = levelDiff(level, ID.get(var_index));
				} catch (NameNotExist e) {// 变量名不存在
					System.out.println(e.getMessage());
				}
				if (res != null && res[1] == -1) {
					System.out.println("无法给常量赋值");
				}

				codes.add(new CodeBean(OPKind.OPR, 0, 16));// read
				codes.add(new CodeBean(OPKind.STO, res[0], res[1]));// STO

				System.out.println("读结构");
				return true;

			}
		}
		return false;
	}

	/**
	 * 检查循环结构
	 * 
	 * @throws SyntaxError
	 */
	private static boolean Xunhuan(int level) throws SyntaxError {
		// while
		// 表达式
		// do
		// 语句
		if (isCurrentCode("while")) {
			int code_start = codes.size();

			advance++;
			if (TiaoJianBiaoDaShi(level)) {// 标识符

				// 错误跳转出去
				CodeBean outCode = new CodeBean(OPKind.JPC, 0, 0);
				codes.add(outCode);

				if (isCurrentCode("do")) {
					advance++;
					if (FuHe(level)) {
						// 条回到while的开始
						CodeBean whileCode = new CodeBean(OPKind.JMP, 0, code_start);
						codes.add(whileCode);

						outCode.setA(codes.size());
						return true;
					}
				}

			}
		}
		return false;
	}

	/**
	 * 检查调用结构
	 * 
	 * @throws SyntaxError
	 */
	private static boolean CallJieGou(int level) throws SyntaxError {
		// call
		// 过程名
		if (isCurrentCode("call")) {
			advance++;
			if (SYM.get(advance) == -1) {// 标识符
				int p_name_index = advance;
				advance++;
				int res[] = null;
				try {
					res = levelDiff(level, ID.get(p_name_index));
				} catch (NameNotExist e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CodeBean cbean = new CodeBean(OPKind.CAL, 0, level-res[0]);
				codes.add(cbean);
				System.out.println("call结构");
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查条件结构
	 * 
	 * @throws SyntaxError
	 */
	private static boolean TiaoJianJieGou(int level) throws SyntaxError {
		// if
		// 条件：TiaoJianBiaoDaShi()
		// then
		// 语句：YUJU()
		if (isCurrentCode("if")) {
			advance++;
			TiaoJianBiaoDaShi(level);
			if (isCurrentCode("then")) {
				advance++;
				YUJU(level);
				return true;
			}
		}
		return false;
	}



	/**
	 * 检查复合语句
	 * 
	 * @throws SyntaxError
	 */
	private static boolean FuHe(int level) throws SyntaxError {
		// begin
		// 语句
		// 子复合
		if (isCurrentCode("begin")) {
			advance++;
			if(YUJU(level)){
				return SubFuHe(level);
			}
		}
		return false;
	}

	/**
	 * 检查子复合
	 * 
	 * @throws SyntaxError
	 */
	private static boolean SubFuHe(int level) throws SyntaxError {
		// 语句
		// 子复合

		// or end

		if (YUJU(level)) {
			SubFuHe(level);
			return true;
		} else if (isCurrentCode("end")) {
			advance++;
			return true;
		}
		return false;
	}

	/**
	 * 检查赋值语句
	 * 
	 */
	private static boolean FuZhi(int level) throws SyntaxError {
		// 标识符
		// :=
		// 表达式

		if (SYM.get(advance) == -1) {//
			int a = advance;
			advance++;
			if (isCurrentCode(":=")) {//
				advance++;
				if (BiaoDaShi(level)) {

					//////////////////// 保存赋值运算结果 ///////////////////////
					int[] res;
					try {
						res = levelDiff(level, ID.get(a));
					} catch (NameNotExist e) {
						System.out.println(e.getMessage());
						throw new RuntimeException(e);
					}
					if (res[1] == -1) {// "不能给常量赋值"
						System.out.println("不能给常量赋值");
						throw new RuntimeException(new ChangeConstError());
					}
					// 产生STO命令
					codes.add(new CodeBean(OPKind.STO, res[0], res[1]));
					return true;
				}
				advance--;
			}
			advance--;
		}
		return false;
	}

	/**
	 * 检查表达式
	 */
	private static boolean BiaoDaShi(int level) {
		// 项
		// 子表达式

		if (Item(level) && BiaoDaShi_P(level)) {
			return true;
		}
		return false;
		// throw new SyntaxError();
	}

	/**
	 * 检查条件表达式
	 */
	private static boolean TiaoJianBiaoDaShi(int level) {
		// 标识符 或 数字
		// 比较运算
		// 标识符 或 数字
		if (SYM.get(advance) == -1 || SYM.get(advance) == -2) {// 标识符、数字
			if (SYM.get(advance) == -1) {// lod 标识符
				int res[] = null;
				try {
					res = levelDiff(level, ID.get(advance));
				} catch (NameNotExist e) {
					System.out.println("名字不存在");
					throw new RuntimeException(e);
				}
				if (res[1] != -1) {
					codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
				} else {
					codes.add(new CodeBean(OPKind.LIT, res[0], res[2]));
				}
			} else {// lit 常数
				codes.add(new CodeBean(OPKind.LIT, 0, NUM.get(advance)));
			}

			// int var_index = advance;
			advance++;
			if (SYMTable.BiJiaoFu(SYM.get(advance))) {
				advance++;
				if (SYM.get(advance) == -1 || SYM.get(advance) == -2) {// 标识符、数字
					if (SYM.get(advance) == -1) {// lod 标识符
						int res[] = null;
						try {
							res = levelDiff(level, ID.get(advance));
						} catch (NameNotExist e) {
							System.out.println("名字不存在");
							throw new RuntimeException(e);
						}

						codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
					} else {// lit 常数
						codes.add(new CodeBean(OPKind.LIT, 0, NUM.get(advance)));
					}

					advance++;

					codes.add(new CodeBean(OPKind.OPR, 0, 9));
					System.out.println("条件表达式");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查子表达式
	 */
	private static boolean BiaoDaShi_P(int level) {
		// +|-
		// 项
		// 子表达式

		// or 空

		if (isCurrentCode("+")) {
			advance++;
			Item(level);
			BiaoDaShi_P(level);
			codes.add(new CodeBean(OPKind.OPR, 0, YunSuanTable.getOPR_a("+")));
			return true;
		} else if (isCurrentCode("-")) {
			advance++;
			Item(level);
			BiaoDaShi_P(level);
			codes.add(new CodeBean(OPKind.OPR, 0, YunSuanTable.getOPR_a("-")));
			return true;
		} else if (isCurrentCode(";")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断项
	 */
	private static boolean Item(int level) {
		// 因子
		// 子项

		if (Factor(level) && SubItem(level)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断子项
	 */
	private static boolean SubItem(int level) {
		// *|/
		// 因子
		// 子项

		// or 空

		if (isCurrentCode("*") || isCurrentCode("/")) {
			CodeBean codeBean = null;
			if(isCurrentCode("*")) {
				codeBean = new CodeBean(OPKind.OPR, 0, YunSuanTable.getOPR_a("*"));
			}else{
				codeBean = new CodeBean(OPKind.OPR, 0, YunSuanTable.getOPR_a("/"));
			}
			advance++;
			if (Factor(level) && SubItem(level)) {
				codes.add(codeBean);
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断因子
	 */
	private static boolean Factor(int level) {
		// 标识符
		// or 数字
		// or 表达式

		int code = SYM.get(advance);
		if (code == -1) {
			advance++;

			int[] res;
			try {
				res = levelDiff(level, ID.get(advance - 1));
			} catch (NameNotExist e) {
				System.out.println(e.getMessage());
				return false;
			}
			if (res[1] == -1) {
				// 产生LIT: LIT,0,常数
				codes.add(new CodeBean(OPKind.LIT, 0, res[2]));
			} else {
				// 产生LOD: LOD,层差,层内偏移
				codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
			}

			return true;
		} else if (code == -2) {

			advance++;

			int[] res;
			try {
				res = levelDiff(level, ID.get(advance - 1));
			} catch (NameNotExist e) {
				System.out.println(e.getMessage());
				return false;
			}
			if (res[1] == -1) {
				// 产生LIT: LIT,0,常数
				codes.add(new CodeBean(OPKind.LIT, 0, res[2]));
			} else {
				// 产生LOD: LOD,层差,层内偏移
				codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
			}

			return true;
		}
		return false;
	}

	/**
	 * 检查过程声明
	 * 
	 * @throws SyntaxError
	 */
	private static void PROC_SM(int level) throws SyntaxError {
		// 声明头
		// 子程序

		if (PROC_SM_P(level)) {

			SubProgram(level);

		}
	}

	/**
	 * 检查过程声明的首部
	 * 
	 * @throws SyntaxError
	 */
	private static boolean PROC_SM_P(int level) throws SyntaxError {
		// procedure
		// 标识符
		// ;f
		if (isCurrentCode("procedure")) {//
			advance++;
			if (SYM.get(advance) == -1) {//
				advance++;
				if (isCurrentCode(";")) {//
					String pname = ID.get(advance - 1);
					// System.out.println("produce :"+pname+"
					// level:"+(level-1));
					nameTable.add(new NameItem(pname, NameKind.PROCEDURE, level - 1, ""));

					//////////////////////// 这一层说明层的起始地址//////////////////////////////////////
					int cIndex = nameTable.size();
					// int cLevel = levelIndex.size();
					levelIndex.add(cIndex);
					advance++;
					return true;
				} else {
					throw new SyntaxError();
				}
			} else {
				throw new SyntaxError();
			}
		}
		return false;
	}

	/**
	 * 检查变量声明
	 * 
	 * @throws SyntaxError
	 */
	private static void VAR_SM(int level) throws SyntaxError {
		// VAR
		// SY
		// VAR_SM_P()
		// ;

		if (SYM.get(advance) == SYMTable.getSysCode("var")) {// 检查是关键字 var
			advance++;
			if (SYM.get(advance) == -1) {// 检查是标识符
				String varname = ID.get(advance);
				// System.out.println("var name : "+varname+" level:"+level);
				nameTable.add(new NameItem(varname, NameKind.VARIABLE, level, 3 + ""));
				advance++;
				VAR_SM_P(level, 4);
				if (SYM.get(advance) == SYMTable.getSysCode(";")) {// 检查是不是;结尾
					advance++;
				} else {
					throw new SyntaxError();
				}
			} else {
				throw new SyntaxError();
			}
		}
	}

	/**
	 * 检查变量声明的子部
	 * 
	 */
	private static void VAR_SM_P(int level, int ADR) throws SyntaxError {
		// ,
		// VAR_SM_P()

		// 或空
		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
			advance++;
			if (SYM.get(advance) == -1) {
				String varname = ID.get(advance);
				// System.out.println("var name : "+varname+" level:"+level);
				nameTable.add(new NameItem(varname, NameKind.VARIABLE, level, ADR + ""));
				advance++;
				VAR_SM_P(level, ADR + 1);
			} else {
				throw new SyntaxError();
			}
		}
	}

	/**
	 * 检查常量声明
	 * 
	 */
	private static void CONST_SM() throws SyntaxError {
		// const
		// CONST_DY()
		// CONST_SM_P()
		// ;
		if (SYM.get(advance) == SYMTable.getSysCode("const")) {
			advance++;
			CONST_DY();
			CONST_SM_P();
			if (SYM.get(advance) == SYMTable.getSysCode(";")) {
				advance++;
			} else {
				throw new SyntaxError();
			}
		}
	}

	/**
	 * 检查常量的定义
	 * 
	 * @throws SyntaxError
	 */
	private static void CONST_DY() throws SyntaxError {
		// SY
		// =
		// NUM

		if (SYM.get(advance) == -1) {// 用户标识符
			advance++;
			if (SYM.get(advance) == SYMTable.getSysCode("=")) {// 等号
				advance++;
				if (SYM.get(advance) == -2) {// 数字
					String constname = ID.get(advance - 2);
					Integer value = NUM.get(advance);
					// System.out.println(constname+" value:"+value);
					nameTable.add(new NameItem(constname, NameKind.CONSTANT, value, ""));
					advance++;
				} else {
					throw new SyntaxError();
				}
			} else {
				throw new SyntaxError();
			}
		} else {
			throw new SyntaxError();
		}
	}

	/**
	 * 检查常量声明的子部
	 * 
	 * @throws SyntaxError
	 */
	private static void CONST_SM_P() throws SyntaxError {
		// ,
		// CONST_DY();
		// CONST_SM_P();

		// 或空

		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
			advance++;
			CONST_DY();
			CONST_SM_P();
		}
	}

	/********************************* 语义分析和中间代码生成 *************************************/
	// 代码区
	private static LinkedList<CodeBean> codes = new LinkedList<>();
	// 代码的分段,记录每一个procs的开始位置
	private static LinkedList<Integer> procs = new LinkedList<>();

	public static void GEN(boolean print) {

		if (print) {
			int index = 0;
			for (CodeBean i : codes) {
				System.out.println(index++ + ":\t" + i);
			}
		}
	}

	/**
	 * 查找变量的位置 去name表中找，层差，曾内偏移(!!!+3)
	 * 
	 * 
	 * @param varName
	 *            变量名
	 * @param codelevel
	 *            当前代码在第几层
	 * @return new int[3]
	 * 
	 *         0:说明和调用的层差,
	 * 
	 *         1:说明层内偏移，常量返回-1
	 * 
	 *         2:常量返回其值（变量不要用）
	 * @throws NameNotExist
	 *             变量名不存在报出异常
	 */
	private static int[] levelDiff(int codelevel, String varName) throws NameNotExist {
		int res[] = null;

		// 变量在第几层,从代码所在的层 往0层找，(最近的优先)
		for (int i = codelevel; i >= 0; i--) {
			// System.out.println(varName+"-"+codelevel);
			// 查找层i起始位置
			int levelStart = levelIndex.get(i);
			// 查找层i的结束位置
			int levelEnd;
			if (i + 1 > levelIndex.size() - 1) {
				levelEnd = nameTable.size() - 1;
			} else {
				levelEnd = levelIndex.get(i + 1) - 1;
			}

			for (int ii = levelStart; ii <= levelEnd; ii++) {
				if (ii <= nameTable.size() - 1 && varName.equals(nameTable.get(ii).getName())) {
					res = new int[3];
					res[0] = codelevel - i;

					NameItem nameItem = nameTable.get(ii);
					switch (nameItem.getKind()) {
					case CONSTANT:
						res[1] = -1;// 常量
						res[2] = nameTable.get(ii).getValue();
						break;
					case PROCEDURE:
						res[1] = -2;// 过程
						break;
					case VARIABLE:
						res[1] = Integer.parseInt(nameItem.getADR());// 变量
						break;
					}
					return res;
				}
			}
		}
		if (res == null) {
			throw new NameNotExist(varName + ":是未声明的变量");
		}
		return res;
	}

	/************************************* 运行程序 ***********************************************/
	// 数据区
	private static LinkedList<Integer> datas = new LinkedList<>();

	// 数据区的
	// private static LinkedList<Integer> data_index = new LinkedList<>();
	public static void RUN(boolean print) {
		// 一定是执行最后一个proc
		int main_addr = procs.getLast();
		// System.out.println(main_addr);

		int pc = main_addr;// 当前执行的指令
		int sp = 0;// 栈底指针
		int top = -1;// 栈顶指针
		int tmp = pc;

		// 当pc被置成-1时，表明程序退出
		while (pc != -1) {
			CodeBean ccode = codes.get(pc);
			pc++;

			int a = ccode.getA();
			int i = ccode.getI();

			int leveldiff = i;
			int leveloffset = a;

			int p_sp = sp;// 最终的sp的位置
			while (leveldiff > 0) {
				p_sp = datas.get(p_sp);
				leveldiff--;
			}
			int data_index = p_sp + leveloffset;

			switch (ccode.getF()) {
			case CAL:
				// 找到那个程序
				int p = procs.get(a);
				tmp = pc;
				pc = p;
				break;
			case INT:
				// 0:sp指向老sp
				// 1:返回地址
				// 2:全局display
				// 3:display
				datas.add(sp);
				sp = top+1;
				datas.add(tmp);
				datas.add(0);// ????
				int mm_num = ccode.getA();
				while (mm_num - 3 > 0) {
					datas.add(0);
					mm_num--;
				}
				top = datas.size() - 1;

				break;
			case JMP:
				pc = a;
				break;
			case JPC:
				int res = datas.removeLast();
				if (res == 0) {
					// 跳向a
					pc = a;
				}
				break;
			case LIT:// 常数为a
				datas.add(a);
				break;
			case LOD:
				datas.add(datas.get(data_index));
				break;
			case OPR:

				switch (a) {
				case 2://加法
					int asecond = datas.removeLast();
					int afirst = datas.removeLast();
					datas.add(afirst+asecond);
					break;
				case 3://减法
					int ssecond = datas.removeLast();
					int sfirst = datas.removeLast();
					datas.add(sfirst-ssecond);
					break;
				case 4://乘法
					int msecond = datas.removeLast();
					int mfirst = datas.removeLast();
					datas.add(mfirst*msecond);
					break;
				case 5://除法
					int dsecond = datas.removeLast();
					int dfirst = datas.removeLast();
					datas.add(dfirst/dsecond);
					break;
				case 15:// 输出
					System.out.println(datas.removeLast());
					break;
				case 16:// 读取键盘输入
					datas.add(scan.nextInt());
					break;

				case 9:// 比较<
					int second = datas.removeLast();
					int first = datas.removeLast();
					if (first < second) {
						datas.add(1);
					} else {
						datas.add(0);
					}
					break;
				case 0:// 退出栈
					int old_sp = datas.get(sp);
					int old_pc = datas.get(sp + 1);

					top = sp;
					sp = old_sp;
					pc = old_pc;

					while (datas.size() > 0 && datas.size() > top) {
						datas.removeLast();
					}
					if (datas.size() == 0) {
						pc = -1;
					}
					
					System.out.println(datas);
					break;
				}
				break;
			case STO:

				datas.set(data_index, datas.getLast());
				datas.removeLast();
				// System.out.println(datas);
				break;
			}
			top = datas.size()-1;
		}
		//System.out.println(procs);

	}

	private static Scanner scan = new Scanner(System.in);
}
