package de.swt.custom.widgets.sash;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * This class represents the stripe, which one can drag up and down to redistribute the space between upper and lower {@link SashForm} containers.
 * @author skip
 *
 */
public class Sash extends Composite {
	private int width = 25;
	private int height = 15;
	private SashToken tokenLeft;
	private SashToken tokenRight;
	
	public Sash(Composite parent, int style) {
		super(parent, style);
		
		setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		
		tokenLeft = new SashToken(this, SWT.NONE);
		tokenLeft.setSize(width, height);
		tokenRight = new SashToken(this, SWT.NONE);
		tokenRight.setSize(width, height);
		
		doLayout();
	}
	
	protected SashToken getSashTokenLeft(){
		return tokenLeft;
	}
	
	protected SashToken getSashTokenRight(){
		return tokenRight;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return  new Point(width, height);
	}
	
	@Override
	public Point getSize() {
		Point p =  super.getSize();
		return p;
	}
	
	private void doLayout(){
		this.setLayout(new FormLayout());
		
		// left token is places left from the middle
		FormData fd_tokenLeft = new FormData();
		fd_tokenLeft.right = new FormAttachment(50,0);
		tokenLeft.setLayoutData(fd_tokenLeft);

		// right token is places right from the middle
		FormData fd_tokenRight = new FormData();
		fd_tokenRight.left = new FormAttachment(50,0);
		tokenRight.setLayoutData(fd_tokenRight);
	}
	
	public void resizeOnDrag(final SashForm sash, final SashFormExpandable sashFormExpandable){
		setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				final int sashHeight  = sash.getSize().y;
				final Display d = Display.getCurrent();
				
				// first switch the sash to expand-both mode
				sashFormExpandable.disableMaximizationOfShellContainers();
				
				// listen to movement and resize on move
				final Listener mouseMovement = new Listener() {
					@Override
					public void handleEvent(Event event) {
						Point absMousePos = d.getCursorLocation();
						
						
						// 1. get the Cursor-Location inside of the SashForm!
						Point relMousePos = sash.toControl(absMousePos);
						System.out.println(relMousePos);
						
						// let the mouseY be 0 < mouseY < sash.height 
						int mouseY = Math.max( Math.min(relMousePos.y, sash.getSize().y), 0);
						
						// how did it change the weight? The weight is proportional to the sashForm size
						int w1 = mouseY;
						int w2 = sash.getSize().y  - mouseY; 
						
						// don't allow to hide the bottom-composite, more than SashToken, since SashToken is contained by bottom-composite 
						w1 = Math.min(w1, sashHeight-height);
						w2 = Math.max(w2, height);
						
						System.out.println("w1: "+w1 + " w2: "+w2);
						
						sash.setWeights(new int[]{w1, w2});
					}
				};
				Display.getCurrent().addFilter(SWT.MouseMove, mouseMovement);
				
				// disable movement listener on global mouseup
				Display.getCurrent().addFilter(SWT.MouseUp, new Listener() {
					@Override
					public void handleEvent(Event event) {
						Display.getCurrent().removeFilter(SWT.MouseMove, mouseMovement);
					}
				});
			}
		});
		
	}
	
}
