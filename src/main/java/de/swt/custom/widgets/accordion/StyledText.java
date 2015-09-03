package de.swt.custom.widgets.accordion;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class StyledText {

    private HAccordionLabel hAccordionLabel;

    private static final int WORD_SPACE = 5; // px
    Color colorMouseover = null;
    int length = 0;
    int height = 0;
    int marginFromLeft = 0;
    int marginFromTop = 0;

    Image cacheNormalState;

    List<Text> textList = new ArrayList<>();
    List<Style> styleList = new ArrayList<>();

    protected StyledText(HAccordionLabel hAccordionLabel) {
        this.hAccordionLabel = hAccordionLabel;
    }

    synchronized void measureAndCache() {
        GC gc = new GC(hAccordionLabel);
        length = 0;
        height = 0;

        // measure Text
        PairIterator pi = new PairIterator(textList, styleList);
        Pair p = pi.getNext();
        while (p != null) {
            gc.setFont(p.style.font);
            p.text.size = gc.textExtent(p.text.text);
            height = Math.max(height, p.text.size.y);
            length += p.text.size.x + WORD_SPACE;

            p = pi.getNext();
        }

        createCache();
        gc.dispose();
    }

    void paint(Display display, GC gc, int x, int y) {

        int xPointer = x;
        PairIterator pi = new PairIterator(textList, styleList);
        Pair p = pi.getNext();
        while (p != null) {

            if (colorMouseover != null) {
                gc.setForeground(colorMouseover);
            } else {
                gc.setForeground(p.style.color);
            }

            // if the text is smaller, than another text in the List - compensate
            int deltaY = height - p.text.size.y;

            gc.setFont(p.style.font);
            gc.drawString(p.text.text, xPointer, y + deltaY, true);

            // move the pointer to the next text. Move up, because of the Transform of -90 degree
            xPointer += p.text.size.x + WORD_SPACE;

            p = pi.getNext();
        }
    }

    void createCache() {
        if (length <= 0 && height <= 0) {
            return;
        }
        this.cacheNormalState = new Image(hAccordionLabel.getDisplay(), length, height);
        GC gc = new GC(this.cacheNormalState);
        paint(hAccordionLabel.getDisplay(), gc, height, 0);
        gc.dispose();
    }

    void setPos(int x, int y) {
        marginFromLeft = x;
        marginFromTop = y;
    }

    public void addText(String text) {
        this.textList.add(new Text(text));
        measureAndCache();
    }

    public void addStyle(Style style) {
        styleList.add(style);
        measureAndCache();
    }

    public void clearStyle() {
        styleList.clear();
    }

    public void clearText() {
        textList.clear();
    }

    /**
     * Returns pairs as long as text exists. Next style is taken from the style list, together with
     * the text.
     * If there are more text objects - the last style is used.
     * 
     * @author alf
     * 
     */
    private class PairIterator {
        private Deque<Text> text;
        private Deque<Style> style;

        public PairIterator(List<Text> newText, List<Style> newStyle) {
            this.text = new LinkedList<>(newText);
            this.style = new LinkedList<>(newStyle);
            if ((this.style.isEmpty()) || (newStyle == null)) {
                // default style
                this.style.add(new Style(null, null));
            }
        }

        Pair getNext() {
            Pair p = null;

            if (text.peek() != null) {
                p = new Pair();

                p.text = text.removeFirst();
                if (style.size() <= 1) {
                    p.style = style.peek();
                } else {
                    p.style = style.removeFirst();
                }
            }
            return p;
        }
    }

    private class Pair {
        Text text;
        Style style;
    }

    private class Text {
        Text(String text) {
            this.text = text;
        }

        String text;
        Point size = new Point(0, 0);
    }

}
