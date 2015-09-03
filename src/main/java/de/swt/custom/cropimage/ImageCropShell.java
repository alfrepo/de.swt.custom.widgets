package de.swt.custom.cropimage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class ImageCropShell extends Shell {
	
	private static final Cursor cursorSizeNS = new Cursor(Display.getCurrent(), SWT.CURSOR_SIZENS);
	private static final Cursor cursorSizeEW = new Cursor(Display.getCurrent(), SWT.CURSOR_SIZEW);
	private static final Cursor cursorHand = new Cursor(Display.getCurrent(), SWT.CURSOR_SIZEALL);
	
	private enum Side{LEFT, RIGHT, TOP, BOTTOM}
	private static final int borderWidth = 10;
	
	int width = 400;
	int height = 400;
	
	int minWidth= 20;
	int minHeight= 40;
	
	double aspectRatioXMultiplier= 2.4/2.9;
	
	public void init(){
		
		// init make the shell as big as saved here
		setSize(width, height);
		
		// layout
		setLayout(new FormLayout());
		
		createWidgets(this);
	    
		addDrag(this);
		
		layout(true, true);
		redraw();
	}
	
	
	void createWidgets(Shell shell){
		DraggingComposite top = createWidgetTop(shell);
		DraggingComposite bottom = createWidgetBottom(shell);
		DraggingComposite left = createWidgetLeft(shell);
		DraggingComposite right = createWidgetRight(shell);
		
		addWidthResizeListener(left);
		addWidthResizeListener(right);
		addHeightResizeListener(top);
		addHeightResizeListener(bottom);
		
		top.setCursor(cursorSizeNS);
		bottom.setCursor(cursorSizeNS);
		left.setCursor(cursorSizeEW);
		right.setCursor(cursorSizeEW);
	}	
	
	/** Listener which modifies the width of the frame */
	private void addWidthResizeListener(final DraggingComposite d){
		  Listener lHeight = new Listener() {
		        Point origin;

		        public void handleEvent(Event e) {
		          switch (e.type) {
		          case SWT.MouseDown:
		            origin = new Point(e.x, e.y);
		            break;
		          case SWT.MouseUp:
		            origin = null;
		            break;
		          case SWT.MouseMove:
		            if (origin != null) {
		              Point absp = Display.getCurrent().map(d, null, e.x, e.y);
		              resizeWidth(absp.x);
		            }
		            break;
		          }
		        }
		      };
		      d.addListener(SWT.MouseDown, lHeight);
		      d.addListener(SWT.MouseUp, lHeight);
		      d.addListener(SWT.MouseMove, lHeight);
	}
	
	/** Listener which modifies the height of the frame */
	private void addHeightResizeListener(final DraggingComposite d){
		  Listener lHeight = new Listener() {
		        Point origin;

		        public void handleEvent(Event e) {
		          switch (e.type) {
		          case SWT.MouseDown:
		            origin = new Point(e.x, e.y);
		            break;
		          case SWT.MouseUp:
		            origin = null;
		            break;
		          case SWT.MouseMove:
		            if (origin != null) {
		              Point absp = Display.getCurrent().map(d, null, e.x, e.y);
		              resizeHeight(absp.y);
		            }
		            break;
		          }
		        }
		      };
		      d.addListener(SWT.MouseDown, lHeight);
		      d.addListener(SWT.MouseUp, lHeight);
		      d.addListener(SWT.MouseMove, lHeight);
	}
	
	
	DraggingComposite createWidgetTop(Shell shell){
		int widgetWidth = minWidth;
		int widgetHeight = borderWidth;

		DraggingComposite d = new DraggingComposite(shell, SWT.NONE);
		d.setSize(widgetWidth, widgetHeight);
		
		FormData formData = new FormData();
		// shell is measured wrong? Why offset "borderWidth*2"  is needed 
		formData.left = new FormAttachment(50, -widgetWidth/2-borderWidth*2); 
		formData.top = new FormAttachment(0);
		formData.bottom = new FormAttachment(0, widgetHeight);
		d.setLayoutData(formData);
		
		return d;
	}
	
	DraggingComposite createWidgetBottom(Shell shell){
		int widgetWidth = minWidth;
		int widgetHeight = borderWidth;

		DraggingComposite d = new DraggingComposite(shell, SWT.NONE);
		d.setSize(widgetWidth, widgetHeight);
		
		FormData formData = new FormData();
		// shell is measured wrong? Why offset "borderWidth*2"  is needed 
		formData.left = new FormAttachment(50, -widgetWidth/2 -borderWidth*2);
		formData.top = new FormAttachment(100, -widgetHeight);
		formData.bottom = new FormAttachment(100);
		d.setLayoutData(formData);
		
		return d;
	}
	
	DraggingComposite createWidgetLeft(Shell shell){
		int widgetWidth = borderWidth;
		int widgetHeight = minHeight;

		DraggingComposite d = new DraggingComposite(shell, SWT.NONE);
		d.setSize(widgetWidth, widgetHeight);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(0, widgetWidth);
		formData.top = new FormAttachment(50, -widgetHeight/2);
		d.setLayoutData(formData);
		
		return d;
	}
	
	DraggingComposite createWidgetRight(final Shell shell){
		int widgetWidth = borderWidth;
		int widgetHeight = minHeight;

		final DraggingComposite d = new DraggingComposite(shell, SWT.NONE);
		d.setSize(widgetWidth, widgetHeight);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(100, -widgetWidth);
		formData.right = new FormAttachment(100);
		formData.top = new FormAttachment(50, -widgetHeight/2);
		d.setLayoutData(formData);
		
		return d;

	}
	

	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ImageCropShell shell = new ImageCropShell(display);
			
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	
	
	private static Region border(int x, int y, int w, int h){
	    Region region = new Region();
	    region.add(new Rectangle(x,y, w, h));
	    region.subtract(new Rectangle(x+borderWidth,y+borderWidth, w-2*borderWidth, h-2*borderWidth));
	    return region;
	}
	
	/** allow dragging the shell */
	private static void addDrag(final Shell shell){
		final Display display = Display.getDefault();
	    Listener l = new Listener() {

	    	// position relative to the shell. Used to find left top corner 
	        Point origin;

	        public void handleEvent(Event e) {
	          switch (e.type) {
	          case SWT.MouseDown:
	            origin = new Point(e.x, e.y);
	            System.out.println(String.format("Click e.x %s , e.y : %s", e.x, e.y));
	            break;
	          case SWT.MouseUp:
	            origin = null;
	            break;
	          case SWT.MouseMove:
	            if (origin != null) {
	              Point p = display.map(shell, null, e.x, e.y);
	              shell.setLocation(p.x - origin.x, p.y - origin.y);
	            }
	            break;
	          }
	        }
	      };
	      shell.addListener(SWT.MouseDown, l);
	      shell.addListener(SWT.MouseUp, l);
	      shell.addListener(SWT.MouseMove, l);
	      
	      shell.setCursor(cursorHand);
	}
	

	public ImageCropShell(Display display,Shell parent) {
		this(display);
		setParent(parent);
	}
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public ImageCropShell(Display display) {
		super(display, SWT.NO_TRIM | SWT.ON_TOP);
		init();
	}
	
	
	private  void resizeWidth(int cursorXAbs){
		// retrieve the nearest side (left or right)
		Rectangle bounds = getBounds();
		Rectangle boundsNew = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		Side nearestSide;
		if(Math.abs(bounds.x-cursorXAbs) <= Math.abs(bounds.width-cursorXAbs) ){
			nearestSide = Side.LEFT;
		}else{
			nearestSide = Side.RIGHT;
		}
		
		int widthNew = -1;
		// if left - calculate x and width
		if(nearestSide == Side.LEFT){
			widthNew = Math.abs(cursorXAbs - bounds.width);
			boundsNew.x = cursorXAbs; // modify left coordinate
		}
		
		// if right - calculate width
		if(nearestSide == Side.RIGHT){
			widthNew = Math.abs(cursorXAbs - bounds.x);
			boundsNew.width = cursorXAbs;// modify right coordinate
		}
			
		// check minWidth constraint
		if(widthNew<minWidth){
			// do not resize
			return;
		}
		
		// correct aspect Ratio
		if(aspectRatioXMultiplier > 0){
			int heightNew = (int) Math.round(widthNew * 1/aspectRatioXMultiplier);
			boundsNew.height = boundsNew.y + heightNew;
		}
		
		// modify if yes
		setBounds(boundsNew.x, boundsNew.y, boundsNew.width, boundsNew.height);
	}
	
	private  void resizeHeight(int cursorYAbs){
		// retrieve the nearest side (top or bottom)
		Rectangle bounds = getBounds();
		Rectangle boundsNew = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		Side nearestSide;
		if(Math.abs(bounds.y-cursorYAbs) <= Math.abs(bounds.height-cursorYAbs) ){
			nearestSide = Side.TOP;
		}else{
			nearestSide = Side.BOTTOM;
		}
		
		int heightNew = -1;
		// if top - calculate y and height
		if(nearestSide == Side.TOP){
			heightNew = Math.abs(cursorYAbs - bounds.height);
			boundsNew.y = cursorYAbs; // modify left coordinate
		}
		
		// if bottom - calculate height
		if(nearestSide == Side.BOTTOM){
			heightNew = Math.abs(cursorYAbs - bounds.y);
			boundsNew.height = cursorYAbs;// modify right coordinate
		}
		
			
		// check minHeight constraint
		if(heightNew<minHeight){
			// do not resize
			return;
		}
		
		// correct aspect Ratio
		if(aspectRatioXMultiplier > 0){
			int widthNew = (int) Math.round(heightNew * aspectRatioXMultiplier);
			boundsNew.width = boundsNew.x + widthNew;
		}
	
		// modify if yes	
		setBounds(boundsNew.x, boundsNew.y, boundsNew.width, boundsNew.height);
	}

	/** Set Size makes the shell resize the frame. */
	@Override
	public void setSize(int width, int height) {
		
		// init aspect Ratio by height
		if(aspectRatioXMultiplier > 0){
			width = (int) Math.round(height * aspectRatioXMultiplier);
		}
		
		super.setSize(width, height);
		
		// cache
		this.width = width;
		this.height = height;
		
		// modify the border on set size 
	    Region region = new Region();
	    region.add(border(0,0, width, height));
		setRegion(region);
	}
	
	/**
	 *  Bound of non rect shells are returned wrong. Correct that.
	 *  @return two coordinates: top left and bottom right
	 */
	@Override
	public Rectangle getBounds() {
		Point pos = getLocation();
		return new Rectangle(pos.x, pos.y, pos.x+width, pos.y+height);
	}
	
	/** Set size by bounds */
	public void setBounds(int xLeft, int yTop, int xRight, int yBottom) {
		int temp;
		if(xLeft > xRight){
			temp = xLeft;
			xLeft = xRight;
			xRight = temp;
		}
		if(yTop > yBottom){
			temp = yTop;
			yTop = yBottom;
			yBottom = temp;
		}
		setLocation(xLeft,yTop);
		setSize(xRight-xLeft, yBottom-yTop);
	}
	
	public Rectangle getRectangleWithinFrame(){
		Rectangle result = getBounds();
		result.width = result.width - result.x;
		result.height = result.height - result.y;
		
		// add border to x,y
		result.x = result.x + borderWidth;
		result.y = result.y + borderWidth;
		
		// subtract border from width, height
		result.width = result.width - 2*borderWidth;
		result.height = result.height - 2*borderWidth;
		
		return result;
	}
	
	/** 
	 * Pass in the ratio of shell's width, compared to height.
	 * The given value will be used to calculate the height from  
	 * */
	public void setAspectRatioX(double multiplierX){
		this.aspectRatioXMultiplier = multiplierX;
	}
	
	public void disableAspectRatio(){
		this.aspectRatioXMultiplier = -1;
	}
	
	@Override
	protected void checkSubclass() {
		// allow inherit from shell
	}
	
	public static Image getScreenshot(Display display, int x, int y, int width, int height){
		GC gc = new GC(display);
        final Image image = new Image(display, width,height);
        gc.copyArea(image, x, y);
        gc.dispose();		
        return image;
	}
	
	
	public class DraggingComposite extends Composite {

		/**
		 * Create the composite.
		 * @param parent
		 * @param style
		 */
		public DraggingComposite(Composite parent, int style) {
			super(parent, style);
			setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));

		}

		@Override
		protected void checkSubclass() {
			// Disable the check that prevents subclassing of SWT components
		}

	}
}
