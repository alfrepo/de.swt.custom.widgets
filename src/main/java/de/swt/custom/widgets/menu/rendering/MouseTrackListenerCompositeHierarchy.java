package de.swt.custom.widgets.menu.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import de.swt.custom.utils.UtilsUI;

public abstract class MouseTrackListenerCompositeHierarchy{

	private Boolean isAlreadyWithinControl = false; 
	private MouseTrackListener mouseTrackListenerAllChildren;
	private List<Control> mainControls = Collections.synchronizedList(new ArrayList<Control>());
	
	public void forget(final Control control){
		if(control==null || control.isDisposed()){
			return;
		}
		if(control instanceof Composite){
			forgetComposite((Composite)control);
		}else{
			control.removeMouseTrackListener(mouseTrackListenerAllChildren);
		}
		this.mainControls.remove(control);
		
	}
	
	private void forgetComposite(final Composite composite){
		forgetRek(composite, mouseTrackListenerAllChildren);
	}
	
	public void listen(final Control control){
		if(control == null || control.isDisposed()){
			return;
		}
		if(control instanceof Composite){
			listenComposite((Composite)control);
		}else{
			control.addMouseTrackListener(mouseTrackListenerAllChildren);
		}
		this.mainControls.add(control);
	}
	
	private void listenComposite(final Composite control){
		mouseTrackListenerAllChildren = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				synchronized (isAlreadyWithinControl) {
					if(!isAlreadyWithinControl){
						isAlreadyWithinControl = true;
						// trigger mouse enter 
						MouseTrackListenerCompositeHierarchy.this.mouseEnter(e);
					}
				}
		
			}
			@Override
			public void mouseExit(MouseEvent e) {
				try{
					Point p = Display.getCurrent().getCursorLocation();
					if(!UtilsUI.isCursorInControl(control, p.x, p.y)){
						isAlreadyWithinControl = false;
						// trigger mouse enter 
						MouseTrackListenerCompositeHierarchy.this.mouseExit(e);
					}
				}catch(SWTException swte){
					// widget is disposed - nothing
				}
				
			}
		};
		
		// listen to the whole hierarchy
		listenRek(control, mouseTrackListenerAllChildren);
	}
	
	private void listenRek(Composite composite, MouseTrackListener l){
		composite.addMouseTrackListener(l);
		if(composite.getChildren().length>0){
			for(Control child: composite.getChildren()){
				if(child instanceof Composite){
					listenRek((Composite) child, l);
				}else{
					child.addMouseTrackListener(l);
				}
			}
		}
	}
	
	private void forgetRek(Composite composite, MouseTrackListener l){
		composite.removeMouseTrackListener(l);
		if(composite.getChildren().length>0){
			for(Control child: composite.getChildren()){
				if(child instanceof Composite){
					forgetRek((Composite) child, l);
				}else{
					child.removeMouseTrackListener(l);
				}
			}
		}
	}
	
	abstract void mouseEnter(MouseEvent e);

	abstract void mouseExit(MouseEvent e);


}
