package xyz.majn.comp;

/**
 * �м�����һ�����룺 f�������� I����β� a��λ����
 * 
 * Ŀ��ָ����8����
 * LIT��������ջ����aΪ����
 * LOD��������ջ����a���ڲ��е����λ�ã�I���ú�˵���Ĳ�β
 * STO����ջ���������͵�ĳ������Ԫ�С�a,l��ĺ�����LOD����ͬ��
 * CAL�����ù��̵�ָ�aΪ�����ù��̵�Ŀ���������е�ַ��lΪ��
 * INT��Ϊ�����õĹ��̣���������������ջ�п�����������a��Ϊ���ٵĸ�����
 * JMP����������ת��aΪת���ַ��
 * JPC����������ת��ջ������ֵΪ�����桱ת��a������˳��ִ�С�
 * OPR����ϵ���������㡣���������a�������������Ϊջ���ʹ�ջ����������ڴ�ջ����aΪ0ʱ�ǣ��˳���������
 * 
 * @author majin
 *
 */
public class CodeBean {
	private int f;
	private int i;
	private int a;

	public CodeBean(int f, int i, int a) {
		super();
		this.f = f;
		this.i = i;
		this.a = a;
	}

	public CodeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

}
