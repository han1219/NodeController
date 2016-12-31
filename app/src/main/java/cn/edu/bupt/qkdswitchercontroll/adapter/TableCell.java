package cn.edu.bupt.qkdswitchercontroll.adapter;

/**
 * TableCell 实现表格的格单元
 * 
 * @author hellogv
 */
  
 public class TableCell {
	static public final int STRING = 0;
	static public final int IMAGE = 1;
	public Object value;
	public int width;
	public int height;
	public int type;

	public TableCell(Object value,int type) {
		this.value = value;
		this.type = type;
	}

}