package xyz.majn.comp;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import xyz.majin.excep.NameNotExist;
import xyz.majin.excep.SyntaxError;
import xyz.majin.utils.NameItem;
import xyz.majin.utils.NameKind;
import xyz.majin.utils.OPKind;
import xyz.majin.utils.Utils;

public class MyCompiler {
	// ÿ�����ʵ�����û��Զ��������Ϊ-1������Ϊ-2��ϵͳ����ļ�@xyz.majn.comp.SYMTable
	public static LinkedList<Integer> SYM = new LinkedList<>();
	// -1��SYM���е�λ��Ϊkey���û��Զ������������Ϊvalue
	public static HashMap<Integer, String> ID = new HashMap<>();
	// -2��SYM���е�λ��Ϊkey���û��Զ������������Ϊvalue
	public static HashMap<Integer, Integer> NUM = new HashMap<>();

	/************************************* �ʷ����� ***********************************************/
	private static String word;

	/**
	 * ��ȡ�ַ���
	 */
	public static void getSym(boolean print) {
		try {
			char c = ' ';
			while (true) {
				word = "";
				while (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
					c = Utils.getChar();
				}
				if (Utils.isLetter(c)) {// ����ĸ��ͷ
					word += c;
					c = Utils.getChar();
					while (Utils.isLetter(c) || Utils.isDigit(c)) {// �����������ĸ������
						word += c;
						c = Utils.getChar();
					}
					int sysCode = SYMTable.getSysCode(word);
					SYM.add(sysCode);
					if (sysCode == -1) {// �û���ʶ��
						ID.put(SYM.size() - 1, word);
					}
				} else if (Utils.isDigit(c)) {// �����ֿ�ͷ
					word += c;
					c = Utils.getChar();
					while (Utils.isDigit(c)) {// ������������
						word += c;
						c = Utils.getChar();
					}
					int sysCode = SYMTable.getSysCode(word);
					SYM.add(sysCode);
					NUM.put(SYM.size() - 1, Integer.parseInt(word));
				} else if (Utils.isLianYunsuan(c)) {// ������ �����
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
					if (c == '=') {
						c = ' ';
					}
				} else if (Utils.isSuanFu(c)) {// �����

					int sysCode = SYMTable.getSysCode(c + "");
					SYM.add(sysCode);
					c = ' ';
				}

			}
		} catch (EOFException e) {
			System.out.println("�ʷ���������");
		}
		if (print) {
			System.out.println("SYM��:" + SYM);
			System.out.println("ID��:" + ID);
			System.out.println("NUM��:" + NUM);
		}
	}

	/**************************************** �ݹ��½����﷨���� ***************************************/
	// Ҫ����TABLE
	public static ArrayList<NameItem> nameTable = new ArrayList<>();
	// ��¼ÿһ���λ�� ����i��Ԫ�ر�ʾ��i����table��λ��
	public static ArrayList<Integer> levelIndex = new ArrayList<>();
	static {
		levelIndex.add(0);
	}

