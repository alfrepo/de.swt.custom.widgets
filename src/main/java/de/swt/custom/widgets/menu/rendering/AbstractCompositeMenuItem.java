package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractCompositeMenuItem extends Composite {
	
	public AbstractCompositeMenuItem(Composite parent, int style) {
		super(parent, style);
	}
	abstract void setVisibleLblHasSubpages(boolean isVisible);
	abstract void setIcon(Image image);
	abstract void setText(String name);
	
}
