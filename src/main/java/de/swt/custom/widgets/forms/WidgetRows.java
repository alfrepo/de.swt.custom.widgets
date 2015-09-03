package de.swt.custom.widgets.forms;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Its a layout widget. 
 * It creates rows, which fill space horizontally, 
 * distribute the vertical space equally between Rows.
 *  
 * @author alf
 *
 */
public class WidgetRows extends WidgetWithEquallyDistributedSpace {
	private static final int DEFAULT_ROW_NUM = 1;

	public WidgetRows(Composite parent, int style) {
		this(parent, style, DEFAULT_ROW_NUM);
	}
	
	public WidgetRows(Composite parent, int style, int rowNumbers) {
		this(parent, style, rowNumbers, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.PACK);
	}

	public WidgetRows(Composite parent, int style, int rowNumbers, GRIP_CONSTRAINTS horizontal, GRIP_CONSTRAINTS vertical) {
		super(parent, style, horizontal, vertical, SWT.VERTICAL);
		for(int i=0; i<rowNumbers; i++){
			addRow();
		}
	}
	
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
	}
	
	@Override
	protected void setHeight(int height) {
		super.setHeight(height);
	}
	
	/**
	 * All Rows
	 * @return
	 */
	public List<Composite> getRows(){
		return getChildrenAsComposites();
	}
	
	/**
	 * If the row was not explicetly layed out, 
	 * then the layout is as specified by {@link #setupNewRowLayout(Composite)
	 * 
	 * @return
	 */
	public Composite getRow(int position){
		return getChildAsComposite(position);
	}
	
	/**
	 * Creates a Row and adds it on the right
	 * @return - the new Row
	 */
	public Composite addRow(){
		Composite row = addChild();
		setupNewRowLayout(row);
		return row;
	}
	
	/**
	 * The default layout is the {@link FillLayout}
	 * @param row
	 */
	public void setupNewRowLayout(Composite row) {
		row.setLayout(new FillLayout());
	}

	/**
	 * removes last Row
	 */
	public void removeRow(){
		removeRow(getChildren().length-1);
	}
	
	/**
	 * Removes the Row on the given position
	 * @param position
	 */
	public void removeRow(int position){
		removeChild(position);
	}


}
