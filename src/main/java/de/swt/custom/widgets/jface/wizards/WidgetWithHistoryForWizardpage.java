package de.swt.custom.widgets.jface.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class WidgetWithHistoryForWizardpage extends Composite {

	Label lblTitle;
	Composite parentOfHistoryItems;
	List<HistoryItem> items = new ArrayList<HistoryItem>();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WidgetWithHistoryForWizardpage(Composite parent, int style) {
		// create THIS composite
		super(parent, style);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		createContent(this);
	}
	
	
	private void createContent(Composite clientArea){
		setLayout(new GridLayout(1, false));
		
		// data
		lblTitle = FactoryWidgets.getLabel(clientArea, SWT.NONE);
		GridData gd_lblTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblTitle.exclude = true;
		lblTitle.setLayoutData(gd_lblTitle);
		parentOfHistoryItems = new Composite(clientArea, SWT.NONE);
		parentOfHistoryItems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
