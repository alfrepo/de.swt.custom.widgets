package de.swt.custom.widgets.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


public class ConnectorDisapearingShell {

	private static final int OVERLAP = 8;
	
	private Control control; 
	private Shell pop;
	
	private Display display = Display.getDefault();
	
	private MouseTrackAdapter mouseTrackAdapterControl;
	private Listener listenerStopHidingOnClick;
	private Listener listenerMouseMove;
	
	private Point cursorLocation;
	
	public void connect(Control control, Shell popup){
		this.control = control;
		this.pop = popup;
		
		// onmouseover display the shell
		this.mouseTrackAdapterControl = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Point p = getAbsControlPosition();
				showPopup(p);
			}
		};
		
		
		// onclick on shell stop hiding the shell on exit
		this.listenerStopHidingOnClick = new Listener() {
			@Override
			public void handleEvent(Event e) {
				if(isCursorInControl(pop, e.x, e.y)){
					return;
				}
				// stop hiding shell
				display.removeFilter(SWT.MouseMove, listenerMouseMove);
				
				// now this listener does not make sence
				display.removeFilter(SWT.MouseDown, ConnectorDisapearingShell.this.listenerStopHidingOnClick);
			}
		};
		
		// onMouseMove should hide popup
		this.listenerMouseMove = new Listener() {
			@Override
			public void handleEvent(Event e) {
				if(!pop.isVisible()){
					return;
				}
				if(!isCursorInControlOrPopup()){
					hidePopup();	
				}
			}
		};
		
		// register
		control.addMouseTrackListener(this.mouseTrackAdapterControl);
		display.addFilter(SWT.MouseMove, listenerMouseMove);

		// on dispose of control
		ensureWhenWIdgetIsDisposed();
	}
	
	public void dispose(){
		if(!control.isDisposed()){
			control.removeMouseTrackListener(mouseTrackAdapterControl);	
		}
		display.removeFilter(SWT.MouseDown, ConnectorDisapearingShell.this.listenerStopHidingOnClick);
		display.removeFilter(SWT.MouseMove, ConnectorDisapearingShell.this.listenerMouseMove);
	}
	
	private Point getAbsControlPosition(){
		return control.toDisplay(OVERLAP, OVERLAP);
	}
	
	private void showPopup(Point pos){
		Point size = pop.getSize();
		int x = pos.x-OVERLAP;
		int y = pos.y-size.y+OVERLAP;
		ConnectorDisapearingShell.this.pop.setLocation(x, y);
		ConnectorDisapearingShell.this.pop.moveAbove(ConnectorDisapearingShell.this.control);
		ConnectorDisapearingShell.this.pop.setVisible(true);
		display.addFilter(SWT.MouseDown, ConnectorDisapearingShell.this.listenerStopHidingOnClick);
	}
	
	private void hidePopup(){
		ConnectorDisapearingShell.this.pop.setVisible(false);
	}
	
	private void ensureWhenWIdgetIsDisposed(){
		control.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		pop.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}
	
	private boolean isCursorInControl(Control control, int cursX, int cursY){
		Rectangle rect = control.getBounds();
		return rect.contains(cursX, cursY);
	}
	
	private boolean isCursorInControlOrPopup(){
		cursorLocation = display.getCursorLocation();
		boolean isInControl =  isCursorInControl(control, cursorLocation.x, cursorLocation.y);
		boolean isInPop =  isCursorInControl(pop, cursorLocation.x, cursorLocation.y);
		return isInControl || isInPop; 
	}
}
