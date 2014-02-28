package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * Layout can have a fixed size.
 * Children are positioned horizontally in a Row.
 * The layout will not wrap the children, so that hidden overflow becomes possible.
 * This Layout is used to position {@link HAccordionItem}s inside of the {@link HAccordion}.
 */
public class NotWrappingRowLayout extends Layout {
    // cache
    Point[] sizes;
    private int totalWidth;
    private int maxHeight;
    private int fixedHeight = 0;
    private int fixedWidth = 0;

    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        Control[] children = composite.getChildren();
        if (flushCache || sizes == null || sizes.length != children.length) {
            initialize(children);
        }
        int width = wHint;
        int height = hHint;
        if (wHint == SWT.DEFAULT) {
            if (fixedWidth > 0) {
                width = fixedWidth;
            } else {
                width = totalWidth;
            }
        }
        if (hHint == SWT.DEFAULT) {
            if (fixedHeight > 0) {
                height = fixedHeight;
            } else {
                height = maxHeight;
            }
        }
        return new Point(width, height);
    }

    @Override
    protected void layout(Composite composite, boolean flushCache) {
        Control[] children = composite.getChildren();
        if (flushCache || sizes == null || sizes.length != children.length) {
            initialize(children);
        }
        Rectangle rect = composite.getClientArea();
        int x = 0;
        int y = 0;
        int height = Math.max(rect.height, maxHeight);
        for (int i = 0; i < children.length; i++) {
            int width = sizes[i].x;
            children[i].setBounds(x, y, width, height);
            x += width;
        }
    }

    private void initialize(Control[] children) {
        totalWidth = 0;
        maxHeight = 0;
        sizes = new Point[children.length];
        for (int i = 0; i < children.length; i++) {
            sizes[i] = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            maxHeight = Math.max(maxHeight, sizes[i].y);
            totalWidth += sizes[i].x;
        }
    }

    /**
     * this Height will be used if not 0
     * 
     * @param height
     *            - height
     * */
    protected void setHeight(int height) {
        this.fixedHeight = Math.max(height, 0);
    }

    /**
     * this Width will be used if not 0
     * 
     * @param width
     *            - width
     * */
    protected void setWidth(int width) {
        this.fixedWidth = Math.max(width, 0);
    }
}
