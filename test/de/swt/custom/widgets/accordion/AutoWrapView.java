package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AutoWrapView {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AutoWrapView window = new AutoWrapView();
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
		
		final ThreeInRowLayout tl = new ThreeInRowLayout();
		shell.setLayout(tl);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		
		shell.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				// pass resize to my children, via layout or directly
				Point p = shell.getSize();
				resizeChildren(p.x, SWT.DEFAULT); // one of parameters has to be default. Only then child asks its LayoutManager to layout itselfe
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// pass resize to my children, via layout or directly
				Point p = shell.getSize();
				resizeChildren(p.x, SWT.DEFAULT); // one of parameters has to be default. Only then child asks its LayoutManager to layout itselfe 
			}
		});
		
		// initial Heavy
		HeavyControl h = new HeavyControl(shell, SWT.NONE, 3);
		FormData fd = new FormData();
		fd.top = new FormAttachment(shell, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(shell, 50, SWT.TOP);
		h.setLayoutData(fd);
		
		// other heavies below
		createHeavies(20, shell, h);
	}
	
	private void createHeavies(int count, Composite parent, Composite positionAbove){
		if(count>0){
			HeavyControl h = create( parent,  positionAbove);
			createHeavies(--count, parent, h);
		}
	}
	
	private HeavyControl create(Composite parent, Composite positionBelowThis){
		HeavyControl h = new HeavyControl(parent, SWT.NONE, 3);
		FormData fd = new FormData();
		fd.top = new FormAttachment(positionBelowThis, 50, SWT.TOP);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(positionBelowThis, 100, SWT.TOP);
		h.setLayoutData(fd);
		return h;
	}
	
	private void resizeChildren(int widthHint, int heightHint){
		for(Control c:shell.getChildren()){
			c.computeSize(widthHint, heightHint, true);
		}
	}

}
