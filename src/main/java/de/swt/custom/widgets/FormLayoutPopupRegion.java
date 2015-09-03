package de.swt.custom.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class FormLayoutPopupRegion extends Composite {

    private static final int ANIMATION_DELAY_MS = 10;

    static final int FORM_ATTACHEMENT_BOTTOM = 100;

    boolean isOpen = true;

    private Integer height;
    private Integer heightBackup = null;
    private Integer width = -1;

    private FormData wrapperFormData;

    Composite wraper;
    Composite clientArea;

    /**
     * Use the height, calculated by this container, otherwise it does not make sense to use it.
     * 
     * To work properly - give this widget a possibility to control it's own height,
     * This is done by using the FormLayout for it and setting only the top OR bottom constraint.
     * 
     * @param parent
     * @param style
     */
    public FormLayoutPopupRegion(Composite parent, int style, int height) {
        super(parent, style);

        // TODO alf: implement the given height to be the MIN Height

        // INIT
        wraper = new Composite(this, SWT.NONE);

        // LAYOUT
        this.setLayout(new FormLayout());
        wrapperFormData = new FormData();
        wrapperFormData.height = height;

        wrapperFormData.top = new FormAttachment(0, height); // offset of height
        // wrapperFormData.bottom = new FormAttachment(FORM_ATTACHEMENT_BOTTOM, 0);
        wrapperFormData.left = new FormAttachment(0, 0);
        wrapperFormData.right = new FormAttachment(FORM_ATTACHEMENT_BOTTOM, 0);
        wraper.setLayoutData(wrapperFormData);

        // DEFAULT Layout
        wraper.setLayout(new FillLayout());

        // HOOK
        createClientArea(wraper);

        // TODO del
        // Display.getDefault().addFilter(SWT.KeyDown, new Listener() {
        //
        // @Override
        // public void handleEvent(Event event) {
        // if (isOpen) {
        // close();
        // System.out.println("Close");
        // return;
        // }
        // open();
        // System.out.println("Open");
        // }
        // });

        setHeight(height);
    }

    /**
     * This will override the layout calculated height.
     * 
     * @param height
     *            - if < 0, then the layout calculated height will be used
     */
    public final void setHeight(int height) {
        this.height = height;
        this.wrapperFormData.height = height;
    }

    /**
     * Override to create the client area here
     * 
     * @param parent
     */
    public void createClientArea(Composite parent) {
        parent.setLayout(new FillLayout());

        clientArea = new Composite(parent, SWT.NONE);
        clientArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        Button b = new Button(clientArea, SWT.PUSH);
        b.setText("Button");
        b.pack();
    }

    /**
     * getContainer
     * 
     * @return - Returns the Composite, which should be used as parent, to create content
     *         Composites.
     */
    public Composite getContainer() {
        return wraper;
    }

    /**
     * Will clear the current container from content.
     */
    public void clear() {
        for (Control c : wraper.getChildren()) {
            c.dispose();
        }
    }

    /**
     * This will override the layout calculated width.
     * 
     * @param width
     *            - if < 0, then the layout calculated width will be used
     */
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setSize(int widthNew, int heightNew) {
        super.setSize(widthNew, heightNew);
        setHeight(heightNew);
        setWidth(widthNew);
    }

    // OVERRIDE

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point p = new Point(wHint, hHint);
        if (height >= 0) {
            p.y = height;
        }
        if (width >= 0) {
            p.y = width;
        }
        p = super.computeSize(p.x, p.y, changed);
        return p;
    }

    @Override
    public Point computeSize(int wHint, int hHint) {
        Point p = new Point(wHint, hHint);
        if (height >= 0) {
            p.y = height;
        }
        if (width >= 0) {
            p.y = width;
        }
        p = super.computeSize(p.x, p.y);
        return p;
    }

    public void open() {
        if (isOpen) {
            return;
        }

        height = heightBackup;
        heightBackup = null;
        isOpen = true;

        redrawShell();

        // animated opening slide up of the wrapper
        for (int i = height; i >= 0; i -= 2) {
            wrapperFormData.top.offset = i;
            try {
                Thread.sleep(ANIMATION_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redrawThis();
        }
    }

    public void close() {
        if (!isOpen) {
            return;
        }

        heightBackup = height;
        height = 0;
        isOpen = false;

        redrawShell();
    }

    // PRIVATE
    private void redrawShell() {
        this.getShell().layout(true, true);
        this.getShell().redraw();
        this.getShell().update();
    }

    private void redrawThis() {
        this.layout(true, true);
        this.redraw();
        this.update();
    }
}
