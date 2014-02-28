package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieses Obekt wird innerhalb eines jede Haccodionitems platziert. Es EnthГ¤lt den Namen des
 * Haccodionitems.
 */
public class HAccordionLabel extends Canvas {

    private static final Logger LOG = LoggerFactory.getLogger(HAccordionLabel.class);

    public static final int WIDTH = 40; // px
    private static final int SHADOW_WIDTH = 10;
    private static final int MARGIN = 10; // px
    private static final int IMAGE_SIDELENGTH = WIDTH - (MARGIN * 2); // px

    protected StyledText bottomLabelStyledText;
    protected StyledText middleLabelStyledText;
    protected boolean displayIconsInLabels = false;
    // COLORS
    protected Color colorBgcolor;
    protected Color colorForeground;
    protected Color colorBorder;
    protected Color colorDarkShadow;
    protected Color colorHighlightShadow;
    protected Color colorLightShadow;
    protected Color colorNormalShadow;
    protected Color colorTextMouseover;
    protected Color colorText;

    private HAccordionItem accordionItem;
    private final HAccordion accordion;
    private final AnimationCommon animationCommon;
    private Image image;

    /**
     * Erstellt ein Label.
     * 
     * @param accordionItem
     *            - Item
     * @param style
     *            - style bit
     */
    public HAccordionLabel(final HAccordionItem accordionItem, int style) {
        super(accordionItem, SWT.NONE);
        this.accordionItem = accordionItem;
        this.accordion = this.accordionItem.getParent();
        this.animationCommon = this.accordion.getAnimationCommon();

        final Display display = getDisplay();
        final int shadowMarginLeft = WIDTH - SHADOW_WIDTH;

        colorBgcolor = accordion.colorBgcolor;
        colorForeground = accordion.colorForeground;
        colorBorder = accordion.colorBorder;
        colorDarkShadow = accordion.colorDarkShadow;
        colorHighlightShadow = accordion.colorHighlightShadow;
        colorLightShadow = accordion.colorLightShadow;
        colorNormalShadow = accordion.colorNormalShadow;
        colorTextMouseover = accordion.colorTextMouseover;
        colorText = colorDarkShadow;

        bottomLabelStyledText = new StyledText(this);
        middleLabelStyledText = new StyledText(this);

        // listeners
        this.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                accordionItem.activate();
            }
        });

        // TODO del setting defaults
        // setBottomsetSize();
        // setMiddleSize();
        // initImage();

        // drawing will happen here
        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {

                // draw in this area. Must be inside of the Paint Listener, because the clientarea
                // changes its size
                Rectangle clientArea = HAccordionLabel.this.getClientArea();

                final int middleTextsMarginLeft = shadowMarginLeft - middleLabelStyledText.height;
                final int middleTextsMarginFromTop = clientArea.height / 2;
                middleLabelStyledText.setPos(middleTextsMarginLeft, middleTextsMarginFromTop);

                final int bottomTextsMarginLeft = shadowMarginLeft - bottomLabelStyledText.height;
                final int bottomTextsMarginFromTop = clientArea.height - MARGIN;
                bottomLabelStyledText.setPos(bottomTextsMarginLeft, bottomTextsMarginFromTop);

                // background
                e.gc.setBackground(colorLightShadow);
                e.gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

                // draw a gradient shadow
                e.gc.setForeground(colorLightShadow);
                e.gc.setBackground(colorDarkShadow);
                e.gc.fillGradientRectangle(shadowMarginLeft, 1, clientArea.width, clientArea.height - 1, false);

                // draw a light frame
                e.gc.setForeground(colorHighlightShadow);
                e.gc.drawRectangle(1, 1, clientArea.width, clientArea.height);

                // draw a dark frame
                e.gc.setForeground(colorNormalShadow);
                e.gc.drawRectangle(0, 0, clientArea.width, clientArea.height - 1);

                // Text
                // rotate the screen
                Transform trIdentity = new Transform(display);
                Transform trRotated = new Transform(display);
                trRotated.rotate(-90);
                e.gc.setTransform(trRotated);

                e.gc.setFont(display.getSystemFont());
                e.gc.setForeground(colorText);

                if (image != null) {
                    // rotate
                    e.gc.setTransform(trIdentity);
                    // draw image instead of bottomText
                    // e.gc.drawImage(image, -bottomLabelStyledText.marginFromTop, MARGIN);
                    e.gc.drawImage(image, MARGIN, bottomLabelStyledText.marginFromTop - image.getImageData().height);
                    // undo rotate image
                    e.gc.setTransform(trRotated);
                }

                if (shouldDrawIcon()) {
                    // move the text up, if we have icons enables
                    bottomLabelStyledText.marginFromTop -= IMAGE_SIDELENGTH + MARGIN;
                }

                middleLabelStyledText.paint(display, e.gc, -middleLabelStyledText.marginFromTop,
                        middleLabelStyledText.marginFromLeft);
                bottomLabelStyledText.paint(display, e.gc, -bottomLabelStyledText.marginFromTop,
                        bottomLabelStyledText.marginFromLeft);

                // rotate back
                trRotated.identity();
                e.gc.setTransform(trRotated);

            }
        });

        this.addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseHover(MouseEvent e) {
            }

            @Override
            public void mouseEnter(MouseEvent e) {

                bottomLabelStyledText.colorMouseover = accordion.colorTextMouseover;
                middleLabelStyledText.colorMouseover = accordion.colorTextMouseover;

                // stop the timeout if it is running
                animationCommon.getTimeoutCollapseHandler().stop();

                // redraw bottom
                HAccordionLabel.this.redraw();
            }

            @Override
            public void mouseExit(MouseEvent e) {
                bottomLabelStyledText.colorMouseover = null;
                middleLabelStyledText.colorMouseover = null;

                // start a timeout, after which the items on the left will be collapsed to a stack
                animationCommon.getTimeoutCollapseHandler().start();

                // redraw text
                HAccordionLabel.this.redraw();
            }

        });
    }

    @Override
    public Point getSize() {
        LOG.info("getsize called");
        return super.getSize();
    }

    public void setTextColorMouseover(Color color) {
        this.colorTextMouseover = color;
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point p = super.computeSize(wHint, hHint, changed);
        p.x = HAccordionLabel.WIDTH;
        return p;
    }

    @Override
    public void dispose() {
        super.dispose();

        colorBgcolor.dispose();
        colorForeground.dispose();
        colorBorder.dispose();
        colorDarkShadow.dispose();
        colorHighlightShadow.dispose();
        colorLightShadow.dispose();
        colorNormalShadow.dispose();
        colorText.dispose();
        colorTextMouseover.dispose();
    }

    // CUSTOM STYLING

    /**
     * Assume quadratic images
     * 
     * @param image
     * @param display
     * @param sideLength
     * @return
     */
    private Image scaleImage(Image image, Display display) {
        // final Image scaledImage = new Image(display,newWidth,newHeight);
        final ImageData scaledImageData = image.getImageData().scaledTo(IMAGE_SIDELENGTH, IMAGE_SIDELENGTH);
        // ImageData stores the data about transparency. Use it in new Image to respect transparency
        final Image scaledImage = new Image(display, scaledImageData);
        return scaledImage;
    }

    protected void addBottomText(String text) {
        this.bottomLabelStyledText.addText(text);
    }

    protected void addBottomStyledText(StyledText styledText) {
        this.bottomLabelStyledText = styledText;
    }

    protected void addMiddleText(String text) {
        this.middleLabelStyledText.addText(text);
    }

    protected void setMiddleStyledText(StyledText styledText) {
        this.middleLabelStyledText = styledText;
    }

    protected void setImage(Image newImage) {
        image = scaleImage(newImage, getDisplay());
    }

    /**
     * Text's distance from the bottom, caused by the icon and margins.
     */
    protected int getBottomMargin() {
        int result = MARGIN;
        if (shouldDrawIcon()) {
            result += IMAGE_SIDELENGTH + MARGIN;
        }
        return result;
    }

    private boolean shouldDrawIcon() {
        return (image != null || displayIconsInLabels);
    }

    // TODO del
    Image getImage(Display display) {
        Image im = new Image(display, 50, 50);
        im.setBackground(display.getSystemColor(SWT.COLOR_DARK_RED));
        return im;
    }

    void setBottomsetSize() {
        FontData fontData = new FontData();
        fontData.setHeight(11);
        fontData.setStyle(SWT.BOLD);

        Color fontColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
        bottomLabelStyledText.addStyle(new Style(fontColor, fontData));
    }

    void setMiddleSize() {
        FontData fontData = new FontData();
        fontData.setHeight(9);
        fontData.setStyle(SWT.BOLD);

        FontData fontData2 = new FontData();
        fontData2.setHeight(12);
        fontData2.setStyle(SWT.BOLD);
        Color color = getDisplay().getSystemColor(SWT.COLOR_RED);

        middleLabelStyledText.addStyle(new Style(colorDarkShadow, fontData));
        middleLabelStyledText.addStyle(new Style(color, fontData2));
        middleLabelStyledText.addText("BLA");
        middleLabelStyledText.addText("blub");
    }

    void initImage() {
        setImage(getImage(getDisplay()));
    }

}
