package de.swt.custom.widgets.accordion;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Dieses Obekt wird innerhalb eines jeden Haccodionitems platziert.
 * Es Enthält den Inhalt (content) des Haccodionitems.
 */
public class HAccordionClientArea extends Composite {

    /**
     * The HAccordionClientArea
     * 
     * @param parent
     *            - parent composite of the area
     * @param style
     *            - the java style bit
     */
    protected HAccordionClientArea(final Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Removes all children.
     */
    public HAccordionClientArea clearContent() {
        for (Control control : this.getChildren()) {
            control.dispose();
        }
        return this;
    }
    
    
    
    // XXX test - try hiding children during animation
    
    boolean childrenHidden = false;
    public void hideChildren(boolean hide){
    	this.setVisible(hide);
    	this.childrenHidden = hide;
    }
    
    private Control[] noChildrenArray = new Control[0];
    @Override
    public Control[] getChildren() {
    	if(childrenHidden){
    		return noChildrenArray;
    	}else{ 
    		return super.getChildren();	
    	}
    }
}
