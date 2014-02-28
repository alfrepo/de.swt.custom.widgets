package de.swt.custom.widgets.pshelf;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PaletteShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Computes the size of the item, but not the position.
 * The Position is computed by the {@link Shelf}.
 * 
 * <b>Attention:</b> Use this Renderer with {@link Shelf} and {@link ShelfItem} objects only.
 * 
 * @author alf
 */
public class ShelfRendererItemWidget extends PaletteShelfRenderer {

    private PShelf parent;
    private static final int SPACING = 4;
    private static final int GRADIENT_WIDTH = 40;
    private static final int IMAGE_AND_HEADERTEST_SPACE = 6;

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(GC gc, Object value) {
        ShelfItem item = (ShelfItem) value;

        // make the item know it's bounds, so it will be able to compute It's widget's position.
        item.setBounds(getBounds());

        // compute widget size, AFTER the item-bounds are set, because they are used to compute
        // widget position
        if (item.getWidget() != null) {
            item.getWidget().computeSize(SWT.DEFAULT, getBounds().height - 4);
        }

        // Color back = parent.getBackground();
        Color fore = parent.getForeground();

        gc.fillRectangle(0, getBounds().y, getBounds().width - 1, getBounds().height - 1);

        gc.setForeground(getShadeColor());

        gc.fillGradientRectangle(0, getBounds().y, GRADIENT_WIDTH, getBounds().height - 1, false);

        gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
        gc.drawLine(0, getBounds().y, 0, getBounds().y + getBounds().height - 1);
        gc.drawLine(0, getBounds().y, getBounds().width - 1, getBounds().y);

        gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        gc.drawLine(0, getBounds().y + getBounds().height - 1, getBounds().width - 1, getBounds().y
                + getBounds().height - 1);
        gc.drawLine(getBounds().width - 1, getBounds().y, getBounds().width - 1, getBounds().y + getBounds().height - 1);

        int x = IMAGE_AND_HEADERTEST_SPACE;
        if (item.getImage() != null) {
            int y2 = (getBounds().height - item.getImage().getBounds().height) / 2;
            if ((getBounds().height - item.getImage().getBounds().height) % 2 != 0) {
                y2++;
            }

            gc.drawImage(item.getImage(), x, getBounds().y + y2);

            x += item.getImage().getBounds().width + SPACING;
        }
        gc.setForeground(fore);

        int y2 = (getBounds().height - gc.getFontMetrics().getHeight()) / 2;
        if ((getBounds().height - gc.getFontMetrics().getHeight()) % 2 != 0) {
            y2++;
        }

        if (isHover() && !isSelected()) {
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
        }

        String text = getShortString(gc, item.getText(), getBounds().width - x - 4 - item.getWidget().getWidth());
        gc.drawString(text, x, getBounds().y + y2, true);

        // make the widget render itself
        if (item.getWidget() != null) {
            Point widgetpos = item.getWidgetPosition();
            item.getWidget().draw(widgetpos.x, widgetpos.y, gc);
        }
        gc.setForeground(fore);

        if (isFocus()) {
            gc.drawFocus(1, 1, getBounds().width, getBounds().height - 1);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Remember the parent inside the subclass, since the private parent can not be accessed.
     * </p>
     * 
     * @param control
     *            - the parent
     */
    @Override
    public void initialize(Control control) {
        super.initialize(control);
        this.parent = (PShelf) control;
    }

    private static String getShortString(GC gc, String t, int width) {

        if (t == null) {
            return null;
        }

        if (t.isEmpty()) {
            return "";
        }

        if (width >= gc.stringExtent(t).x) {
            return t;
        }

        int w = gc.stringExtent("...").x;
        String text = t;
        int l = text.length();
        int pivot = l / 2;
        int s = pivot;
        int e = pivot + 1;
        while (s >= 0 && e < l) {
            String s1 = text.substring(0, s);
            String s2 = text.substring(e, l);
            int l1 = gc.stringExtent(s1).x;
            int l2 = gc.stringExtent(s2).x;
            if (l1 + w + l2 < width) {
                text = s1 + "..." + s2;
                break;
            }
            s--;
            e++;
        }

        if (s == 0 || e == l) {
            text = text.substring(0, 1) + "..." + text.substring(l - 1, l);
        }

        return text;
    }
}
