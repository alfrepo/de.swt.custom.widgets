package de.swt.custom.widgets.pshelf;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

/**
 * Class should be used together with {@link ShelfRendererItemWidget}, because it will render the
 * {@link Widget} too.
 * 
 * @author alf
 */
public class ShelfItem extends PShelfItem implements MouseListenerBoolean {

    // Item (not Composite) is used, because ShelfItemIvu(a special Item) should be the parent and
    // Items can't own Composites.
    private ShelfItemWidget widget;
    private Shelf parent;
    private Rectangle bounds;

    boolean isOverWidget = false;

    /**
     * Creates a special {@link PShelf}
     * 
     * @param shelfIvuParent
     *            - parent
     * @param style
     *            - {@link SWT} stylebits
     */
    public ShelfItem(Shelf shelfIvuParent, int style) {
        super(shelfIvuParent, style);
        this.widget = new ShelfItemWidget(this, SWT.NONE);
        this.parent = shelfIvuParent;
    }

    /**
     * 
     * Creates a special {@link PShelf}
     * 
     * @param parent
     *            - parent
     * @param style
     *            - {@link SWT} style bits
     * @param index
     *            - index
     */
    public ShelfItem(Shelf parent, int style, int index) {
        super(parent, style, index);
    }

    @Override
    public boolean mouseDown(Event e) {
        if (widget != null && isWidgetArea(e.x, e.y)) {
            // let the widget handle the mouse event
            return widget.mouseDown(e);
        }
        return false;
    }

    @Override
    public boolean mouseUp(Event e) {
        if (widget != null && isWidgetArea(e.x, e.y)) {
            // let the widget handle the mouse event
            return widget.mouseUp(e);
        }
        return false;
    }

    @Override
    public boolean mouseOver(Event e) {
        if (widget != null && isWidgetArea(e.x, e.y)) {
            // let the widget handle the mouse event
            return widget.mouseOver(e);
        }
        return false;
    }

    @Override
    public boolean mouseOut(Event e) {
        if (widget != null && isOverWidget) {
            isOverWidget = false;
            // let the widget handle the mouse event
            return widget.mouseOut(e);
        }
        return false;
    }

    @Override
    public boolean mouseMove(Event e) {
        if (widget != null) {
            if (isWidgetArea(e.x, e.y)) {
                if (!isOverWidget) {
                    isOverWidget = true;
                    widget.mouseOver(e);
                }
                // let the widget handle the mouse event
                return widget.mouseMove(e);
            } else if (isOverWidget) {
                isOverWidget = false;
                widget.mouseOut(e);
            }
        }
        return false;
    }

    /**
     * The Widget should implement the {@link MouseListenerBoolean} to handle the MouseEvents.
     * 
     * @param widget
     *            - the widget to display inside of every item
     */
    public void setWidget(ShelfItemWidget widget) {
        this.widget = widget;
    }

    /**
     * Retrieves the Widget in the upper right corner.
     * 
     * @return the Widget
     */
    public ShelfItemWidget getWidget() {
        return widget;
    }

    public void setBounds(Rectangle boundsparameter) {
        this.bounds = boundsparameter;
    }

    public Shelf getShelf() {
        return this.parent;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * item is capable of computing it's widget's postion to pass mouseEvents to it.
     * 
     * @return - the Position of the Widget.
     */
    public Point getWidgetPosition() {
        return new Point(this.bounds.width - this.widget.getWidth(), this.bounds.y);
    }

    private boolean isWidgetArea(int x, int y) {
        if (getWidgetPosition().x < x) {
            return true;
        }
        return false;
    }
}
