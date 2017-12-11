package xyz.majin.utils;

//����һ��������name��kind��value
//��������name��kind��level��addR
//��������name��kind��level
public class NameItem {
	private String name;
	private NameKind kind;
	private int value;// ����ֵ���߱�����level
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
		String valueType = "����:";
		if(this.kind == NameKind.CONSTANT){
			type="const";
			valueType = "ֵ  :";
		}else if(this.kind == NameKind.PROCEDURE){
			type= "produce";
			valueType = "����:";
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
