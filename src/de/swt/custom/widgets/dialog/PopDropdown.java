package de.swt.custom.widgets.dialog;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * This Tooltip has a header. It can contain Widgets inside. It closes automatically, onMouseOut.
 * </p>
 * 
 * <p>
 * Because no OK/Close buttons has to be clicked in order to close the dialog using the Data in this
 * dialog is much easier and faster. This dialog has to be used to show/Edit small amount of data.
 * To display larger amounts of data - the usual dialogs with buttons and header should be used.
 * </p>
 * 
 * @author alf
 * 
 */
public class PopDropdown extends Window {
	
    static boolean takeFocusOnOpen = false;
    static boolean persistSize = true;
    static boolean persistLocation = true;
    static boolean showDialogMenu = false;
    static boolean showPersistActions = false;

    private Listener listenerEsc;
    private Listener listenerClickOutside;
    private ControlListener parentShellModify;
    private ShellListener parentShellActivatedListener;

    private Shell parentShell;
    private Composite tooltipComposite;
    private String title;

    // Ensure that only one tooltip is active in time
    private static Shell currentPopupShell;

    private Boolean allowOneInstance = true;
    private int x = -1;
    private int y = -1;

    private static final int MARGIN_CLIENTAREA = 5;
    private static final int SHELL_STYLE = SWT.NO_FOCUS | SWT.TOOL;

    private int style = CLOSE_ON_PARENT_MOVE | CLOSE_ON_ESC ;
    
    public static final int NONE = 0;								//0000
    public static final int CLOSE_ON_PARENT_MOVE = 1;				//0001
    public static final int CLOSE_ON_ESC = 2;						//0100
    public static final int CLOSE_ON_CLICK_OUTSIDE_DIALOG = 4;		//1000
    

