package de.swt.custom.widgets.accordion; 

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

class ThreeInRowLayout extends Layout {
  
  int childHeight = 50; // fix height
  int w = 0;
  int h = 0;
  

  protected Point computeSize(Composite composite, int wHint, int hHint,
     boolean changed) {
     w = wHint;
     h = hHint;
     Control [] children = composite.getChildren();
     if (changed && children.length>0) {
    	 int childWidth = wHint / children.length;
         for(Control c: children){
        	 c.computeSize(childWidth, childHeight, true);
         }
     }
     return new Point(wHint, hHint);
  }
 
  protected void layout(Composite composite, boolean changed) {
     Control [] children = composite.getChildren();
     if (children.length>0) {
    	 int childWidth = composite.getSize().x / children.length;
    	 int posX=0;
    	 int posY=0;
         for(Control c: children){
        	 Point p = c.computeSize(childWidth, childHeight, true);
        	 c.setBounds(posX, posY, p.x, p.y);
        	 posX += p.x; // move to the right
         }
     }
  }
}