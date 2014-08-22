package de.swt.custom.widgets.sash;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The Button, which has an arrow up or down arrow on it. <br>
 * This Button is placed on the horizontal Sash-Belt / Sash-stripe ({@link Sash}). <br>
 * This Button controlls either the expanding or collapsing of sash areas. <br>
 * 
 * @author skip
 *
 */
public class SashToken extends Canvas {

	private int width = 30;
	private int height = 15;
	private Color bgColorNormal = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	private Color bgColorOverflow = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	private Color borderColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BORDER);
	
	protected static final Image IMAGE_ARROWDOWN = new Image(Display.getCurrent(), SashToken.class.getResourceAsStream("/arrow_down.png"));;
	protected static final Image IMAGE_ARROWUP = new Image(Display.getCurrent(), SashToken.class.getResourceAsStream("/arrow_up.png")); ;

	private Image bg = IMAGE_ARROWDOWN;
	private boolean drawBorder = false;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SashToken(Composite parent, int style) {
		super(parent, style);
		
		setBackground(bgColorNormal);
		setForeground(borderColor);
		addListener(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setBackground(bgColorOverflow);
			}
		});
		addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setBackground(bgColorNormal);
			}
		});
		addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setBackground(bgColorNormal);				
			}
		});
		addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setBackground(bgColorOverflow);
			}
		});
		
		// draw border and bg
		addPaintListener(new PaintListener() {
	        @Override
	        public void paintControl(PaintEvent e) {
	        	
	        	if(drawBorder){
		        	// border
		            e.gc.setAntialias(SWT.ON);
		            e.gc.setForeground(borderColor);
	                e.gc.drawRoundRectangle(0, 0, getClientArea().width-1, getClientArea().height-1, 1,1);	        		
	        	}
                
                // center bg
                int bgX = (getClientArea().width /2) - (bg.getBounds().width/2);
                int bgY = (getClientArea().height /2) - (bg.getBounds().height/2);
                e.gc.drawImage(bg,bgX, bgY);
	        }
	    });

		setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
	}
	
	

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return super.computeSize(width, height, changed);
	}
	
	/**
	 * Makes the token ignore clicks.
	 */
	public void deactivate(){
		// TODO alf
	}



	public Image getBg() {
		return bg;
	}



	public void setBg(Image bg) {
		this.bg = bg;
	}

}
