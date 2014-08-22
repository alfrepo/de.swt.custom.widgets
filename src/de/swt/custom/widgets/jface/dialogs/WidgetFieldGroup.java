package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class WidgetFieldGroup extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WidgetFieldGroup(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginBottom = Style.WIDGET_VALIDATED_LABLED_TEXT_MARGIN_BOTTOM;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);

		/*
		 * To make the forms fill the group - laying out the children is necessary.
		 * Unfortunately it is not possible to listen for children addition, in order to layout the children addition automatically.
		 * This means that the user will have to remember  to layout the children, which is a nogo.
		 * 
		 * Solution: 
		 * istead of adding right layout on child addition -  check if there are new children every time the widget is resized.
		 * if a widget without layoutData is found (new child not layed out) - add a layout
		 */
		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				layoutChildren();
			}
		});
	}
	
	/**
	 * Overrides child layout to make the child fill the group horizontally
	 */
	private void layoutChildren(){
		// TODO alf remember old children and layout only new children
		for(Control child:getChildren()){
			child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));	
		}
	}
	
	public void addToGroup(Composite child){
		child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
