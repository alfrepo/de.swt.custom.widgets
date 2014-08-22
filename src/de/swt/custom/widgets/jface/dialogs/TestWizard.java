package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

public class TestWizard extends Wizard {

	public TestWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		addPage(new HistoryWizardPage() {
			@Override
			public void createControls(Composite clientArea) {
				// create stuff
			}
		});
		addPage(new HistoryWizardPage() {
			@Override
			public void createControls(Composite clientArea) {
				// create stuff
			}
		});
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
