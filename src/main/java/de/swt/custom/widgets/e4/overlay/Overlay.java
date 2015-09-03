package de.swt.custom.widgets.e4.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.swt.custom.utils.UtilsUI;

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
    
    private int widthContentShell = 200;
    private int heightContentShell = 200;
    
    private Shell shellOverlay;
    private Shell shellContent;
    
    ShellBinder shellBinderOverlayToParent;
    ShellBinder shellBinderContentShellToOverlayShell;

    public Overlay(final Composite parentComposite) {
        // and create, position and style the content overlay  
        createOverlay(parentComposite);
    }

    public Shell getShellContent() {
        return shellContent;
    }

    public Shell getShellOverlay() {
        return shellOverlay;
    }

    /** Opens the overlay shell. */
    public void open() {
        // user hook in here
        createOverlayContent(shellContent);
        
        // layout both shells to adopt their size on dependent shell sizes
		this.shellOverlay.layout(true, true);
		this.shellContent.layout(true, true);
		
		// position the overlay over the parent
		updateOverlayPosition();
		
		// now open the shells
        getShellOverlay().open();
        getShellContent().open();
    }

    /**
     * Closes the overlay shell. Does not destroy the shell. It must not explicetly be destroyed -
     * it will be destroyed together with the parent
     */
    public void close() {
    	// shellContent will be closed automatically because it's a subshell of ShellOverlay 
    	try{
    		/* disconnect the overlay from the covered composite
    		 * Should dispose the overlay shell too,
    		 * which should dispose the dependant content shell
    		 */
    		this.shellBinderOverlayToParent.dispose();
    		
    		/* explicitely dispose the content shell again */
    		this.shellBinderContentShellToOverlayShell.dispose();
    		
    		/* explicitely close the overlay shell */
    		getShellOverlay().close();
    		
    	}catch(SWTException e){
    		// shel is already closed!
    	}
    }

    /** Updates the position of the overlay */
    public void updateOverlayPosition() {
        shellBinderOverlayToParent.update();
    }

    /** Makes the overlay shell - visible */
    public void setVisible(boolean isVisible) {
        this.shellOverlay.setVisible(isVisible);
        this.shellContent.setVisible(isVisible);
    }

    /** Resizes the size of the content shell */
    public void setContentShellSize(int width, int height) {
        this.widthContentShell = width;
        this.heightContentShell = height;
    }
    
    // PROTECTED
    protected void bindOverlayToComposite(Composite parentComposite){
    	
    	// recycle the old binder
    	if(this.shellBinderOverlayToParent != null){
    		this.shellBinderOverlayToParent.disposeListeners();
    	}
    	
    	// now create a new binder
        this.shellBinderOverlayToParent = new ShellBinder(parentComposite, this.shellOverlay, false) {
        	
        	@Override
        	public String toString() {
        		return "ShellBinder to bind the Overlay to the parent";
        	}
        	
            @Override
            protected void positionDependentShell() {
                // hook in. let the user of the overlay implement the positioning of the overlay
                positionOverlay(getParent(), getShellOverlay());
            }
        };
    }

    // PRIVATE
    protected void createOverlay(Composite parentComposite) {
    	
    	// create Overlay shell
        this.shellOverlay = new Shell(parentComposite.getShell(), SWT.NONE);

        // bind the overlay to the parent
        bindOverlayToComposite(parentComposite);
    	
        // create small content-shell
    	this.shellContent = new Shell(this.shellOverlay, SWT.BORDER | SWT.SHADOW_OUT);
        
        styleOverlayShell();
        styleContentShell();

        // bind the small content-shell to the overlay
        this.shellBinderContentShellToOverlayShell = new ShellBinder(this.shellOverlay, shellContent, true) {
        	
        	@Override
        	public String toString() {
        		return "ShellBinder to bind the small content-shell to the overlay";
        	}
        	
            @Override
            protected void positionDependentShell() {
                try {
                    Rectangle overflowShellBounds = shellOverlay.getBounds();
                    Rectangle contentShellBounds = shellContent.getBounds();

                    getDependentShell().setSize(widthContentShell, heightContentShell);

                    // cut the contentShell to the size of overlay if necessary
                    if (contentShellBounds.width > overflowShellBounds.width) {
                        shellContent.setSize(overflowShellBounds.width, contentShellBounds.height);
                    }
                    if (contentShellBounds.height > overflowShellBounds.height) {
                        shellContent.setSize(contentShellBounds.width, overflowShellBounds.height);
                    }
                    contentShellBounds = shellContent.getBounds();

                    int deltaWidth = overflowShellBounds.width - contentShellBounds.width;
                    int deltaHeight = overflowShellBounds.height - contentShellBounds.height;

                    int x = overflowShellBounds.x + deltaWidth / 2;
                    int y = overflowShellBounds.y + deltaHeight / 2;

                    shellContent.setLocation(x, y);
                    shellContent.moveAbove(getParentShell());
                } catch (org.eclipse.swt.SWTException e) {
                    System.out.println("The shell is allready disposed. Will not adopt the overflow dialog's size");
                }
            }
        };
    }

    private void styleOverlayShell() {
        this.shellOverlay.setBackground(OVERLAY_COLOR);
        this.shellOverlay.setBackgroundMode(SWT.INHERIT_DEFAULT);
        this.shellOverlay.setAlpha(DEFAULT_ALPHA);
    }

    protected void styleContentShell() {
        // nothing yet
    }

    /**
     * Default implementation positions the overlay over the parent
     * it may be necessary to let out some trim, window decoration..
     *
     * @param parent
     *            - the Composite, which is covered by the current overlay
     * @param overlay
     *            - the shell which represents the overlay. It should covert the parent.
     */
    protected void positionOverlay(Composite parent, Shell overlay) {
        Rectangle clientArea = parent.getClientArea();

        /*
         * Retrieve the position of the composite by using the method, which would consider the Right-To-Left mode.
         */
        Point pos = UtilsUI.getDisplayPos(parent);

        int x = pos.x;
        int y = pos.y;

        int width = clientArea.width;
        int height = clientArea.height;
        overlay.setLocation(x, y);
        overlay.setSize(width, height);
    };

    abstract protected void createOverlayContent(Shell parent);

}
