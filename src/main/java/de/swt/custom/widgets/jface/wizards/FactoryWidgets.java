package de.swt.custom.widgets.jface.wizards;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Is able to mark the widgets with classes, provide a central style
 * @author alf
 *
 */
public class FactoryWidgets {
	
	public static Label getLabel(Composite parent, int style){
		Label label = new Label(parent, style);
		return label;
	}
	
	public static Button getButton(Composite parent, int style){
		Button button = new Button(parent, style);
		return button;
	}
	
	public static Text getText(Composite parent, int style){
		Text text = new Text(parent, style);
		return text;
	}
	
	public static Combo getCombo(Composite parent, int style){
		Combo combo = new Combo(parent, style);
		return combo;
	}
	

}
