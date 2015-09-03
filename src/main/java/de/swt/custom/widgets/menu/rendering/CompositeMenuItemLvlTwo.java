package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.widgets.Composite;

public class CompositeMenuItemLvlTwo extends CompositeMenuItem{

	@Override
	protected void modifyVars() {
	}
	
	public CompositeMenuItemLvlTwo(Composite parent, int styleBits, Style style) {
		super(parent, styleBits, style);
	}

	//change the style
	@Override
	protected void initStyle(Style style) {
		MENU_ITEM_FONT = style.getMENUITEM_FONT_LVL2();
		MENU_ITEM_DEFAULT_HEIGHT = style.getMENUITEM_HEIGHT_LVL2();
		MENU_ITEM_ICON_SIZE = style.getMENUITEM_ICON_SIZE_LVL2();
	}
}
