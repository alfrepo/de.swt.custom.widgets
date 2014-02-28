package de.swt.custom.widgets.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ShellBinder {

	private DisposeListener disposeListener;
	private Composite parent;
	private Shell parentShell;
	private Shell dependentShell;
	private boolean bindShellsTogether;
	
	public ShellBinder(Composite parent, Shell dependentShell, boolean bindShellsTogether) {
		this.parent = parent;
		this.parentShell = parent.getShell();
		this.dependentShell = dependentShell;
		this.bindShellsTogether = bindShellsTogether;
		init();
	}
	

	private void init(){
		
		final ControlListener resizeListenerControl = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				positionDependentShell();
			}
		};
		final ControlListener resizeListenerShell = new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent e) {
				positionDependentShell();
			}
		};
		final Listener moveListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				positionDependentShell();
			}
		};
		final ShellAdapter shellOpenCloseSync = new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				dependentShell.open();
			}
			@Override
			public void shellClosed(ShellEvent e) {
				dependentShell.close();
			}
		};


		this.disposeListener = new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				parent.removeListener(SWT.Move, moveListener);
				parentShell.removeShellListener(shellOpenCloseSync);
				parent.removeDisposeListener(disposeListener);
				parentShell.removeControlListener(resizeListenerShell);
				parent.removeControlListener(resizeListenerControl);
				dependentShell.dispose();
			}
		};
		
		// add listeners
		parent.addListener(SWT.Move, moveListener);
		parent.addDisposeListener(disposeListener);
		parentShell.addControlListener(resizeListenerShell);
		parent.addControlListener(resizeListenerControl);
		if(this.bindShellsTogether){ 
			parentShell.addShellListener(shellOpenCloseSync);
		}

	}
	
	public void dispose(){
		this.disposeListener.widgetDisposed(null);
	}
	
	public Shell getDependentShell() {
		return dependentShell;
	}
	
	public Shell getParentShell() {
		return parentShell;
	}
	
	public Composite getParent() {
		return parent;
	}
	
	public void update(){
		positionDependentShell();
	}
	

	/* Does the positioning of the content shell relatively to the OverlayShell */
	protected void positionDependentShell() {
		try {
			Rectangle overflowShellBounds = parentShell.getBounds();
			Rectangle contentShellBounds = dependentShell.getBounds();

			dependentShell.setSize(200, 200);
			
			// cut the contentShell to the size of overlay if necessary
			if (contentShellBounds.width > overflowShellBounds.width) {
				dependentShell.setSize(overflowShellBounds.width,
						contentShellBounds.height);
			}
			if (contentShellBounds.height > overflowShellBounds.height) {
				dependentShell.setSize(contentShellBounds.width,
						overflowShellBounds.height);
			}
			contentShellBounds = dependentShell.getBounds();

			int deltaWidth = overflowShellBounds.width
					- contentShellBounds.width;
			int deltaHeight = overflowShellBounds.height
					- contentShellBounds.height;

			int x = overflowShellBounds.x + deltaWidth / 2;
			int y = overflowShellBounds.y + deltaHeight / 2;

			dependentShell.setLocation(x, y);
			dependentShell.moveAbove(parentShell);
		} catch (org.eclipse.swt.SWTException e) {
			System.out
					.println("The shell is allready disposed. Will not adopt the overflow dialog's size");
		}
	}
	
}
