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
	// ÿ�����ʵ�����û��Զ��������Ϊ-1������Ϊ-2��ϵͳ����ļ�@xyz.majn.comp.SYMTable
	public static LinkedList<Integer> SYM = new LinkedList<>();
	// -1��SYM���е�λ��Ϊkey���û��Զ������������Ϊvalue
	public static HashMap<Integer, String> ID = new HashMap<>();
	// -2��SYM���е�λ��Ϊkey������ֵΪvalue
	public static HashMap<Integer, Integer> NUM = new HashMap<>();

	// SYM��String��ʽ
	public static LinkedList<String> SYMSTR = new LinkedList<>();
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
					} else if (sysCode > 0) {// ϵͳ��־��
						SYMSTR.add(word.toUpperCase() + "SYM");
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
					SYMSTR.add(word);
					if (c == '=') {
						c = ' ';
					}
				} else if (Utils.isSuanFu(c)) {// �����

					int sysCode = SYMTable.getSysCode(c + "");
					SYM.add(sysCode);
					SYMSTR.add(c + "");
					c = ' ';
				}

			}
		} catch (EOFException e) {
			System.out.println("�ʷ���������");
		}
		if (print) {
			System.out.println("SYM\t\t ID\t NUM\t\t");
			int SYMSTRIndex = 0;
			for (int i = 0; i < SYM.size(); i++) {
				int item = SYM.get(i);

				if (item == -1) {// ��ʶ��
					System.out.println("IDENT\t\t" + ID.get(i) + "\t" + "\t");
				} else if (item == -2) {
					System.out.println("NUMBER\t\t\t" + NUM.get(i) + "\t");
				} else {
					System.out.println(SYMSTR.get(SYMSTRIndex) + "\t");
					SYMSTRIndex++;
				}
			}
			/*
			 * System.out.println(SYMSTR); System.out.println("SYM��:" + SYM);
			 * System.out.println("ID��:" + ID); System.out.println("NUM��:" +
			 * NUM);
			 */
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
			System.out.println("�﷨����������û����");
		} else {
			System.out.println("�﷨�����������д���");
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

		// ��ȡ��ǰ�����еĵı�����������ջ
		/////// �����ӳ��򣬿���ջ///////////

		// ����level��ȡ ���ڲ���������������
		int levelStart = levelIndex.get(level);

		// ��ǰ��Ľ���λ��
		int levelEnd;
		if (level == levelIndex.size() - 1) {
			levelEnd = nameTable.size();
		} else {
			levelEnd = levelIndex.get(level + 1);
		}
		// ��¼����������
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

		/////// ����������˳�ջ/////////////
		codes.add(new CodeBean(OPKind.OPR, 0, 0));
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
		// ���ʽ
		if (isCurrentCode("write")) {
			advance++;
			if (BiaoDaShi(level)) {// ��ʶ��
				////////////////////////////////// д�ṹ //////////////////////////////////

				codes.add(new CodeBean(OPKind.OPR, 0, 15));// write
				System.out.println("д�ṹ");
				return true;

			}
		}
		return false;
	}

	private static boolean readJiegou(int level) {
		// read
		// ��ʶ��
		if (isCurrentCode("read")) {
			advance++;
			if (SYM.get(advance) == -1) {// ��ʶ��
				int var_index = advance;
				advance++;

				////////////////////////////////// ���ṹ
				////////////////////////////////// //////////////////////////////////
				// �ҵ���ʶ��
				int res[] = null;
				try {
					res = levelDiff(level, ID.get(var_index));
				} catch (NameNotExist e) {// ������������
					System.out.println(e.getMessage());
				}
				if (res != null && res[1] == -1) {
					System.out.println("�޷���������ֵ");
				}

				codes.add(new CodeBean(OPKind.OPR, 0, 16));// read
				codes.add(new CodeBean(OPKind.STO, res[0], res[1]));// STO

				System.out.println("���ṹ");
				return true;

			}
		}
		return false;
	}

	/**
	 * ���ѭ���ṹ
	 * 
	 * @throws SyntaxError
	 */
	private static boolean Xunhuan(int level) throws SyntaxError {
		// while
		// ���ʽ
		// do
		// ���
		if (isCurrentCode("while")) {
			int code_start = codes.size();

			advance++;
			if (TiaoJianBiaoDaShi(level)) {// ��ʶ��

				// ������ת��ȥ
				CodeBean outCode = new CodeBean(OPKind.JPC, 0, 0);
				codes.add(outCode);

				if (isCurrentCode("do")) {
					advance++;
					if (FuHe(level)) {
						// ���ص�while�Ŀ�ʼ
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
				System.out.println("call�ṹ");
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
		// ������TiaoJianBiaoDaShi()
		// then
		// ��䣺YUJU()
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
			if(YUJU(level)){
				return SubFuHe(level);
			}
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
			int a = advance;
			advance++;
			if (isCurrentCode(":=")) {//
				advance++;
				if (BiaoDaShi(level)) {

					//////////////////// ���渳ֵ������ ///////////////////////
					int[] res;
					try {
						res = levelDiff(level, ID.get(a));
					} catch (NameNotExist e) {
						System.out.println(e.getMessage());
						throw new RuntimeException(e);
					}
					if (res[1] == -1) {// "���ܸ�������ֵ"
						System.out.println("���ܸ�������ֵ");
						throw new RuntimeException(new ChangeConstError());
					}
					// ����STO����
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
	 * �����ʽ
	 */
	private static boolean BiaoDaShi(int level) {
		// ��
		// �ӱ��ʽ

		if (Item(level) && BiaoDaShi_P(level)) {
			return true;
		}
		return false;
		// throw new SyntaxError();
	}

	/**
	 * ����������ʽ
	 */
	private static boolean TiaoJianBiaoDaShi(int level) {
		// ��ʶ�� �� ����
		// �Ƚ�����
		// ��ʶ�� �� ����
		if (SYM.get(advance) == -1 || SYM.get(advance) == -2) {// ��ʶ��������
			if (SYM.get(advance) == -1) {// lod ��ʶ��
				int res[] = null;
				try {
					res = levelDiff(level, ID.get(advance));
				} catch (NameNotExist e) {
					System.out.println("���ֲ�����");
					throw new RuntimeException(e);
				}
				if (res[1] != -1) {
					codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
				} else {
					codes.add(new CodeBean(OPKind.LIT, res[0], res[2]));
				}
			} else {// lit ����
				codes.add(new CodeBean(OPKind.LIT, 0, NUM.get(advance)));
			}

			// int var_index = advance;
			advance++;
			if (SYMTable.BiJiaoFu(SYM.get(advance))) {
				advance++;
				if (SYM.get(advance) == -1 || SYM.get(advance) == -2) {// ��ʶ��������
					if (SYM.get(advance) == -1) {// lod ��ʶ��
						int res[] = null;
						try {
							res = levelDiff(level, ID.get(advance));
						} catch (NameNotExist e) {
							System.out.println("���ֲ�����");
							throw new RuntimeException(e);
						}

						codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
					} else {// lit ����
						codes.add(new CodeBean(OPKind.LIT, 0, NUM.get(advance)));
					}

					advance++;

					codes.add(new CodeBean(OPKind.OPR, 0, 9));
					System.out.println("�������ʽ");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ����ӱ��ʽ
	 */
	private static boolean BiaoDaShi_P(int level) {
		// +|-
		// ��
		// �ӱ��ʽ

		// or ��

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
	 * �ж���
	 */
	private static boolean Item(int level) {
		// ����
		// ����

		if (Factor(level) && SubItem(level)) {
			return true;
		}
		return false;
	}

	/**
	 * �ж�����
	 */
	private static boolean SubItem(int level) {
		// *|/
		// ����
		// ����

		// or ��

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
	 * �ж�����
	 */
	private static boolean Factor(int level) {
		// ��ʶ��
		// or ����
		// or ���ʽ

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
				// ����LIT: LIT,0,����
				codes.add(new CodeBean(OPKind.LIT, 0, res[2]));
			} else {
				// ����LOD: LOD,���,����ƫ��
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
				// ����LIT: LIT,0,����
				codes.add(new CodeBean(OPKind.LIT, 0, res[2]));
			} else {
				// ����LOD: LOD,���,����ƫ��
				codes.add(new CodeBean(OPKind.LOD, res[0], res[1]));
			}

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

					//////////////////////// ��һ��˵�������ʼ��ַ//////////////////////////////////////
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
	// ������
	private static LinkedList<CodeBean> codes = new LinkedList<>();
	// ����ķֶ�,��¼ÿһ��procs�Ŀ�ʼλ��
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
	 * ���ұ�����λ�� ȥname�����ң�������ƫ��(!!!+3)
	 * 
	 * 
	 * @param varName
	 *            ������
	 * @param codelevel
	 *            ��ǰ�����ڵڼ���
	 * @return new int[3]
	 * 
	 *         0:˵���͵��õĲ��,
	 * 
	 *         1:˵������ƫ�ƣ���������-1
	 * 
	 *         2:����������ֵ��������Ҫ�ã�
	 * @throws NameNotExist
	 *             �����������ڱ����쳣
	 */
	private static int[] levelDiff(int codelevel, String varName) throws NameNotExist {
		int res[] = null;

		// �����ڵڼ���,�Ӵ������ڵĲ� ��0���ң�(���������)
		for (int i = codelevel; i >= 0; i--) {
			// System.out.println(varName+"-"+codelevel);
			// ���Ҳ�i��ʼλ��
			int levelStart = levelIndex.get(i);
			// ���Ҳ�i�Ľ���λ��
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
						res[1] = -1;// ����
						res[2] = nameTable.get(ii).getValue();
						break;
					case PROCEDURE:
						res[1] = -2;// ����
						break;
					case VARIABLE:
						res[1] = Integer.parseInt(nameItem.getADR());// ����
						break;
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

	/************************************* ���г��� ***********************************************/
	// ������
	private static LinkedList<Integer> datas = new LinkedList<>();

	// ��������
	// private static LinkedList<Integer> data_index = new LinkedList<>();
	public static void RUN(boolean print) {
		// һ����ִ�����һ��proc
		int main_addr = procs.getLast();
		// System.out.println(main_addr);

		int pc = main_addr;// ��ǰִ�е�ָ��
		int sp = 0;// ջ��ָ��
		int top = -1;// ջ��ָ��
		int tmp = pc;

		// ��pc���ó�-1ʱ�����������˳�
		while (pc != -1) {
			CodeBean ccode = codes.get(pc);
			pc++;

			int a = ccode.getA();
			int i = ccode.getI();

			int leveldiff = i;
			int leveloffset = a;

			int p_sp = sp;// ���յ�sp��λ��
			while (leveldiff > 0) {
				p_sp = datas.get(p_sp);
				leveldiff--;
			}
			int data_index = p_sp + leveloffset;

			switch (ccode.getF()) {
			case CAL:
				// �ҵ��Ǹ�����
				int p = procs.get(a);
				tmp = pc;
				pc = p;
				break;
			case INT:
				// 0:spָ����sp
				// 1:���ص�ַ
				// 2:ȫ��display
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
					// ����a
					pc = a;
				}
				break;
			case LIT:// ����Ϊa
				datas.add(a);
				break;
			case LOD:
				datas.add(datas.get(data_index));
				break;
			case OPR:

				switch (a) {
				case 2://�ӷ�
					int asecond = datas.removeLast();
					int afirst = datas.removeLast();
					datas.add(afirst+asecond);
					break;
				case 3://����
					int ssecond = datas.removeLast();
					int sfirst = datas.removeLast();
					datas.add(sfirst-ssecond);
					break;
				case 4://�˷�
					int msecond = datas.removeLast();
					int mfirst = datas.removeLast();
					datas.add(mfirst*msecond);
					break;
				case 5://����
					int dsecond = datas.removeLast();
					int dfirst = datas.removeLast();
					datas.add(dfirst/dsecond);
					break;
				case 15:// ���
					System.out.println(datas.removeLast());
					break;
				case 16:// ��ȡ��������
					datas.add(scan.nextInt());
					break;

				case 9:// �Ƚ�<
					int second = datas.removeLast();
					int first = datas.removeLast();
					if (first < second) {
						datas.add(1);
					} else {
						datas.add(0);
					}
					break;
				case 0:// �˳�ջ
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
