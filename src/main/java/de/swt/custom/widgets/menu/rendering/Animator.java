package de.swt.custom.widgets.menu.rendering;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.AbstractEffect;
import org.eclipse.nebula.animation.effects.SetColorEffect;
import org.eclipse.nebula.animation.effects.SetColorEffect.IColoredObject;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.swt.custom.widgets.menu.MenuItemClickable;

public class Animator {

	private int MENUITEM_HIGHLIGHTING_DURATION_MS = 300;
	private IMovement itemAnimationEasingFunction = new LinearInOut();
	private AbstractEffect predescessorEffect;
	
	private Composite compositeMenuItem;
	private MenuItemClickable menuItemClickable;
	
	private Color notHighlightedColor = new Color(Display.getCurrent(), 255, 255, 255);
	
	public Animator(MenuItemClickable menuItemClickable, Composite compositeMenuItem, Style style, int lvl){
		this.compositeMenuItem = compositeMenuItem;
		this.menuItemClickable = menuItemClickable;
		
		this.notHighlightedColor = getNotHighlightedColor(lvl, style);
		
		this.MENUITEM_HIGHLIGHTING_DURATION_MS = style.getMENUITEM_HIGHLIGHTING_DURATION_MS();
	}
	
	
	private Color getNotHighlightedColor(int lvl, Style style) {
		if(lvl<=1){
			return style.getMENU_PAGE_COLOR_LVL1();
		}
		return style.getMENU_PAGE_COLOR_LVL2();
	}


	public void highlightMenuItem(){
		Color currentColor = compositeMenuItem.getBackground();
		Color highlightedColor = menuItemClickable.getColor();
		animateMenuItemBg(currentColor, highlightedColor);
	}
	
	public void unhighlightMenuItem(){
		Color currentColor = compositeMenuItem.getBackground();
		animateMenuItemBg(currentColor, notHighlightedColor);
	}
	
	public void dispose(){
		notHighlightedColor.dispose();
	}
	
	private void animateMenuItemBg(Color startColor, Color targetColor){
		try{
			// cancel previous animation for this item
			if(predescessorEffect != null && !predescessorEffect.isDone()){
				predescessorEffect.cancel();
			}
			
			// create a color changing effect
			predescessorEffect = new SetColorEffect(
					getIColoredObject(compositeMenuItem), 	// object 
					startColor, 							// color to start animation from
					targetColor, 							// destination color
					MENUITEM_HIGHLIGHTING_DURATION_MS, 				// duration
					itemAnimationEasingFunction, 			// onstop 
					null, 									// Runnable callback on stop 
					null);									// Runnable callback on cancel
			
			
			// remember the animation and start 
			new AnimationRunner().runEffect(predescessorEffect);
						
		}catch(SWTException disposedException){
			// the item was disposed during animation execution. The listener will unregister itselfe soon
		}

	}
	
	private IColoredObject getIColoredObject(final Composite composite){
		return new IColoredObject() {
			
			@Override
			public void setColor(Color c) {
				try{
					composite.setBackground(c);	
				}catch(SWTException disposedException){
					// the composite is disposed
				}
				
			}
			
			@Override
			public Color getColor() {
				try{
					return composite.getBackground();
				}catch(SWTException disposedException){
					// the composite is disposed
					return null;
				}
			}
		};
	}
}
