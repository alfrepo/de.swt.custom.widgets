package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class CompositeMenuGroupLevelTwo extends AbstractCompositeMenuGroup {

	public static final int MENU_GROUP_ICON_SIZE = 20; //px
	
	public Label lblIcon;
	public Label lblGroupTitle;

	public CompositeMenuGroupLevelTwo(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblIcon = new Label(this, SWT.NONE);
		GridData gd_lblIcon = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblIcon.widthHint = MENU_GROUP_ICON_SIZE;
		gd_lblIcon.heightHint = MENU_GROUP_ICON_SIZE;
		gd_lblIcon.minimumWidth = MENU_GROUP_ICON_SIZE;
		gd_lblIcon.minimumHeight = MENU_GROUP_ICON_SIZE;
		lblIcon.setLayoutData(gd_lblIcon);
		lblIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		
		lblGroupTitle = new Label(this, SWT.NONE);
		lblGroupTitle.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		lblGroupTitle.setText("Group Title");
	}
	
	public void layoutChild(Control child, int height){
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.heightHint = height;
		gridData.minimumHeight = height;
		child.setLayoutData(gridData);
	}
	
	public CompositeMenuGroupLevelTwo setGroupName(String name){
		this.lblGroupTitle.setText(name);
		return this;
	}
	
	public CompositeMenuGroupLevelTwo setIcon(Image icon){
		this.lblIcon.setImage(icon);
		return this;
	}

}
