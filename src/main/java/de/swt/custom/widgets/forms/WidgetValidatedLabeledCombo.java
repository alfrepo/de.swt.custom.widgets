package de.swt.custom.widgets.forms;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.swt.custom.widgets.jface.wizards.FactoryWidgets;

public class WidgetValidatedLabeledCombo extends WidgetValidatedLabeled {

	private Combo combo;
	
	public WidgetValidatedLabeledCombo(Composite parent, int style) {
		super(parent, style);
	}
	
	protected void createContent(Composite contentParent){
		contentParent.setLayout(new FillLayout());
		combo = FactoryWidgets.getCombo(contentParent, SWT.BORDER);
	}
	
	public Combo getCombo() {
		return combo;
	}
	
	public void setComboContent(Map<String,String> idToStringMap){
		// TODO alf implement
	}

}
