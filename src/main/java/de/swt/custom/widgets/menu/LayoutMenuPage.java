package de.swt.custom.widgets.menu;

import org.eclipse.swt.widgets.Composite;

public abstract class LayoutMenuPage {

	public int itemHeight;
	public int pagePadding;
	public int itemSpacing;
	
	abstract void layoutMenuPage(MenuPage page, Composite pageComposite);
	
	abstract void layoutMenuItemInPage(MenuItem item, Composite itemComposite);
}
