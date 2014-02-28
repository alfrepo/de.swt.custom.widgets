package de.swt.custom.widgets.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Represents a transparent overlay, which is able to cover composites.
 * It binds on covered composites and changes with them together (moves, resizes, destroys itselfe).
 * 
 * @author alf
 * 
 */
public abstract class Overlay {

	
	private static final Color OVERLAY_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static final int DEFAULT_ALPHA = 150;
	int widthContentShell = 200;
	int heightContentShell = 200;
	Shell shellOverlay;
	Shell shellContent;
	ShellBinder shellBinderOverlayToParent;
	
	public Overlay() {}
	
	public Overlay(final Composite parentComposite) {
		this();
		bind(parentComposite);
	}
	
	public Shell getShellContent() {
		return shellContent;
	}
	
	public Shell getShellOverlay() {
		return shellOverlay;
	}
	
	/** Opens the overlay shell. */
	public void open(){
		setVisible(true);
		getShellOverlay().open();
		getShellContent().open();
	}
	
	/** Closes the overlay shell. Does not destroy the shell. It must not explicetly be destroyed - it will be destroyed together with the parent*/
	public void close(){
		getShellOverlay().close();
	}
	
	public void bind(Composite parentComposite){
		this.shellOverlay = new Shell(parentComposite.getShell(), SWT.NONE);
		
		// bind the overlay to the parent
		this.shellBinderOverlayToParent = new ShellBinder(parentComposite, this.shellOverlay, false){
			@Override
			protected void positionDependentShell() {
				// hook in. let the user of the overlay implement the positioning of the overlay
				positionOverlay( getParent(), getShellOverlay() );
			}
		}; 
		init();
		
		setVisible(false);
		update();
		setVisible(true);
	}
	
	public void unbind(){
		if(this.shellBinderOverlayToParent == null) return;
		this.shellBinderOverlayToParent.dispose();
		this.shellBinderOverlayToParent = null;
		
		this.shellOverlay = null;
	}
	
	/** Updates the position of the overlay	 */
	public void update(){
		shellBinderOverlayToParent.update();
	}
	
	/** Makes the overlay shell - visible */
	public void setVisible(boolean isVisible){
		this.shellOverlay.setVisible(isVisible);
		this.shellContent.setVisible(isVisible);
	}
	
	/** Resizes the size of the content shell */
	public void setContentShellSize(int width, int height){
		this.widthContentShell = width;
		this.heightContentShell = height;
	}
	
	//PRIVATE
	private void init(){
		this.shellContent = new Shell(this.shellOverlay, SWT.BORDER|SWT.SHADOW_OUT);
		styleOverlayShell();
		styleContentShell();
		
		// bind the content shell to the overlay
		new ShellBinder(this.shellOverlay, shellContent, true){
			@Override
			protected void positionDependentShell() {
				try {
					Rectangle overflowShellBounds = shellOverlay.getBounds();
					Rectangle contentShellBounds = shellContent.getBounds();

					getDependentShell().setSize(widthContentShell, heightContentShell);
					
					// cut the contentShell to the size of overlay if necessary
					if (contentShellBounds.width > overflowShellBounds.width) {
						shellContent.setSize(overflowShellBounds.width,
								contentShellBounds.height);
					}
					if (contentShellBounds.height > overflowShellBounds.height) {
						shellContent.setSize(contentShellBounds.width,
								overflowShellBounds.height);
					}
					contentShellBounds = shellContent.getBounds();

					int deltaWidth = overflowShellBounds.width
							- contentShellBounds.width;
					int deltaHeight = overflowShellBounds.height
							- contentShellBounds.height;

					int x = overflowShellBounds.x + deltaWidth / 2;
					int y = overflowShellBounds.y + deltaHeight / 2;

					shellContent.setLocation(x, y);
					shellContent.moveAbove(getParentShell());
				} catch (org.eclipse.swt.SWTException e) {
					System.out
							.println("The shell is allready disposed. Will not adopt the overflow dialog's size");
				}
			}
		};
		
		// user hook in here
		createContent(shellContent);
	}
	
	private void styleOverlayShell(){
		this.shellOverlay.setBackground(OVERLAY_COLOR);
		this.shellOverlay.setBackgroundMode(SWT.INHERIT_DEFAULT);
		this.shellOverlay.setAlpha(DEFAULT_ALPHA);
	}
	
	protected void styleContentShell(){
		// nothing yet
	}
	
	/** Default implementation positions the overlay over the parent
	 *  it may be necessary to let out some trim, window decoration..		 
	 *  
	 *  @param parent - the Composite, which is covered by the current overlay
	 *  @param overlay - the shell which represents the overlay. It should covert the parent. */
	protected void positionOverlay(Composite parent, Shell overlay){
		Rectangle clientArea = parent.getClientArea();
		
		/* The size and position  of the clientarea is always given relative to the parent.
		 * So toDisplay() can be safely used here.  
		 */
		Point pos = parent.toDisplay(clientArea.x, clientArea.y);
		
		int x = pos.x;
		int y = pos.y;

		int width = clientArea.width;
		int height = clientArea.height;
		overlay.setLocation(x, y);
		overlay.setSize(width, height);
	};
	
	abstract protected void createContent(Shell parent);
	
}
