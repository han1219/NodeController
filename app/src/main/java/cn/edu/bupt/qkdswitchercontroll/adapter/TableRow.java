package cn.edu.bupt.qkdswitchercontroll.adapter;

/**
 * TableRow 实现表格的行
 * 
 * @author hellogv
 */
public class TableRow {
	private TableCell[] cell;

	public TableRow(TableCell[] cell) {
		this.cell = cell;
	}

	public int getSize() {
		return cell.length;
	}

	public TableCell getCellValue(int index) {
		if (index >= cell.length)
			return null;
		return cell[index];
	}
}