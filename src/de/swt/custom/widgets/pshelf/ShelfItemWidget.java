package de.swt.custom.widgets.pshelf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;

import de.swt.custom.widgets.CommonResourceUtil;

/**
 * This Widget can be added to an {@link ShelfItem}.
 * 
 * 
 */
public class ShelfItemWidget extends Item implements MouseListenerBoolean {

    private ShelfItem parent;
    private Image arrowimg;
    private Image buttonimgMouseout;
    private Image buttonimgMouseover;
    private Image buttonimgMouseclick;

    private static final int DEFAULT_WIDTH = 30;
    private static final int DEFAULT_HEIGHT = 10;

    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;

    /**
     * use SWT flags: {@link SWT#MouseHover}, {@value SWT#MouseExit}
     */
    private int stateLocation = SWT.MouseExit;
    /**
     * use SWT flags: {@link SWT#MouseDown} , {@link SWT#MouseUp}
     */
    private int stateAction = SWT.MouseUp;

    /**
     * Creates a new Widget for {@link ShelfItem}
     * 
     * @param shelfItemIvu
     *            - the parent
     * @param style
     *            - {@link SWT} style bits
     */
    public ShelfItemWidget(ShelfItem shelfItemIvu, int style) {
        super(shelfItemIvu, style);
        parent = shelfItemIvu;
        arrowimg = null;
        // arrowimg = ResourceUtil.resize(arrowimg, 0.8f);
    }

    /**
     * The Renderer will ask the Widget, how large it want to be.
     * 
     * @param widthHint
     *            - the Renderer will tell the Widget, about width restriction
     * @param heightHint
     *            - the Renderer will tell the Widget, about width restriction
     * @return - the calculated width/height
     */
    public Point computeSize(int widthHint, int heightHint) {
        if (widthHint != SWT.DEFAULT) {
            width = widthHint;
        }
        if (heightHint != SWT.DEFAULT) {
            height = heightHint;
        }

        // sqare
        // return new Point(height, height);
        return new Point(width, height);
    }

    /**
     * * Triggered by the Renderer. The Renderer will set the position of the Widget.
     * 
     * @param posx
     *            - passing {@link SWT#DEFAULT} means - no restrictions.
     * @param posy
     *            - passing {@link SWT#DEFAULT} means - no restrictions.
     * 
     * @param gc
     *            - the {@link GC}
     */
    public void draw(int posx, int posy, GC gc) {

        Transform transform = new Transform(gc.getDevice());
        transform.translate(posx - 2, posy + 1);
        gc.setTransform(transform);

        int drawWidth = width;
        int drawHeight = height;

        if (buttonimgMouseover == null) {
            buttonimgMouseover = new Image(getDisplay(), drawWidth, drawHeight);
            Button button = new Button(parent.getShelf(), SWT.PUSH);
            button.setVisible(false);
            button.setAlignment(SWT.CENTER);
            button.setImage(arrowimg);
            button.setSize(drawWidth, drawHeight);
            button.setSelection(true);

            GC gcbutton = new GC(buttonimgMouseover);
            button.print(gcbutton);
            button.dispose();
        }
        if (buttonimgMouseout == null) {
            buttonimgMouseout = arrowimg;
        }
        if (buttonimgMouseclick == null) {
            buttonimgMouseclick = buttonimgMouseout;
        }

        // DRAW arrow
        int imgx = (drawWidth - arrowimg.getBounds().width) / 2;
        int imgy = (drawHeight - arrowimg.getBounds().height) / 2;

        // DRAW button
        if (stateAction == SWT.MouseDown) {
            gc.drawImage(buttonimgMouseclick, imgx, imgy);
        } else if (stateLocation == SWT.MouseHover) {
            gc.drawImage(buttonimgMouseover, 0, 0);
        } else {
            gc.drawImage(buttonimgMouseout, imgx, imgy);
        }

        transform.dispose();
        gc.setTransform(new Transform(gc.getDevice()));
    }

    // will react on Mouse
    @Override
    public boolean mouseDown(Event e) {
        this.stateAction = SWT.MouseDown;
        redraw();
        for (Listener l : this.getListeners(SWT.MouseDown)) {
            l.handleEvent(e);
        }

        return true;
    }

    @Override
    public boolean mouseUp(Event e) {
        this.stateAction = SWT.MouseUp;
        redraw();
        for (Listener l : this.getListeners(SWT.MouseUp)) {
            l.handleEvent(e);
        }
        return true;
    }

    @Override
    public boolean mouseOver(Event e) {
        stateLocation = SWT.MouseHover;
        redraw();
        for (Listener l : this.getListeners(SWT.MouseHover)) {
            l.handleEvent(e);
        }
        return true; // capture the event
    }

    @Override
    public boolean mouseMove(Event e) {
        stateLocation = SWT.MouseHover;
        redraw();
        return true;
    }

    @Override
    public boolean mouseOut(Event e) {
        stateLocation = SWT.MouseExit;
        stateAction = SWT.MouseUp;
        redraw();
        for (Listener l : this.getListeners(SWT.MouseExit)) {
            l.handleEvent(e);
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void redraw() {
        this.parent.getShelf().redraw(this.parent.getBounds().x, this.parent.getBounds().y,
                this.parent.getBounds().width, this.parent.getBounds().height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        arrowimg.dispose();
        buttonimgMouseclick.dispose();
        buttonimgMouseout.dispose();
        buttonimgMouseover.dispose();
    }

}
