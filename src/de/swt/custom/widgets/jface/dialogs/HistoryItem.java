package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class HistoryItem extends Composite {
	Label lblIcon;
	Link linkText;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HistoryItem(Composite parent, int style) {
		super(parent, SWT.NO_BACKGROUND);
		
		createContent(this);
		
		// style
		this.setBackgroundMode(SWT.INHERIT_FORCE);
	}
	
	private void createContent(Composite contentArea){
		contentArea.setLayout(new FormLayout());
		
		lblIcon = new Label(contentArea, SWT.NONE);
		FormData fd_lblIcon = new FormData();
		fd_lblIcon.right = new FormAttachment(0, Style.WIDTH_ACTIVE_HISTORY_ITEM_ICON_PX);
		fd_lblIcon.top = new FormAttachment(0);
		fd_lblIcon.left = new FormAttachment(0);
		lblIcon.setLayoutData(fd_lblIcon);
		
		linkText = new Link(contentArea, SWT.NONE);
		FormData fd_linkText = new FormData();
		fd_linkText.top = new FormAttachment(0);
		fd_linkText.left = new FormAttachment(lblIcon);
		linkText.setLayoutData(fd_linkText);
		linkText.setText("<a>New Label</a>");
		
	}
	
	public void setActive(boolean isActive){
		if(isActive){
			lblIcon.setText("x");	
		}else{
			lblIcon.setText("");
		}
	}
	
	public void setText(String text){
		linkText.setText(text);
	}
}
