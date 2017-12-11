package xyz.majn.comp;

import xyz.majin.utils.NameKind;
import xyz.majin.utils.OPKind;

/**
 * 中间代码的一条代码： f：功能码 I：层次差 a：位移量
 * 
 * 目标指令有8条：
 * LIT：常数进栈顶，a为常数
 * LOD：变量放栈顶。a所在层中的相对位置，I调用和说明的层次差。
 * STO：将栈顶的内容送到某变量单元中。a,l域的含义与LOD的相同。
 * CAL：调用过程的指令。a为被调用过程的目标程序的入中地址，l为层差。
 * INT：为被调用的过程（或主程序）在运行栈中开辟数据区。a域为开辟的个数。
 * JMP：无条件跳转，a为转向地址。
 * JPC：有条件跳转，栈顶布尔值为“非真”转向a，否则顺序执行。
 * OPR：关系和算数运算。具体操作有a给出。运算对象为栈顶和次栈顶。结果放在次栈顶。a为0时是，退出数据区。
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
