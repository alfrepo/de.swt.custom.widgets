package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractCompositeMenuGroup extends Composite {
	
	public AbstractCompositeMenuGroup(Composite parent, int style) {
		super(parent, style);
	}
	
	abstract AbstractCompositeMenuGroup setGroupName(String name);
	abstract AbstractCompositeMenuGroup setIcon(Image icon);
}
