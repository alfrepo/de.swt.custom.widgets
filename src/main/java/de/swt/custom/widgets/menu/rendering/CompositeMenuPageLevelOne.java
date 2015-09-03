package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CompositeMenuPageLevelOne extends AbstractCompositeMenuPage {

	public int MENU_ITEM_LVL_ONE_HEIGHT; //px

	public CompositeMenuPageLevelOne(Composite parent, int styleBits) {
		super(parent, styleBits, Style.get());
	}
	
	public CompositeMenuPageLevelOne(Composite parent, int styleBits, Style style) {
		super(parent, styleBits, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
	}
	
	@Override
	protected void initStyle(Style style) {
		super.initStyle(style);
		MENU_ITEM_LVL_ONE_HEIGHT = style.getMENUITEM_HEIGHT_LVL1();
	}
	
	public void layoutChild(Control child){
		this.layoutChild(child, MENU_ITEM_LVL_ONE_HEIGHT);
	}
	
	public void layoutChild(Control child, int height){
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.heightHint = height;
		gridData.minimumHeight = height;
		child.setLayoutData(gridData);
	}

}
