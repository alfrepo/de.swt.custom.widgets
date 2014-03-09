package de.swt.custom.widgets.accordion;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alle Objekte, welche im Haccoridon liegen und animierbar sind - sind AnimatableItems.
 * Die Groesse von AnimatableItems wird vom Haccordion kontrolliert.
 * Der Stack und die HaccordionItems sind AnimatableItems.
 */
public class AnimatableItem extends Canvas {

    private static final Logger LOG = LoggerFactory.getLogger(AnimatableItem.class);

    // diese Werte kontrollieren die Tatsächliche Größe des AnimatableItem
    protected int itemWidth = 0;
    protected int itemHeight = 0;

    /**
     * Only {@link HAccordion} is allowed as parent.
     * 
     * @param parent
     *            - parent
     * @param style
     *            - SWT style bit
     */
    protected AnimatableItem(HAccordion parent, int style) {
        super(parent, style);
    }

    // Overridden
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        // Do not consider the HINTS. ItemSize is set by Haccordion.
        Point p = new Point(itemWidth, itemHeight);

        LOG.debug("AnimatableItem width: {}", itemWidth);
        return p;
    }

    /**
     * Nur parents vom Typ {@link HAccordion} werden akzeptiert.
     * 
     * @param parent
     *            - Haccordion parent
     * @return
     *         - true on success
     */
    protected boolean setParent(HAccordion parent) {
        return super.setParent(parent);
    }

    /**
     * <h2>Depreciated method.</h2> Nur Composite parents sind erlaubt.
     * Kann diesen Konstruktor nicht verstecken. Beim Aufruf dieses Konstruktors wird eine Exception
     * geworfen.
     * 
     * @param parent
     *            - the parent
     * @return - true on success
     */
    @Override
    public boolean setParent(Composite parent) {
        throw new IllegalArgumentException(
                "Usage of setParent(Composite parent) constructor is illegal. Only parents of type HAccordion are allowed. Use the setParent(HAccordion parent) constructor.");
    }

    /**
     * Nur parents vom Typ {@link HAccordion} werden akzeptiert, deswegen ist casting legitim hier.
     * 
     * @return - the parent composite
     */
    @Override
    public HAccordion getParent() {
        return (HAccordion) super.getParent();
    }

    /**
     * Da die Items die Kontrolle ueber eigene Breite uebernehmen kann man die Breite explizit
     * setzen
     * 
     * @param width
     *            Itembreite
     */
    public void setWidth(int width) {
        this.itemWidth = width;
    }

    public int getWidth() {
        return this.itemWidth;
    }

    public void setHeight(int height) {
        this.itemHeight = height;
    }

    public int getHeight() {
        return this.itemHeight;
    }

    /**
     * Dieser Wert wird benutzt, um ein AnimatableItem vollständig auszuklappen.
     * Ohne diesen Wert ist es unbekannt, wie Weit ein AnimatableItem ausgeklappt werden kann.
     * Dieser Wert ist zB. fГ�?r den ItemStack relevant.
     * 
     * @return die Breite, welche benutzt wird, um dieses item vollstРґndig zu expandieren.
     */
    protected int getMaxWidth() {
        return 0;
    }

    /**
     * Checks if this item is located in the parent Accordion on the left side, from the second
     * item. <br>
     * 
     * @param item
     *            - the second item
     * @return only when both items are found in accordion and [this][item] it returns true;
     *         otherwise false;
     */
    protected boolean leftFrom(AnimatableItem item) {
        Control[] children = this.getParent().getChildren();
        int posThis = -1;
        int posSec = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] == this) {
                posThis = i;

            } else if (children[i] == item) {
                posSec = i;
            }
        }

        // evaluate
        if (posSec < 0 || posThis < 0) {
            return false; // if one of the items wasn't found
        } else if (posThis < posSec) {
            return true; // both found, and right disposition
        }
        return false; // both found, but wrong disposition
    }

    /**
     * Checks if this item is located in the parent Accordion on the right side, from the second
     * item. <br>
     * 
     * @param item
     *            - the second item
     * @return only when both items are found in accordion and [item][this], it returns true;
     *         otherwise false;
     */
    protected boolean rightFrom(AnimatableItem item) {
        Control[] children = this.getParent().getChildren();
        int posThis = -1;
        int posSec = -1;

        // find both item's positions
        for (int i = 0; i < children.length; i++) {
            if (children[i] == this) {
                posThis = i;
            } else if (children[i] == item) {
                posSec = i;
            }
        }

        // evaluate
        if (posSec < 0 || posThis < 0) {
            return false;
        } else if (posThis > posSec) {
            return true;
        }
        return false; // both found, but wrong disposition
    }
    
    /**
     * 
     * Hides the content of the {@link AnimatableItem} during the animation is running.
     * This is the place to display a preloader!
     * 
     * @param hide - a flag shows whether to hide or to show the content
     */
    protected void setContentVisible(boolean isVisible){
    	// nothing on default 
    }
}
