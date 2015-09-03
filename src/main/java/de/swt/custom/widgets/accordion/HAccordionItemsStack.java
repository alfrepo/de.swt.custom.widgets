package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repräsentiert mehrere zusammengeklappte Slides auf der linken Seite.
 * Wird angezeigt, wenn auf der linken Akkordionseite zu viele Slides vorhanden sind.
 */
public class HAccordionItemsStack extends AnimatableItem {

    private static final Logger LOG = LoggerFactory.getLogger(HAccordionItemsStack.class);

    protected static final int DEFAULT_WIDTH = 20;

    private final HAccordion accordion;
    private final AnimationCommon animationCommon;

    final Color colorLightShadow;
    final Color colorNormalShadow;

    /**
     * Erstellt ein {@link HAccordionItemsStack}
     * 
     * @param parent
     *            - parent composite
     */
    protected HAccordionItemsStack(HAccordion parent) {
        super(parent, SWT.NONE);

        accordion = parent;
        this.animationCommon = this.accordion.getAnimationCommon();

        final HAccordionItemsStack canvas = this;

        colorLightShadow = accordion.colorLightShadow;
        colorNormalShadow = accordion.colorNormalShadow;

        // drawing happens here
        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {

                // draw in this area. Must be inside of the Paint Listener, because the clientarea
                // changes its size
                Rectangle clientArea = canvas.getClientArea();

                // constants
                final double stripes_distance = (double) clientArea.width / 4;

                // Background
                // 0. default background
                e.gc.setBackground(colorLightShadow);
                e.gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

                // draw a gradient shadow
                e.gc.setForeground(colorNormalShadow);
                e.gc.setBackground(colorLightShadow);
                e.gc.fillGradientRectangle(0, 1, 20, clientArea.height - 1, false);

                e.gc.setForeground(colorNormalShadow);
                double startx = 2;
                for (int i = 0; i < 3; i++) {
                    startx += stripes_distance;
                    int startxInt = (int) Math.ceil(startx);
                    e.gc.drawLine(startxInt, 0, startxInt, clientArea.height);
                }

                // draw a dark frame
                e.gc.setForeground(colorNormalShadow);
                e.gc.drawRectangle(0, 0, clientArea.width, clientArea.height - 1);
                e.gc.drawLine(1, 1, clientArea.width, 1);
            }

        });

        // add a mouse listener to the stack
        this.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                animationCommon.expandAllItems(new ICallbackRoutine() {

                    @Override
                    public void callback() {
                        // start collapsing countdown, after the expanding is done
                        animationCommon.getTimeoutCollapseHandler().activate();
                        animationCommon.getTimeoutCollapseHandler().start();
                    }
                });
            }
        });
        this.addListener(SWT.MouseEnter, new Listener() {
            @Override
            public void handleEvent(Event event) {
                LOG.debug("MouseEnter");
            }
        });
        this.addListener(SWT.MouseExit, new Listener() {
            @Override
            public void handleEvent(Event event) {
                LOG.debug("MouseExit");
            }
        });

    }

    /**
     * Dieser Wert wird benutzt, um ein AnimatableItem vollständig auszuklappen. <br>
     * <b>ACHTUNG: Es wird hier angenommen, dass die Breite des Stack kleiner ist, als die Breite
     * des {@link HAccordionLabel}.</b>
     * Diese Annahme wird benutzt, wenn der Stack auf Kosten eines {@link HAccordionLabel}
     * ausgeklappt wird.
     * 
     * @return - die Breite
     */
    @Override
    protected int getMaxWidth() {
        return DEFAULT_WIDTH;
    }

    @Override
    public void dispose() {
        super.dispose();
        colorLightShadow.dispose();
        colorNormalShadow.dispose();
    }

}
