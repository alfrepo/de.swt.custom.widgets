package de.swt.custom.widgets.binders;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

public class Main {
	
	BinderWindowPopup<Label> binder;

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
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
		
		Label lblHoverToShow = new Label(shell, SWT.NONE);
		lblHoverToShow.setBounds(50, 101, 191, 15);
		lblHoverToShow.setText("Hover to show Popup");

		// create the binder which is able to produce popups
		binder  = new BinderWindowPopup<Label>(Label.class) {
			@Override
			protected Shell createPopup(Label control) {
				// create the popup
				return new Shell(control.getShell(), SWT.CLOSE|SWT.RESIZE|SWT.ON_TOP);
			}
			
			@Override
			protected void createPopupContent(Label control, Shell shell) {
				// size of popup
				shell.setSize(100, 100);
				
				// create the content
				Text t = new Text(shell, SWT.DEFAULT);
				t.setText("CONTENT");
				
				shell.setLayout(new FillLayout());
			}
		};
		
		// connect the popups wit the hover label
		binder.bind(lblHoverToShow);
	}
}
