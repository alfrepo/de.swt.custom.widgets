package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractCompositeMenuPage extends Composite{

	protected Color MENU_PAGE_COLOR;
	
	public AbstractCompositeMenuPage(Composite parent, int styleBits) {
		this(parent, styleBits, Style.get());
	}
	
	public AbstractCompositeMenuPage(Composite parent, int styleBits, Style style) {
		super(parent, styleBits);
		initStyle(style);
		this.setBackground(MENU_PAGE_COLOR);
	}
	
	protected void initStyle(Style style) {
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		MENU_PAGE_COLOR = style.getMENU_PAGE_COLOR_LVL1();
	}
	
	/**
	 * Override, if another composite than this  (a child) should be used as parent for children
	 * @return - the parent for children
	 */
	public Composite getClientAreaComposite(){
		return this;
	};
	
	/**
	 * Implement to add LayoutData to the child, depending on what Layout is ued on page.
	 * @param child
	 */
	abstract void layoutChild(Control child);
	
	/**
	 * Implement to add LayoutData to the child, depending on what Layout is ued on page. 
	 * @param child
	 * @param height - respect the height when doing the layout
	 */
	abstract void layoutChild(Control child, int height);
	


}
