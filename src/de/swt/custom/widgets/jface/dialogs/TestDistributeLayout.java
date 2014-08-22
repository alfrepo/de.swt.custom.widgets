package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;

public class TestDistributeLayout {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestDistributeLayout window = new TestDistributeLayout();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		shell.setLayout(new FormLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_composite = new FormData();
//		fd_composite.bottom = new FormAttachment(100);
		fd_composite.right = new FormAttachment(100);
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));

		// row1 column 1
		WidgetFieldGroup group1 = new WidgetFieldGroup(composite_1, SWT.NONE);
		group1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		WidgetValidatedLabeledText text11 = new WidgetValidatedLabeledText(group1, SWT.NONE);
		WidgetValidatedLabeledText text12 = new WidgetValidatedLabeledText(group1, SWT.NONE);
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.setBounds(0, 0, 75, 25);
		btnNewButton.setText("New Button");

	}

}
