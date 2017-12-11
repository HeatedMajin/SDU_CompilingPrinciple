package xyz.majn.comp;

import xyz.majin.utils.NameKind;
import xyz.majin.utils.OPKind;

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
	private OPKind f;
	private int i;
	private int a;

	public CodeBean(OPKind f, int i, int a) {
		super();
		this.f = f;
		this.i = i;
		this.a = a;
	}
	
	public String toString() {
		String res = "";
		if(this.f == OPKind.CAL){
			res += "CAL";
		}else if(this.f == OPKind.INT){
			res += "INT";
		}else if(this.f == OPKind.JMP){
			res += "JMP";
		}else if(this.f == OPKind.JPC){
			res += "JPC";
		}else if(this.f == OPKind.LIT){
			res += "LIT";
		}else if(this.f == OPKind.LOD){
			res += "LOD";
		}else if(this.f == OPKind.OPR){
			res += "OPR";
		}else if(this.f == OPKind.STO){
			res += "STO";
		}
		
		res+="\t"+i+"\t"+a;
		return res;
	}

	public OPKind getF() {
		return f;
	}

	public void setF(OPKind f) {
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
