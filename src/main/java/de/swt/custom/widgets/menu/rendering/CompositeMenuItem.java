package de.swt.custom.widgets.menu.rendering;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class CompositeMenuItem extends AbstractCompositeMenuItem {
	
	public Label lblIcon;
	public Label lblText;
	public Label lblIconHasSubpages;
	
	public int MENU_ITEM_DEFAULT_HEIGHT; //px
	public int MENU_ITEM_ICON_SIZE; //px
	public Font MENU_ITEM_FONT;

	protected void initStyle(Style style) {
		MENU_ITEM_DEFAULT_HEIGHT = style.getMENUITEM_HEIGHT_LVL1(); //px
		MENU_ITEM_ICON_SIZE = style.getMENUITEM_ICON_SIZE_LVL1(); //px
		MENU_ITEM_FONT = style.getMENUITEM_FONT_LVL1();
	}
	
	public CompositeMenuItem(Composite parent, int styleBits, Style style) {
		super(parent, styleBits);
		
		initStyle(style);
		
		setLayout(new GridLayout(3, false));
		
		lblIcon = new Label(this, SWT.NONE);
//		lblIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		GridData gd_lblIcon = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd_lblIcon.heightHint = MENU_ITEM_ICON_SIZE;
		gd_lblIcon.widthHint = MENU_ITEM_ICON_SIZE;
		lblIcon.setLayoutData(gd_lblIcon);
		
		lblText = new Label(this, SWT.NONE);
		lblText.setFont(MENU_ITEM_FONT);
		lblText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		lblText.setText("Text");
		
		lblIconHasSubpages = new Label(this, SWT.NONE);
		lblIconHasSubpages.setFont(MENU_ITEM_FONT);
		lblIconHasSubpages.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblIconHasSubpages.setImage(ImageDescriptor.createFromURL(CompositeMenuItem.class.getResource("view_menu.gif")).createImage());
		lblIconHasSubpages.setText("");
		
		init();
	}
	

	/**
	 * May override this to modify 
	 */
	protected void modifyVars(){
		// change variables here
	}
	
	private void init(){
		// hide arrow initially
		setVisibleLblHasSubpages(false);
	}
	
	public void setVisibleLblHasSubpages(boolean isVisible){
		GridData gridData = (GridData) lblIconHasSubpages.getLayoutData();
		gridData.exclude = !isVisible;
		lblIconHasSubpages.setLayoutData(gridData);
	}
	
	public void setIcon(Image image){
		lblIcon.setImage(image);
	}
	
	public void setText(String name){
		lblText.setText(name);
	}
}
