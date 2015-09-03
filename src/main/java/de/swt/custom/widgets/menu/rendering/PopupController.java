package de.swt.custom.widgets.menu.rendering;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import de.swt.custom.utils.UtilsUI;

/**
 * Controls menu popups.
 * 
 * @author alf
 *
 */
public class PopupController{

	private Control currentControl;
	
	// the popups on menu level x are saved here
	ConcurrentHashMap<Integer, Shell> popupsOnMenuLvl = new ConcurrentHashMap<Integer, Shell>();
	
	// the controls, which triggered the popup creation. Usually it will be the menuitem on previous menu lvl
	ConcurrentHashMap<Integer, Control> menuItemOnMenuLvl = new ConcurrentHashMap<Integer, Control>();
	
	Display display;
	Shell currentShell;
	
	MouseTracker mouseTracker;
	
	public PopupController() {
		this(Display.getCurrent().getActiveShell());
	}
	
	public PopupController(Shell currentShell) {
		display = Display.getCurrent();
		this.currentShell = currentShell;
	}
	
	private void startMouseTracking(){
		if(mouseTracker==null){
			mouseTracker = new MouseTracker().start();
		}
	}
	
	private void stopMouseTracking(){
		// TODO alf
//		display.removeFilter(SWT.MouseMove, this);
//		display.removeFilter(SWT.MouseExit, this);
		if(mouseTracker!=null){
			mouseTracker.isRunning = false;
			mouseTracker = null;
		}
	}

	public void addShell(Integer lvl, Control triggeringMenuitem, Shell popup){
		// close prev popup on this lvl
		closeShell(lvl);
				
		// remember the shell
		popupsOnMenuLvl.put(lvl, popup);
		
		//remember the control
		menuItemOnMenuLvl.put(lvl, triggeringMenuitem);
		
		startMouseTracking();
	}
	
	public void closeShells(){
		for(Integer lvl:popupsOnMenuLvl.keySet()){
			closeShell(lvl);
		}
	}
	
	public void closeShell(int lvl){
		Shell shell = this.popupsOnMenuLvl.get(lvl);
		
		if(shell != null && !shell.isDisposed()){
			shell.setVisible(false);
			shell.close();
		}
		this.popupsOnMenuLvl.remove(lvl);

		
		// forget the control which triggered the popup
		this.menuItemOnMenuLvl.remove(lvl);
		
		// stop listening for mouse, when last shell was closed. Will start listening when a new Shell was created and added from outside this class
		if(this.popupsOnMenuLvl.isEmpty()){
			stopMouseTracking();	
		}
	}

	private void handleEvent(int displayX, int displayY) {
		
		// skip movements over last control
		if(currentControl != null && !currentControl.isDisposed()){
			boolean overLast = UtilsUI.isCursorInControl(currentControl, displayX, displayY);
			if(overLast){
				// still over last control - nothing to do
				return; 	
			}
		}
		
		// check if over any other popupShell or menuItem
		
		List<Integer> keys = new ArrayList<Integer>(popupsOnMenuLvl.keySet());
		Integer[] keysArray = keys.toArray(new Integer[keys.size()]);
		
		// reverse level order. Start with the submenus for the subshells to be checked first
		Arrays.sort(popupsOnMenuLvl.keySet().toArray(new Integer[keys.size()]), Collections.reverseOrder());
		
		for(Integer lvl: this.popupsOnMenuLvl.keySet()){
			Shell popup = this.popupsOnMenuLvl.get(lvl);
			Control menuItem= this.menuItemOnMenuLvl.get(lvl);

			if(popup==null || popup.isDisposed() || menuItem==null || menuItem.isDisposed() ){
				continue; // this shell should already have been closed
			}
			
			boolean isOverShell = false;
			boolean isOverMenuItem = false;
			boolean isOverChildShell = false;
			
			// over shell?
			isOverShell = UtilsUI.isCursorInControl(popup, displayX, displayY);
			if(isOverShell){
				this.currentControl = popup;
			}
			
			// not over shell? try menuItem
			if(!isOverShell){
				isOverMenuItem = UtilsUI.isCursorInControl(menuItem, displayX, displayY);
				if(isOverMenuItem){
					this.currentControl = menuItem;	
				}
			}
			
			// not over MenuItem? Òry child shells, e.g. ContextMenu Shells
			if(!isOverMenuItem){
				Object dependantControls = popup.getData(Renderer.MANAGED_SHELLS_LIST);
				
				if(dependantControls != null && (dependantControls instanceof List<?>)){
					for( Object c:((List<?>)dependantControls)){
						if(c instanceof Widget){
							Widget widget = (Widget) c;
							isOverChildShell = UtilsUI.isCursorInWidget(widget, displayX, displayY);
							if(isOverChildShell){
//								this.currentControl = widget;
								break;
							}
						}
					}
				}
			}
			
			
			
			if(isOverShell || isOverMenuItem || isOverChildShell){
				
				/* Achtung: the events will arrive asynchronously.
				 * A this method may be started multiple times in parallel, so first remember the currentControl first to acquire lock remember as current control and return */
				this.currentControl = popup;
				
				//remove popups on lvl > lvl_shell+1
				for(Integer lvl2 : this.popupsOnMenuLvl.keySet()){
					if(lvl2 > lvl+1){
						closeShell(lvl2);
					}
				}
				return;
			}
		}
		
		// otherwise close all shells
		closeShells();
	}
	
	
	class MouseTracker implements Runnable{
		public boolean isRunning = true;
		public static final int mouseCHeckIntervalMs = 100;
		
		MouseTracker start(){
			new Thread(this).start();
			return this;
		}
		
		public void run() {
			while(isRunning){
				// on UI thread
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						Point cl = display.getCursorLocation();
						PopupController.this.handleEvent(cl.x, cl.y);						
					}
				});
				
				// sleep				
				try {
					Thread.sleep(mouseCHeckIntervalMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
