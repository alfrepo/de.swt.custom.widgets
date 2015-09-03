package de.swt.custom.widgets.menu;

import org.eclipse.swt.widgets.Composite;

public abstract class LayoutMenuItem {

	public int itemPadding;
	
	abstract void layoutMenuItem(MenuItem item, Composite menuItem);
	
}
