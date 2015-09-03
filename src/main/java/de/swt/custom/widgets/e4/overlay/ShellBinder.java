package de.swt.custom.widgets.e4.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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

    private static final Object DONT_DIPOSE_SHELL = "DONT_DIPOSE_SHELL";
	private DisposeListener disposeListener;
    private Composite parentComposite;
    private Shell parentCompositesShell;
    private Shell dependentShell;
    private boolean bindShellsTogether;

    public ShellBinder(Composite parent, Shell dependentShell, boolean bindShellsTogether) {
        this.parentComposite = parent;
        this.parentCompositesShell = parent.getShell();
        this.dependentShell = dependentShell;
        this.bindShellsTogether = bindShellsTogether;
        init();
    }

    private void init() {

        final ControlListener resizeListenerControl = new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                try {
                    positionDependentShell();
                } catch (SWTException disposede) {
                    parentComposite.removeControlListener(this);
                }
            }
        };
        final ControlListener resizeListenerShell = new ControlAdapter() {
            @Override
            public void controlMoved(ControlEvent e) {
                try {
                    positionDependentShell();
                } catch (SWTException disposede) {
                    parentCompositesShell.removeControlListener(this);
                }
            }
        };
        final Listener moveListener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                try {
                    positionDependentShell();
                } catch (SWTException disposede) {
                    parentCompositesShell.removeListener(SWT.Move, this);
                }
            }
        };
        final ShellAdapter shellOpenCloseSync = new ShellAdapter() {
            @Override
            public void shellActivated(ShellEvent e) {
                try {
                    dependentShell.open();
                } catch (SWTException disposede) {
                    parentCompositesShell.removeShellListener(this);
                }
            }

            @Override
            public void shellClosed(ShellEvent e) {
                try {
                    dependentShell.close();
                } catch (SWTException disposede) {
                    parentCompositesShell.removeShellListener(this);
                }
            }
        };

        this.disposeListener = new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
            	parentCompositesShell.removeListener(SWT.Move, moveListener);
                parentComposite.removeDisposeListener(disposeListener);
                parentCompositesShell.removeControlListener(resizeListenerShell);
                parentComposite.removeControlListener(resizeListenerControl);
                parentCompositesShell.removeShellListener(shellOpenCloseSync);

                // binder's disposeListeners method uses this to not dispose the dependent Shell 
                if(e!=null && e.data != DONT_DIPOSE_SHELL){
                	dependentShell.dispose();	
                }
            }
        };

        // add listeners
        parentCompositesShell.addListener(SWT.Move, moveListener);
        parentComposite.addDisposeListener(disposeListener);
        parentCompositesShell.addControlListener(resizeListenerShell);
        parentComposite.addControlListener(resizeListenerControl);
        if (this.bindShellsTogether) {
            parentCompositesShell.addShellListener(shellOpenCloseSync);
        }
    }
    
    public Shell getDependentShell() {
        return dependentShell;
    }

    public Shell getParentShell() {
        return parentCompositesShell;
    }

    public Composite getParent() {
        return parentComposite;
    }

    public void update() {
        positionDependentShell();
    }

    /** Removes all listeners which connect the dependent shell with the composite. Disposes the dependent shell. */
    public void dispose(){
    	Event event = new Event();
    	event.widget = parentComposite;
    	DisposeEvent disposeEvent = new DisposeEvent(event);
    	this.disposeListener.widgetDisposed(disposeEvent);
    }
    
    /** Removes all listeners which connect the dependent shell with the composite. DOES NOT dispose the dependent shell. */
    public void disposeListeners(){
    	Event event = new Event();
    	event.widget = parentComposite;
    	DisposeEvent disposeEvent = new DisposeEvent(event);
    	disposeEvent.data = DONT_DIPOSE_SHELL;
    	this.disposeListener.widgetDisposed(disposeEvent);
    }

    /* Does the positioning of the content shell relatively to the OverlayShell */
    protected void positionDependentShell() {
        try {
            Rectangle overflowShellBounds = parentCompositesShell.getBounds();
            Rectangle contentShellBounds = dependentShell.getBounds();

            dependentShell.setSize(200, 200);

            // cut the contentShell to the size of overlay if necessary
            if (contentShellBounds.width > overflowShellBounds.width) {
                dependentShell.setSize(overflowShellBounds.width, contentShellBounds.height);
            }
            if (contentShellBounds.height > overflowShellBounds.height) {
                dependentShell.setSize(contentShellBounds.width, overflowShellBounds.height);
            }
            contentShellBounds = dependentShell.getBounds();

            int deltaWidth = overflowShellBounds.width - contentShellBounds.width;
            int deltaHeight = overflowShellBounds.height - contentShellBounds.height;

            int x = overflowShellBounds.x + deltaWidth / 2;
            int y = overflowShellBounds.y + deltaHeight / 2;

            dependentShell.setLocation(x, y);
            dependentShell.moveAbove(parentCompositesShell);
        } catch (org.eclipse.swt.SWTException e) {
            System.out.println("The shell is allready disposed. Will not adopt the overflow dialog's size");
        }
    }

}