	// �﷨����
	public static void Block(boolean printNameTable) {
		try {
			SubProgram(0);

		} catch (SyntaxError e) {
			throw new RuntimeException(e);
		}
		if (advance == SYM.size()) {
			System.out.println("�﷨������û����");
		} else {
			System.out.println("�﷨�������д���");
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

	// ��ǰ������SYM��λ��
	private static int advance = 0;

	private static boolean isCurrentCode(String word) {
		return SYM.get(advance) == SYMTable.getSysCode(word);
	}

	/**
	 * ����ӳ���
	 * 
	 * @param hasProc
	 *            �ǲ��Ǻ��й�������,���̵��ӳ����в����й�������
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
		YUJU(level);
	}

	/**
	 * ������
	 * 
	 * @throws SyntaxError
	 */
	private static boolean YUJU(int level) throws SyntaxError {
		// ��ֵ���
		// or �������
		// or ����
		// or ѭ��
		// or ��
		// or д
		// or ����

		boolean res = false;
		if (advance != SYM.size() && FuZhi(level)) {
			res = true;
		} else if (advance != SYM.size() && FuHe(level)) {
			res = true;
		} else if (advance != SYM.size() && TiaoJianJieGou(level)) {
			res = true;
		} else if (advance != SYM.size() && CallJieGou(level)) {
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

	/**
	 * �����ýṹ
	 * 
	 * @throws SyntaxError
	 */
	private static boolean CallJieGou(int level) throws SyntaxError {
		// call
		// ������
		if (isCurrentCode("call")) {
			advance++;
			if (SYM.get(advance) == -1) {// ��ʶ��
				advance++;
				return true;
			}
		}
		return false;
	}

	/**
	 * ��������ṹ
	 * 
	 * @throws SyntaxError
	 */
	private static boolean TiaoJianJieGou(int level) throws SyntaxError {
		// if
		// ������TiaoJian()
		// then
		// ��䣺YUJU()
		if (isCurrentCode("if")) {
			advance++;
			TiaoJian();
			if (isCurrentCode("then")) {
				advance++;
				YUJU(level);
				return true;
			}
		}
		return false;
	}

	/**
	 * ����������
	 * 
	 * @throws SyntaxError
	 */
	private static boolean TiaoJian() throws SyntaxError {
		// ���ʽ
		// ��ϵ����
		// ���ʽ
		BiaoDaShi();
		if (isCurrentCode(">") || isCurrentCode(">=") || isCurrentCode("<") || isCurrentCode("<=")
				|| isCurrentCode("==")) {
			advance++;
			BiaoDaShi();
			return false;
		}
		return true;
	}

	/**
	 * ��鸴�����
	 * 
	 * @throws SyntaxError
	 */
	private static boolean FuHe(int level) throws SyntaxError {
		// begin
		// ���
		// �Ӹ���
		if (isCurrentCode("begin")) {
			advance++;
			YUJU(level);
			SubFuHe(level);
			return true;
		}
		return false;
	}

	/**
	 * ����Ӹ���
	 * 
	 * @throws SyntaxError
	 */
	private static boolean SubFuHe(int level) throws SyntaxError {
		// ���
		// �Ӹ���

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
	 * ��鸳ֵ���
	 * 
	 */
	private static boolean FuZhi(int level) throws SyntaxError {
		// ��ʶ��
		// :=
		// ���ʽ
		
		if (SYM.get(advance) == -1) {//
			advance++;
			if (isCurrentCode(":=")) {//
				advance++;
				if (BiaoDaShi()) {
//					System.out.println("��ֵ���");
					return true;
				}
				advance--;
			}
			advance--;
		}
		return false;
	}

	/**
	 * �����ʽ
	 */
	private static boolean BiaoDaShi() {
		// ��
		// �ӱ��ʽ

		if (Item() && BiaoDaShi_P()) {
			return true;
		}
		return false;
		// throw new SyntaxError();
	}

	/**
	 * ����ӱ��ʽ
	 */
	private static boolean BiaoDaShi_P() {
		// +|-
		// ��
		// �ӱ��ʽ

		// or ��

		if (isCurrentCode("+") || isCurrentCode("-")) {
			advance++;
			Item();
			BiaoDaShi_P();
			return true;
		}
		return false;
	}

	/**
	 * �ж���
	 */
	private static boolean Item() {
		// ����
		// ����

		if (Factor() && SubItem()) {
			return true;
		}
		return false;
	}

	/**
	 * �ж�����
	 */
	private static boolean SubItem() {
		// *|/
		// ����
		// ����

		// or ��

		if (isCurrentCode("*") || isCurrentCode("/")) {
			advance++;
			if (Factor() && SubItem()) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * �ж�����
	 */
	private static boolean Factor() {
		// ��ʶ��
		// or ����
		// or ���ʽ

		int code = SYM.get(advance);
		if (code == -1 || code == -2) {
			advance++;
			return true;
		}
		return false;
	}

	/**
	 * ����������
	 * 
	 * @throws SyntaxError
	 */
	private static void PROC_SM(int level) throws SyntaxError {
		// ����ͷ
		// �ӳ���

		if (PROC_SM_P(level)) {
			SubProgram(level);
		}
	}

	/**
	 * �������������ײ�
	 * 
	 * @throws SyntaxError
	 */
	private static boolean PROC_SM_P(int level) throws SyntaxError {
		// procedure
		// ��ʶ��
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
	 * ����������
	 * 
	 * @throws SyntaxError
	 */
	private static void VAR_SM(int level) throws SyntaxError {
		// VAR
		// SY
		// VAR_SM_P()
		// ;

		if (SYM.get(advance) == SYMTable.getSysCode("var")) {// ����ǹؼ��� var
			advance++;
			if (SYM.get(advance) == -1) {// ����Ǳ�ʶ��
				String varname = ID.get(advance);
				// System.out.println("var name : "+varname+" level:"+level);
				nameTable.add(new NameItem(varname, NameKind.VARIABLE, level, 3 + ""));
				advance++;
				VAR_SM_P(level, 4);
				if (SYM.get(advance) == SYMTable.getSysCode(";")) {// ����ǲ���;��β
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
	 * �������������Ӳ�
	 * 
	 */
	private static void VAR_SM_P(int level, int ADR) throws SyntaxError {
		// ,
		// VAR_SM_P()

		// ���
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
	 * ��鳣������
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
	 * ��鳣���Ķ���
	 * 
	 * @throws SyntaxError
	 */
	private static void CONST_DY() throws SyntaxError {
		// SY
		// =
		// NUM

		if (SYM.get(advance) == -1) {// �û���ʶ��
			advance++;
			if (SYM.get(advance) == SYMTable.getSysCode("=")) {// �Ⱥ�
				advance++;
				if (SYM.get(advance) == -2) {// ����
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
	 * ��鳣���������Ӳ�
	 * 
	 * @throws SyntaxError
	 */
	private static void CONST_SM_P() throws SyntaxError {
		// ,
		// CONST_DY();
		// CONST_SM_P();

		// ���

		if (SYM.get(advance) == SYMTable.getSysCode(",")) {
			advance++;
			CONST_DY();
			CONST_SM_P();
		}
	}

	/********************************* ����������м�������� *************************************/
	private static LinkedList<CodeBean> codes = new LinkedList<>();

	public static void GEN(boolean print) {

		// SYM index
		int index = 0;

		// code level
		int codeLevel = 0;

		boolean start = false;

		for (; index < SYM.size(); index++) {

			// SYM item
			int item = SYM.get(index);
			if (item == SYMTable.getSysCode("begin")) {
				start = true;
				// ���Ҵ���Ĳ�����һ
				codeLevel++;
			}
			if (!start) {
				continue;
			}
			if (item == -1) {

				//��һ�����ز��ڶ������ز���ƫ��
				//����û�в���ƫ�ƣ��ڶ�������-1�����������س���ֵ
				int[] res;
				try {
					res = levelDiff(codeLevel, ID.get(index));
				} catch (NameNotExist e) {
					System.out.println(e.getMessage());
					return;
				}
				if (res[1] == -1) {
					// ����LIT: LIT,0,����
					codes.add(new CodeBean(OPKind.LIT, 0, res[2]));
				} else {
					// ����LOD: LOD,���,����ƫ��
					codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
				}
				// } else if (item == SYMTable.getSysCode(":=")) {
				// // ����STO
				//
				// }else if(item == SYMTable.getSysCode("CAL")){
				// //����CAL

			} else if (item == SYMTable.getSysCode("procedure")) {
				// ����INT

				// }else if(item == SYMTable.getSysCode("")){
				// //����JPC
				//
				// }else if(item == SYMTable.getSysCode("")){
				// //����JMP
				//
				// }else if(item == SYMTable.getSysCode("")){
				// //����OPR

			} else if (item == SYMTable.getSysCode("end")) {
				// �����������һ
				codeLevel--;
				start = false;
			}
		}
		if (print) {
			for (CodeBean i : codes) {
				System.out.println(i);
			}
		}
	}

	/**
	 * ȥname�����ң�������ƫ��(!!!+3)
	 * 
	 * @param varName
	 *            ������
	 * @param codelevel
	 *            ��ǰ�����ڵڼ���
	 * @return ˵���͵��õĲ��,˵������ƫ��
	 * @throws NameNotExist
	 *             �����������ڱ����쳣
	 */
	private static int[] levelDiff(int codelevel, String varName) throws NameNotExist {
		int res[] = null;

		// �����ڵڼ���,�Ӵ������ڵĲ� ��0���ң�(���������)
		for (int i = codelevel; i >= 0; i--) {

			// ��ǰ����ʼλ��
			int levelStart = levelIndex.get(i);
			// ��ǰ��Ľ���λ��
			int levelEnd;
			if (codelevel == levelIndex.size() - 1) {
				levelEnd = nameTable.size();
			} else {
				levelEnd = levelIndex.get(i + 1);
			}

			for (int ii = levelStart; ii < levelEnd; ii++) {
				if (varName.equals(nameTable.get(ii).getName())) {
					res = new int[3];
					res[0] = codelevel - i;
					String adr = nameTable.get(ii).getADR();

					
					if (!adr.trim().equals("")) {
						res[1] = Integer.parseInt(adr);//����
					}else{
						res[1] = -1;//����
						res[2] = nameTable.get(ii).getValue();
					}
					return res;
				}
			}
		}
		if (res == null) {
			throw new NameNotExist(varName + ":��δ�����ı���");
		}
		return res;
	}
}
