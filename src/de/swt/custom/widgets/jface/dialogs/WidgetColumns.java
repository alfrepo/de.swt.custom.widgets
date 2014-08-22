package de.swt.custom.widgets.jface.dialogs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class WidgetColumns extends WidgetWithEquallyDistributedSpace {
	private static final int DEFAULT_COLUMN_NUM = 1;

	public WidgetColumns(Composite parent, int style) {
		this(parent, style, DEFAULT_COLUMN_NUM);
	}
	
	public WidgetColumns(Composite parent, int style, int columnNumbers) {
		this(parent, style, columnNumbers, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.FILL);
	}

	public WidgetColumns(Composite parent, int style, int columnNumbers, GRIP_CONSTRAINTS horizontal, GRIP_CONSTRAINTS vertical) {
		super(parent, style, horizontal, vertical, SWT.HORIZONTAL);
		for(int i=0; i<columnNumbers; i++){
			addColumn();
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
	 * All Columns
	 * @return
	 */
	public List<Composite> getColumns(){
		return getChildrenAsComposites();
	}
	
	/**
	 * Retrieves a column.
	 * If the column was not explicetely layed out, 
	 * then the layout is as specified by {@link #setupLayoutOfNewColumn(Composite)}
	 * 
	 * @param position
	 * @return
	 */
	public Composite getColumn(int position){
		return getChildAsComposite(position);
	}
	
	/**
	 * Creates a column and adds it on the right
	 * @return - the new column
	 */
	public Composite addColumn(){
		Composite column = addChild(); 
		setupLayoutOfNewColumn(column);
		return column;
	}
	


	/**
	 * removes last column
	 */
	public void removeColumn(){
		removeColumn(getChildren().length-1);
	}
	
	/**
	 * Removes the column on the given position
	 * @param position
	 */
	public void removeColumn(int position){
		removeChild(position);
	}

	
	/**
	 * You may define default layout for new columns by overridng this method.
	 * On default it's a {@link FillLayout}
	 * @param column
	 */
	public void setupLayoutOfNewColumn(Composite column) {
		// no default layout
	}

}
