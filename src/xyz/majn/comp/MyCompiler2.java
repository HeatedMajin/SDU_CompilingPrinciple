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
//	// ÿ�����ʵ�����û��Զ��������Ϊ-1������Ϊ-2��ϵͳ����ļ�@xyz.majn.comp.SYMTable
//	public static LinkedList<Integer> SYM = new LinkedList<>();
//	// -1��SYM���е�λ��Ϊkey���û��Զ������������Ϊvalue
//	public static HashMap<Integer, String> ID = new HashMap<>();
//	// -2��SYM���е�λ��Ϊkey���û��Զ������������Ϊvalue
//	public static HashMap<Integer, Integer> NUM = new HashMap<>();
//
//	/************************************* �ʷ����� ***********************************************/
//	private static String word;
//
//	/**
//	 * ��ȡ�ַ���
//	 */
//	public static void getSym(boolean print) {
//		try {
//			char c = ' ';
//			while (true) {
//				word = "";
//				while (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
//					c = Utils.getChar();
//				}
//				if (Utils.isLetter(c)) {// ����ĸ��ͷ
//					word += c;
//					c = Utils.getChar();
//					while (Utils.isLetter(c) || Utils.isDigit(c)) {// �����������ĸ������
//						word += c;
//						c = Utils.getChar();
//					}
//					int sysCode = SYMTable.getSysCode(word);
//					SYM.add(sysCode);
//					if (sysCode == -1) {// �û���ʶ��
//						ID.put(SYM.size() - 1, word);
//					}
//				} else if (Utils.isDigit(c)) {// �����ֿ�ͷ
//					word += c;
//					c = Utils.getChar();
//					while (Utils.isDigit(c)) {// ������������
//						word += c;
//						c = Utils.getChar();
//					}
//					int sysCode = SYMTable.getSysCode(word);
//					SYM.add(sysCode);
//					NUM.put(SYM.size() - 1, Integer.parseInt(word));
//				} else if (Utils.isLianYunsuan(c)) {// ������ �����
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
//				} else if (Utils.isSuanFu(c)) {// �����
//
//					int sysCode = SYMTable.getSysCode(c + "");
//					SYM.add(sysCode);
//					c = ' ';
//				}
//
//			}
//		} catch (EOFException e) {
//			System.out.println("�ʷ���������");
//		}
//		if (print) {
//			System.out.println("SYM��:" + SYM);
//			System.out.println("ID��:" + ID);
//			System.out.println("NUM��:" + NUM);
//		}
//	}
//
//	/**************************************** �ݹ��½����﷨���� ***************************************/
//	// Ҫ����TABLE
//	public static ArrayList<NameItem> nameTable = new ArrayList<>();
//
//	// �﷨����
//	public static void Block(boolean printNameTable) {
//		try {
//			SubProgram(0);
//
//		} catch (SyntaxError e) {
//			throw new RuntimeException(e);
//		}
//		if (advance == SYM.size()) {
//			System.out.println("�﷨������û����");
//		} else {
//			System.out.println("�﷨�������д���");
//		}
//		if (printNameTable) {
//			for (NameItem item : nameTable) {
//				System.out.println(item);
//			}
//		}
//	}
//
//	// ��ǰ������SYM��λ��
//	private static int advance = 0;
//
//	private static boolean isCurrentCode(String word) {
//		return SYM.get(advance) == SYMTable.getSysCode(word);
//	}
//
//	/**
//	 * ����ӳ���
//	 * 
//	 * @param hasProc
//	 *            �ǲ��Ǻ��й�������,���̵��ӳ����в����й�������
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
//	 * ������
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void YUJU(int level) throws SyntaxError {
//		// ��ֵ���
//		// or �������
//		// or ����
//		// or ѭ��
//		// or ��
//		// or д
//		// or ����
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
//	 * ��������ṹ
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void TiaoJianJieGou(int level) throws SyntaxError {
//		// if
//		// ������TiaoJian()
//		// then
//		// ��䣺YUJU()
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
//	 * ����������
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void TiaoJian() throws SyntaxError {
//		// ���ʽ
//		// ��ϵ����
//		// ���ʽ
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
//	 * ��鸴�����
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void FuHe(int level) throws SyntaxError {
//		// begin
//		// ���
//		// �Ӹ���
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
//	 * ����Ӹ���
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void SubFuHe(int level) throws SyntaxError {
//		// ;
//		// ���
//		// �Ӹ���
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
//	 * ��鸳ֵ���
//	 * 
//	 */
//	private static void FuZhi(int level) throws SyntaxError {
//		// ��ʶ��
//		// :=
//		// ���ʽ��woc����
//		if (SYM.get(advance) == -1) {//
//			advance++;
//			if (isCurrentCode(":=")) {//
//				advance++;
//				BiaoDaShi();
//			} else {
//				throw new SyntaxError();
//			}
//			// System.out.println("��ֵ���");
//		} else {
//			throw new SyntaxError();
//		}
//
//	}
//
//	/**
//	 * �����ʽ
//	 */
//	private static void BiaoDaShi() {
//		// ��
//		// �ӱ��ʽ
//		Item();
//		BiaoDaShi_P();
//	}
//
//	/**
//	 * ����ӱ��ʽ
//	 */
//	private static void BiaoDaShi_P() {
//		// +|-
//		// ��
//		// �ӱ��ʽ
//
//		// or ��
//
//		if (isCurrentCode("+") || isCurrentCode("-")) {
//			advance++;
//			Item();
//			BiaoDaShi_P();
//		}
//	}
//
//	/**
//	 * �ж���
//	 */
//	private static void Item() {
//		// ����
//		// ����
//
//		Factor();
//		SubItem();
//	}
//
//	/**
//	 * �ж�����
//	 */
//	private static void SubItem() {
//		// *|/
//		// ����
//		// ����
//
//		// or ��
//
//		if (isCurrentCode("*") || isCurrentCode("/")) {
//			advance++;
//			Factor();
//			SubItem();
//		}
//	}
//
//	/**
//	 * �ж�����
//	 */
//	private static void Factor() {
//		// ��ʶ��
//		// or ����
//		// or ���ʽ
//		int code = SYM.get(advance);
//		if (code == -1 || code == -2) {
//			advance++;
//		}
//	}
//
//	/**
//	 * ����������
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void PROC_SM(int level) throws SyntaxError {
//		// ����ͷ
//		// �ӳ���
//
//		PROC_SM_P(level);
//		SubProgram(level);
//	}
//
//	/**
//	 * �������������ײ�
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void PROC_SM_P(int level) throws SyntaxError {
//		// procedure
//		// ��ʶ��
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
//	 * ����������
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void VAR_SM(int level) throws SyntaxError {
//		// VAR
//		// SY
//		// VAR_SM_P()
//		// ;
//
//		if (SYM.get(advance) == SYMTable.getSysCode("var")) {// ����ǹؼ��� var
//			advance++;
//			if (SYM.get(advance) == -1) {// ����Ǳ�ʶ��
//				String varname = ID.get(advance);
//				// System.out.println("var name : "+varname+" level:"+level);
//				nameTable.add(new NameItem(varname, Kind.VARIABLE, level, 3 + ""));
//				advance++;
//				VAR_SM_P(level, 4);
//				if (SYM.get(advance) == SYMTable.getSysCode(";")) {// ����ǲ���;��β
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
//	 * �������������Ӳ�
//	 * 
//	 */
//	private static void VAR_SM_P(int level, int ADR) throws SyntaxError {
//		// ,
//		// VAR_SM_P()
//
//		// ���
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
//	 * ��鳣������
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
//	 * ��鳣���Ķ���
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void CONST_DY() throws SyntaxError {
//		// SY
//		// =
//		// NUM
//
//		if (SYM.get(advance) == -1) {// �û���ʶ��
//			advance++;
//			if (SYM.get(advance) == SYMTable.getSysCode("=")) {// �Ⱥ�
//				advance++;
//				if (SYM.get(advance) == -2) {// ����
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
//	 * ��鳣���������Ӳ�
//	 * 
//	 * @throws SyntaxError
//	 */
//	private static void CONST_SM_P() throws SyntaxError {
//		// ,
//		// CONST_DY();
//		// CONST_SM_P();
//
//		// ���
//
//		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
//			advance++;
//			CONST_DY();
//			CONST_SM_P();
//		}
//	}
//
//	/********************************* ����������м�������� *************************************/
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
//				// ����LIT
//			} else if (item == -1) {
//				//
//			}
//		}
//
//	}
//
//}
