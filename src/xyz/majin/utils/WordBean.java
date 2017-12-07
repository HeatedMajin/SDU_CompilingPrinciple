package xyz.majin.utils;

/**
 * 包含这个词的一系列属性：SYM,ID,NUM 。
 * 
 * @author majin
 *
 */
public class WordBean {
	private String sym;
	private String num;
	private String id;
	public WordBean(String sym,String id, String num) {
		super();
		this.sym = sym;
		this.num = num;
		this.id = id;
	}
	public String getSym() {
		return sym;
	}
	public void setSym(String sym) {
		this.sym = sym;
	}
	
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	
	public WordBean() {
		super();
	}

	@Override
	public String toString() {
		return sym+"  "+ id +"  "+num;
	}
	
}