    /**
     * Constructs a new Window
     * 
     * @param pShell
     *            - parent Shell
     * @param title
     *            - Window Title
     */
    public PopDropdown(Shell pShell, String title) {
        super(pShell);
        parentShell = pShell;
        this.setTitle(title);
        
        
        if(CLOSE_ON_PARENT_MOVE==(style & CLOSE_ON_PARENT_MOVE)){
            this.parentShellModify = new ControlListener() {
                @Override
                public void controlResized(ControlEvent e) {
                		close();	
                }

                @Override
                public void controlMoved(ControlEvent e) {
                		close();
                }
            };
        }


    	if(CLOSE_ON_ESC==(style & CLOSE_ON_ESC)){
            this.listenerEsc = new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.keyCode == SWT.ESC) {
                    		close();
                    }
                }
            };
    	}
        
    	if(CLOSE_ON_CLICK_OUTSIDE_DIALOG==(style & CLOSE_ON_CLICK_OUTSIDE_DIALOG)){
            this.listenerClickOutside = new Listener() {

                @Override
                public void handleEvent(Event event) {
                    if (tooltipComposite == null || tooltipComposite.isDisposed()) {
                        return;
                    }
                    if (event.widget instanceof Control) {
                        Point absolutePos = ((Control) event.widget).toDisplay(event.x, event.y);
                        if (!isInsideDialog(absolutePos)) {
                            close();
                        }
                    }
                }
            };

	        this.parentShellActivatedListener = new ShellAdapter() {
	            @Override
	            public void shellActivated(ShellEvent e) {
	                close();
	            }
	        };
	        
    	}
    }
    
    /**
     * This style works like SWT style bits.
     * @param style - pass the style-bits like {@link #CLOSE_ON_PARENT_MOVE}, {@link #CLOSE_ON_ESC}, {@link #CLOSE_ON_CLICK_OUTSIDE_DIALOG} connected by logical OR.
     */
    public void setStyle(int style){
    	this.style = style;
    }
    
    @Override
    public int open() {
        // allow only one instance of a PopDialog
        if (currentPopupShell != null && allowOneInstance) {
            close();
        }
        if (getShell() == null) {
            create();
        }
        this.getShell().setText(title);
        currentPopupShell = getShell();
        
        if(listenerEsc != null){
        	parentShell.getDisplay().addFilter(SWT.ESC, listenerEsc);
        }
        if(listenerClickOutside != null){
        	parentShell.getDisplay().addFilter(SWT.MouseDown, listenerClickOutside);
        }
        if(parentShellModify != null){
        	parentShell.addControlListener(parentShellModify);
        }
        if(parentShellActivatedListener != null){
        	parentShell.addShellListener(parentShellActivatedListener);
        }
        return super.open();
    }

    /**
     * Opens the Window in position
     * 
     * @param absx
     *            - x pos
     * @param absy
     *            - y pos
     * @return - the return code
     */
    public int open(int absx, int absy) {
        this.x = absx;
        this.y = absy;
        return open();
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        if (x >= 0 && y >= 0) {
            return new Point(x, y);
        } else {
            return super.getInitialLocation(initialSize);
        }
    }

    @Override
    public boolean close() {
        boolean result = super.close();
        if (currentPopupShell != null && !currentPopupShell.isDisposed()) {
            currentPopupShell.dispose();
            currentPopupShell = null;
        }
        tooltipComposite = null;

        
        if(listenerEsc != null){
        	parentShell.getDisplay().removeFilter(SWT.ESC, listenerEsc);
        }
        if(listenerClickOutside != null){
        	parentShell.getDisplay().removeFilter(SWT.MouseDown, listenerClickOutside);
        }
        if(parentShellModify != null){
        	parentShell.removeControlListener(parentShellModify);
        }
        if(parentShellActivatedListener != null){
        	parentShell.removeShellListener(parentShellActivatedListener);
        }
        
        return result;
    }
    
    @Override
    public void create() {
        setShellStyle(SHELL_STYLE);
        super.create();
    }

    public void setTitle(String name) {
        this.title = name;
    }
    
    
    //PROTECTED
    
    @Override
    protected Control createContents(Composite parent) {
        return createToolTipContentArea(parent);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
    }


    protected Composite createToolTipContentArea(Composite parent) {

        parent.setLayout(new FillLayout());

        // CONTAINER
        final Composite container = new Composite(parent, SWT.NONE);
        this.tooltipComposite = container;

        FillLayout fLayout = new FillLayout();
        fLayout.marginWidth = 0;
        fLayout.marginHeight = 0;
        container.setLayout(fLayout);

        // CLIENTAREA COMPOSITE - may be changed by children of this class
        final Composite clientArea = new Composite(container, SWT.NONE) {
            @Override
            public void setBackground(Color color) {
                super.setBackground(color);
                container.setBackground(color);
            }
        };

        FillLayout clientAreaLayout = new FillLayout();
        clientArea.setLayout(clientAreaLayout);

        // this may be a call to an implementation of a subclass,
        // if subclasses do hook in into createContentArea() to create custom ToolTip content
        createContentArea(clientArea);

        return clientArea;
    }

    protected Composite createContentArea(Composite parent) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * Checks whether the Coordinates are inside the Dialog.
     * 
     * @param absCursorLocation
     *            - the absolute coordinates, with 0,0 in the upper left corner.
     * @return - true if true
     */
    protected boolean isInsideDialog(Point absCursorLocation) {
        Point absEventPos = absCursorLocation;
        Rectangle absTooltipBounds = tooltipComposite.getShell().getBounds();

        boolean insideX = (absTooltipBounds.x < absEventPos.x)
                && absEventPos.x < (absTooltipBounds.x + absTooltipBounds.width);
        boolean insideY = (absTooltipBounds.y < absEventPos.y)
                && absEventPos.y < (absTooltipBounds.x + absTooltipBounds.height);
        if (insideX && insideY) {
            return true;
        }
        return false;
    }

}
