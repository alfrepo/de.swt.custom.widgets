package de.swt.custom.widgets.jface.wizards;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public abstract class WizardPageWithHistory extends WizardPage {

	WidgetWithHistoryForWizardpage historyWidget;
	Composite clientArea;
	
	/**
	 * Create the wizard.
	 */
	public WizardPageWithHistory(String title) {
		super(title);
		
		// set default image since jface fails fo load it from jar
		ImageDescriptor imageDescriptor = Style.wizardPageIcon;
		setImageDescriptor(imageDescriptor);
		
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param wizardPageComposite
	 */
	@Override
	public final void createControl(Composite wizardPageComposite) {
		
		wizardPageComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		wizardPageComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		
		Composite dialogArea = new Composite(wizardPageComposite, SWT.NULL);
		dialogArea.setBackgroundMode(SWT.INHERIT_DEFAULT);

		setControl(dialogArea);
		dialogArea.setLayout(new FormLayout());
		
		historyWidget = new WidgetWithHistoryForWizardpage(dialogArea, SWT.NONE);
		historyWidget.setBackgroundMode(SWT.INHERIT_FORCE);
		FormData fd_historyArea = new FormData();
		fd_historyArea.top = new FormAttachment(0);
		fd_historyArea.bottom = new FormAttachment(100, 0);
		fd_historyArea.left = new FormAttachment(0, 0);
		fd_historyArea.right = new FormAttachment(0, 150);
		
		
		historyWidget.setLayoutData(fd_historyArea);
		
		clientArea = new Composite(dialogArea, SWT.NONE);
		FormData fd_clientArea = new FormData();
		fd_clientArea.top = new FormAttachment(0);
		fd_clientArea.bottom = new FormAttachment(100, 0);
		fd_clientArea.left = new FormAttachment(historyWidget);
		fd_clientArea.right = new FormAttachment(100, 0);
		clientArea.setLayoutData(fd_clientArea);
		clientArea.setBackgroundMode(SWT.INHERIT_FORCE);
		
		// abstract. create data on Page
		createControls(clientArea);
		
		// generate History depending on the Wizard 
		generateHistory();
	}
	

	public Composite getHistoryArea() {
		return historyWidget;
	}
	
	public Composite getClientArea() {
		return clientArea;
	}
	
	public abstract void createControls(Composite clientArea);
	
	// PRIVATE
	
	private void generateHistory(){
		IWizard wizard = getWizard();
		for(IWizardPage page : Arrays.asList(wizard.getPages())){
			HistoryItem historyItem = historyWidget.addItem(page);
			if(page.equals(this)){
				historyItem.setActive(true);
			}
		}
		
		// TODO activate / deactivate pages if pages have already been filled in Wizard 
		// when can you switch between pages? When they have been active once?
		// TODO validate before switching forward / backward
	}
	

}
