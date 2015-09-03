package de.swt.custom.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.swt.custom.widgets.jface.wizards.FactoryWidgets;

public class WidgetValidatedLabeledText extends WidgetValidatedLabeled {

	public WidgetValidatedLabeledText(Composite parent, int style) {
		super(parent, style);
	}

	private Text text;
	
	protected void createContent(Composite contentParent){
		contentParent.setLayout(new FillLayout());
		text = FactoryWidgets.getText(contentParent, SWT.BORDER);
	}
	
	public Text getText() {
		return text;
	}

}
