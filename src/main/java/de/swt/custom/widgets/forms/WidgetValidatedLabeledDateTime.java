package de.swt.custom.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.swt.custom.widgets.jface.wizards.FactoryWidgets;

public class WidgetValidatedLabeledDateTime extends WidgetValidatedLabeled {

	public Combo date;
	public Combo time;

	public WidgetValidatedLabeledDateTime(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createContent(Composite textFieldParent) {
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		textFieldParent.setLayout(gl_composite);

		date = FactoryWidgets.getCombo(textFieldParent, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_combo.widthHint = 201;
		date.setLayoutData(gd_combo);

		time = FactoryWidgets.getCombo(textFieldParent, SWT.NONE);
	}

}
