package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

public class TestWindow {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestWindow window = new TestWindow();
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
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		WidgetRows rows = new WidgetRows(shell,SWT.NONE, 2);
		Composite row1 = rows.getRow(0);
		Composite row2 = rows.getRow(1);
		
		// row 1
		WidgetColumns columns = new WidgetColumns(row1, SWT.NONE, 2);
		Composite column1 = columns.getColumn(0);
		Composite column2 = columns.getColumn(1);
		
		column1.setLayout(new GridLayout());
		column2.setLayout(new GridLayout());
		
		column1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		
		// row1 column 1
		WidgetFieldGroup group1 = new WidgetFieldGroup(column1, SWT.NONE);
		group1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		WidgetValidatedLabeledText text11 = new WidgetValidatedLabeledText(group1, SWT.NONE);
		WidgetValidatedLabeledText text12 = new WidgetValidatedLabeledText(group1, SWT.NONE);
		
		// row1 column 2
		WidgetFieldGroup group2 = new WidgetFieldGroup(column2, SWT.NONE);
		group2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		WidgetValidatedLabeledText text21 = new WidgetValidatedLabeledText(group2, SWT.NONE);
		WidgetValidatedLabeledText text22 = new WidgetValidatedLabeledText(group2, SWT.NONE);
		
		
		//row2
		new Button(row2, SWT.None).setText("Button");
	}

}
