package xyz.majin.utils;

//保存一个常量的name，kind，value
//变量名的name，kind，level，addR
//过程名的name，kind，level
public class NameItem {
	private String name;
	private NameKind kind;
	private int value;// 常量值或者变量的level
	private String ADR;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NameKind getKind() {
		return kind;
	}

	public void setKind(NameKind kind) {
		this.kind = kind;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public NameItem(String name, NameKind kind, int value,String ADR) {
		super();
		this.name = name;
		this.kind = kind;
		this.value = value;
		this.ADR = ADR;
	}

	public NameItem() {
		super();
	}

	@Override
	public String toString() {
		String type = "var";
		String valueType = "层数:";
		if(this.kind == NameKind.CONSTANT){
			type="const";
			valueType = "值  :";
		}else if(this.kind == NameKind.PROCEDURE){
			type= "produce";
			valueType = "层数:";
		}
		String res ="name:"+name+"\t\tkind:"+type+"\t\t"+valueType+value+"\t\tADR:"+ADR;
		return res;
	}

	public String getADR() {
		return ADR;
	}

	public void setADR(String aDR) {
		ADR = aDR;
	}
}
