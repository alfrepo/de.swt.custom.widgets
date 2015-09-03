package de.swt.custom.widgets.dialog;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

public class Test {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Test window = new Test();
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
//		SWT.BORDER
//		SWT.CLOSE
//		SWT.MIN
//		SWT.MAX
//		SWT.RESIZE
//		SWT.TITLE
//		SWT.TOOL
//		SWT.NO_TRIM
//		SWT.SHELL_TRIM
//		SWT.DIALOG_TRIM
//		SWT.ON_TOP
//		SWT.MODELESS
//		SWT.PRIMARY_MODAL
//		SWT.APPLICATION_MODAL
//		SWT.SYSTEM_MODAL
//		SWT.SHEET
		
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Label lblLabelll = new Label(shell, SWT.NONE);
		lblLabelll.setBounds(92, 110, 55, 15);
		lblLabelll.setText("Labelll");

		
		final Shell pop = new Shell(shell, SWT.CLOSE|SWT.RESIZE);
		Text t = new Text(pop, SWT.NONE);
		t.setText("TTeest");
		
		ConnectorDisapearingShell connectorDisapearingShell = new ConnectorDisapearingShell();
		connectorDisapearingShell.connect(lblLabelll, pop);
		
	}
}
