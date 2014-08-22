package de.swt.custom.widgets.jface.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

public class HistoryWidget extends Composite {

	Label lblTitle;
	Composite parentOfHistoryItems;
	List<HistoryItem> items = new ArrayList<HistoryItem>();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HistoryWidget(Composite parent, int style) {
		// create THIS composite
		super(parent, style);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		createContent(this);
	}
	
	
	private void createContent(Composite clientArea){
		// layout clientarea
		clientArea.setLayout(new FormLayout());
		
		// data
		lblTitle = new Label(clientArea, SWT.NONE);
		parentOfHistoryItems = new Composite(clientArea, SWT.NONE);
		
		// layout in parent	
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0, 0);
		fd_lblTitle.left = new FormAttachment(0,0);
		lblTitle.setLayoutData(fd_lblTitle);

		// layout in parent		
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100);
		fd_composite.right = new FormAttachment(100);
		fd_composite.top = new FormAttachment(lblTitle);
		fd_composite.left = new FormAttachment(0, 0);
		parentOfHistoryItems.setLayoutData(fd_composite);
		// layout children 
		RowLayout rl_parentOfHistoryItems = new RowLayout(SWT.VERTICAL);
		rl_parentOfHistoryItems.wrap = false;
		parentOfHistoryItems.setLayout(rl_parentOfHistoryItems);
		
		// style
		lblTitle.setFont(Style.TITLE_HISTORY_FONT);
		
		// demo content
		lblTitle.setText("Title");
	}
	
	public HistoryItem addItem(IWizardPage page){
		return addItem(page, false);
	}
	
	public HistoryItem addItem(IWizardPage page, boolean isActive){
		HistoryItem historyItem = new HistoryItem(parentOfHistoryItems, SWT.NONE);
		historyItem.setText(page.getTitle());
		historyItem.setActive(isActive);

		// remember, to generate the history later 
		items.add(historyItem);
		
		layout(true, true);
		
		return historyItem;
	}

}
