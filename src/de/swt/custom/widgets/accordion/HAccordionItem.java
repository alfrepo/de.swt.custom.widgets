package de.swt.custom.widgets.accordion;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReprГ¤sentiert ein Akkordion Item (alias. Slide).
 * Das Akkordion mit diesen Objekten gefР“С?llt.
 */
public class HAccordionItem extends AnimatableItem {

    private static final Logger LOG = LoggerFactory.getLogger(HAccordionItem.class);

    private SlideText slideText;
    private HAccordionLabel rightLabel;
    private String leftLabelText = "";
    private FormLayout layout;
    private ScrolledComposite scrolledComposite;
    private HAccordionClientArea clientArea;
    private Color colorBackground;
    private final HAccordion accordion;

    private static final int LEFT_SLIDE_TEXT_PADDING = 5; // px

    /**
     * Create an accordion item.
     * 
     * @param parent
     *            - the Haccordion instance for the new item
     */
    public HAccordionItem(final HAccordion parent) {
        super(parent, SWT.NONE);

        accordion = parent;
        colorBackground = accordion.colorBgcolor;

        // 1. INIT

        // ACHTUNG:
        // die Reihenfolge ist Entscheidend, dafuer welches Kind zuerst gezeichnet wird.
        // die Kinder werden bottom-up gezeichnet, also slideText zuerst, so dass es von dem label
        // ueberzeichnet wird
        rightLabel = new HAccordionLabel(this, SWT.NONE);
        scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
        slideText = new SlideText(this, SWT.NONE);
        clientArea = new HAccordionClientArea(scrolledComposite, SWT.NONE);

        // 2. LAYOUT: layout after initiation of containers
        this.layout = new FormLayout();
        this.setLayout(layout);

        // am I the first Item?
        Control[] control = this.accordion.getChildren();
        boolean iAmTheFirstItem = this.equals(control[1]);

        FormData formData4SlideText = new FormData();
        formData4SlideText.top = new FormAttachment(0, 0);
        int leftSlideTextPadding = LEFT_SLIDE_TEXT_PADDING;
        if (iAmTheFirstItem) {
            leftSlideTextPadding = 0;
        }
        formData4SlideText.left = new FormAttachment(0, leftSlideTextPadding);
        formData4SlideText.bottom = new FormAttachment(100, 0);
        slideText.setLayoutData(formData4SlideText);

        // position the label on the right side of the HAccordionItem: [ []]
        FormData formData4Label = new FormData();
        formData4Label.top = new FormAttachment(0, 0);
        formData4Label.right = new FormAttachment(100, 0);
        formData4Label.bottom = new FormAttachment(100, 0);
        rightLabel.setLayoutData(formData4Label);

        // position the scrolledComposite left of the label, inside of the HAccordionItem: [[ ] ]
        FormData formData4scrolledComposite = new FormData();
        formData4scrolledComposite.left = new FormAttachment(slideText, 0);
        formData4scrolledComposite.top = new FormAttachment(0, 0);
        formData4scrolledComposite.right = new FormAttachment(rightLabel, 0);
        formData4scrolledComposite.bottom = new FormAttachment(100, 0);
        scrolledComposite.setLayoutData(formData4scrolledComposite);

        // set clientArea's default Layout
        RowLayout defaultSlideLayout = new RowLayout(SWT.HORIZONTAL);
        clientArea.setLayout(defaultSlideLayout);

        // 3. CONFIGURATION
        clientArea.setBackground(colorBackground);

        scrolledComposite.setContent(clientArea);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                updateScrolledCompositeMinSize();
            }
        });

        Map<Integer, List<Listener>> listeners = parent.getDefaultHAccordionLabelListeners();
        for (Entry<Integer, List<Listener>> entry : listeners.entrySet()) {
            for (Listener labelListener : entry.getValue()) {
                this.addLabelListener(entry.getKey(), labelListener);
            }
        }

        // 4. REST
        /*
         * nur das HaccordionItem weiss, wann es hinzugefР“С?gt wird - der Parent weiss es
         * nicht.
         * Deshalb,
         * falls noch kein HAccordionItem im Parent aktiviert wurde und
         * falls das gerade hinzugefР“С?gte Item - das einzige ist
         * - aktiviert sich das HAccordionItem im Parent selbst.
         */
        if (parent.getActiveItem() == null) {
            parent.setActive(this);
            this.hideSlideText();
        }

        // Get the label styles from the accordion
        for (Style s : parent.haccordionItemLabelBottomStyles) {
            addBottomStyle(s);
        }
        for (Style s : parent.haccordionItemLabelMiddleStyles) {
            addMiddleStyle(s);
        }

        displayLabelIcons(accordion.displayIconsInLabels);
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point result = super.computeSize(wHint, hHint, changed);

        LOG.debug("HaccordionItem {}  width: {} height: {}", new Object[] {System.identityHashCode(this), result.x,
                result.y });

        return result;
    }

    // public
    /**
     * Return the width of the label on the right side of the slide.
     * 
     * @return width of the label
     */
    public int getlabelWidth() {
        return HAccordionLabel.WIDTH;
    }

    /**
     * Set the text of the label on the right side.
     * 
     * @param text
     *            - label text
     */
    public void addRightLabelText(String text) {
        this.rightLabel.addMiddleText(text);
    }

    /**
     * Set the text of the label on the left side.
     * 
     * @param text
     *            - label text
     */
    public void setLeftLabelText(String text) {
        this.leftLabelText = text;
    }

    /**
     * Sets both labels, the left and the right, as done by {@link #setLeftLabelText(String)} and
     * {@link #setRightLabelText(String)}
     * 
     * @param text
     *            - label name
     */
    public void setLabelText(String text) {
        this.rightLabel.addBottomText(text);
        this.leftLabelText = text;
    }

    /**
     * Retrieves the Label callbacks
     * 
     * @param eventType
     * @return
     */
    public Listener[] getLabelListeners(int eventType) {
        return this.rightLabel.getListeners(eventType);
    }

    /**
     * Adds a label callback.
     * 
     * @param eventType
     * @param listener
     */
    public final void addLabelListener(int eventType, Listener listener) {
        this.rightLabel.addListener(eventType, listener);
    }

    /**
     * Removes a label Listener
     * 
     * @param eventType
     * @param listener
     */
    public void removeLabelListener(int eventType, Listener listener) {
        this.rightLabel.removeListener(eventType, listener);
    }

    /**
     * Set the mouseover-color of the label's text.
     * 
     * @param color
     *            - text color.
     */
    public void setTextColorMouseover(Color color) {
        this.rightLabel.setTextColorMouseover(color);
    }

    @Override
    public HAccordion getParent() {
        return accordion;
    }

    /**
     * Wird benutzt um content zum Item hinzuzufuegen.
     * 
     * @return - die ClientArea.
     */
    public HAccordionClientArea getClientAreaObject() {
        return this.clientArea;
    }

    /**
     * Activeates the item inside of the accordion.
     */
    public void activate() {
        this.getParent().activateItem(this);
    }

    /**
     * Sets the minimum-size of the scrollable container inside of the item to the size of the
     * content, in order to show the Scrollbars,
     * or sets the minimum-size of the scrollable container to 0, in order to hide the scrollbars.
     * Used inside on content resize and to hide the scrollbars during the animation.
     */
    public void updateScrolledCompositeMinSize() {
        if (accordion.isShowingScrollbars()) {
            // draw scrollbars
            scrolledComposite.setMinSize(clientArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        } else {
            // hide scrollbars
            scrolledComposite.setMinSize(0, 0);
        }
    }

    // STYLE
    public void addBottomText(String text) {
        this.rightLabel.addBottomText(text);
    }

    public void addMiddleText(String text) {
        this.rightLabel.addMiddleText(text);
    }

    public void setImage(Image newImage) {
        this.rightLabel.setImage(newImage);
    }

    public void addMiddleStyleAllLabels(Style style) {
        accordion.addMiddleStyle(style);
    }

    public void addBottomStyleAllLabels(Style style) {
        accordion.addBottomStyle(style);
    }

    public void clearBotomTexts() {
        this.rightLabel.bottomLabelStyledText.clearText();
    }

    public void clearMiddleTexts() {
        this.rightLabel.middleLabelStyledText.clearText();
    }

    public void clearAllBottomStyle() {
        this.rightLabel.bottomLabelStyledText.clearStyle();
    }

    public void clearAllMiddleStyle() {
        this.rightLabel.middleLabelStyledText.clearStyle();
    }

    protected final void addBottomStyle(Style style) {
        this.rightLabel.bottomLabelStyledText.addStyle(style);
    }

    protected final void addMiddleStyle(Style style) {
        this.rightLabel.middleLabelStyledText.addStyle(style);
    }

    /**
     * Enables or disables the drawing of icons in labels
     * 
     * @param enable
     */
    public final void displayLabelIcons(boolean enable) {
        this.rightLabel.displayIconsInLabels = enable;
    }

    // private

    /**
     * Used to hide the slidetext on the first slide
     */
    private void hideSlideText() {
        this.slideText.hide();
    }

    // classes
    /**
     * The slide-label which is displayed on the left side of the slide.
     * 
     * @author alf
     * 
     */
    class SlideText extends Composite {

        String text = "Test";
        public int width = 15; // px

        public SlideText(Composite parent, int style) {
            super(parent, SWT.NONE);

            final Display display = this.getDisplay();
            final Color color_text = display.getSystemColor(SWT.COLOR_BLACK);

            this.addPaintListener(new PaintListener() {
                @Override
                public void paintControl(PaintEvent e) {

                    int slideHeight = SlideText.this.getClientArea().height;
                    Color itemBackgroundColor = clientArea.getBackground();
                    setBackground(itemBackgroundColor);

                    int textMarginTop = slideHeight - rightLabel.getBottomMargin();
                    // int textMarginTop = (slideTextClientArea.height) / 2;

                    // Text
                    // rotate the screen
                    Transform tr = new Transform(display);
                    tr.rotate(-90);
                    e.gc.setTransform(tr);

                    e.gc.setFont(display.getSystemFont());
                    e.gc.setForeground(color_text);
                    e.gc.drawString(leftLabelText, -textMarginTop, 0, true);

                    // rotate back
                    tr.identity();
                    e.gc.setTransform(tr);
                }
            });

        }

        // computes the size of the widget
        @Override
        public Point computeSize(int wHint, int hHint, boolean changed) {
            Point p = super.computeSize(wHint, hHint, changed);
            p.x = this.width;
            return p;
        }

        public void hide() {
            this.width = 0;
        }

    }

}
