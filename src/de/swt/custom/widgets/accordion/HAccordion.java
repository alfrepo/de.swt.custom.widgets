package de.swt.custom.widgets.accordion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.swt.custom.widgets.Colors;


/**
 * Das Objekt repräsentiert das horizontale Akkordion.
 * Es wird mit HaccordionItems gefüllt.
 * Das Haccordion Objekt haelt eine Referenz auf das {@link Animation} objekt und kapselt
 * domÃ¤nenspezifische Aufrufe zur {@link Animation}, welche zB. die Aktivierung bestimmer Items
 * bewirken sollen.
 * <p>
 * <h2>Glossar:</h2>
 * <ul>
 * <li>Haccordion - Horizontal Accordion. Top level Kontainer, welcher HaccordionItems enthält
 * <li>HaccordionItems - Alias Items. Alias Slides. Einheiten, mit denen das Haccordion gefüllt
 * wird. Diese Enthalten den Haccordion Content
 * <li>Label - Jedes HaccordionItem hat ein label. Ein zusammengeklapptes HaccordionItem wird anhand
 * des Labels identifiziert.
 * <li>Stack - Das erste Kind des Haccordion, welcher einen Stapel von zusammengefügten
 * HaccordionItems repräsentiert.
 * </ul>
 * </p>
 * 
 * This classes Methods will catch SWTExceptions, which occur because one of the widgets was
 * disposed while a thread was doing something with it.
 * 
 */
public class HAccordion extends Composite {

    private static final Logger LOG = LoggerFactory.getLogger(HAccordion.class);

    public static final int DEFAULT_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 200;

    public static final int MAX_ITEMS_ON_LEFT = 2; // collapse items to stack, when there are more
                                                   // on the left side.
    protected boolean displayIconsInLabels = false;

    private HAccordionItem activeItem; // pointer to the active item in the Accordion
    private HAccordionItem activeItemOld; // used to revert running animation

    private Animation animation = new Animation(this); // this will animate the accordion slides
                                                       // resizing
    private AnimationCommon animationCommon = new AnimationCommon(animation); // this will be
                                                                              // used by other
                                                                              // classes to
                                                                              // trigger
                                                                              // animation
                                                                              // actions
    private HAccordionItemsStack hAccordionItemsStack; // this is the object, which symbolizes a
                                                       // stack of closed items

    // this listeners will be added to the label of every newly created HAccordionItem. Adding of
    // listeners is done by HAccordionItem, because the parent in SWT do not know, when the children
    // are added to it. So the HAccrodion does not know about children addition and so the children
    // have to do the addition.
    private Map<Integer, ArrayList<Listener>> defaultHAccordionLabelListeners = new HashMap<>();

    private List<Listener> postItemActivationListeners = new ArrayList<>();

    // FLAGS
    protected boolean showScrollbars = true; // a flag, which indicates, whether the scrollbars
                                             // should be visible or not. Set to false during the
                                             // animation.

    private NotWrappingRowLayout layout;

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

    protected List<Style> haccordionItemLabelMiddleStyles = new ArrayList<Style>();
    protected List<Style> haccordionItemLabelBottomStyles = new ArrayList<Style>();

    /**
     * The Accordion.
     * 
     * @param parent
     *            - parent Composite
     * @param style
     *            - style bit.
     */
    public HAccordion(Composite parent, int style) {
        super(parent, style);

        this.addListener(SWT.Resize, new OnResizeListener(this));

        layout = new NotWrappingRowLayout();
        layout.setHeight(DEFAULT_HEIGHT);
        layout.setWidth(DEFAULT_WIDTH);
        this.setLayout(layout);

        colorBgcolor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        colorForeground = getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
        colorBorder = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BORDER);
        colorDarkShadow = Colors.DARK_SHADOW;
        colorHighlightShadow = getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
        colorLightShadow = getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        colorNormalShadow = getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        colorTextMouseover = this.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

        // configuration
        this.setBackground(colorBgcolor);

        hAccordionItemsStack = new HAccordionItemsStack(this);
    }

    // public methods

    /**
     * Aktiviert das gegebene Item. Die Animation wird ausgeführt. Die UI wird angepasst.
     * 
     * @param item
     *            - the item to activate. If the Item is not present inside of the Accordion -
     *            nothing will be activated.
     * @param callback
     *            - das Callaback, welches nach der Animation aufgerufen soll.
     */
    public void activateItem(HAccordionItem item, ICallbackRoutine callback) {
        if (this.getActiveItem() == item) {
            return;
        } else if (animation.isRunning()) {
            return;
        }

        if (callback != null) {
            animation.addCallbackRoutine(callback);
        }

        animation.addCallbackRoutine(new ICallbackRoutine() {

            @Override
            public void callback() {
                for (Listener listener : postItemActivationListeners) {
                    listener.handleEvent(null);
                }
            }
        });

        showScrollbars = false;

        try {

            // registriere ein callback, welches die Scrollbalken wieder aktivieren wird,
            // nachdem die Animation vorbei ist
            final ItemsIterator iterator = this.getItemsIterator();
            animation.addCallbackRoutine(new ICallbackRoutine() {
                @Override
                public void callback() {
                    showScrollbars = true;

                    // the GUI changes should happen on the UI thread
                    getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // upate the scrollbars for all items
                                for (HAccordionItem item : iterator.getChildren()) {
                                    if (item instanceof HAccordionItem) {
                                        item.updateScrolledCompositeMinSize();
                                    }
                                }
                                HAccordion.this.redraw();

                            } catch (SWTException e) {
                                LOG.debug("disposed widget", e);
                            }
                        }

                    });

                }
            });

            /*
             * EDIT: setze das aktive item VOR dem Animationsstart.
             * So wird das zu oeffnende Item sofort aktiv.
             * Somit kann man, beim Ð“â€žndern der Shell Breite
             * aktives_item_Breite=verfügbare_Breite-Breite_nichtAktiverKinder.
             * Wenn die animation laeuft frisst sie die Breite nichtaktiver Kinder wenn noetig auf.
             */
            HAccordionItem activeHaccordionItemNew = item;
            HAccordionItem activeHaccordionItemOld = this.getActiveItem();

            this.setActive(activeHaccordionItemNew);

            // öffne das aktive Item mithilfe der animation
            this.animation.run(activeHaccordionItemNew, activeHaccordionItemOld);

        } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException | SWTException e) {
            LOG.error("error on item activation", e);
        }

    }

    /**
     * Ein Shortcut fuer die Methode {@link this#activateItem(HAccordionItem, ICallbackRoutine)}
     * wobei die callback rourine null ist.
     * 
     * @param item
     *            - item
     */
    public void activateItem(HAccordionItem item) {
        activateItem(item, null);
    }

    public boolean isAnimating() {
        return animation.isRunning();
    }

    /**
     * Gets the next item, to the right of the active item.
     * 
     * @return - next active item
     */
    public HAccordionItem getNextItem() {
        try {
            HAccordionItem newItem = this.getActiveItem();

            int activePos = -1;
            Control[] children = getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i].equals(this.getActiveItem())) {
                    activePos = i;
                    break;
                }
            }
            newItem = (HAccordionItem) children[activePos + 1];

            return newItem;

        } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException | SWTException e) {
            LOG.error("error on get next item", e);
        }

        return null;
    }

    /**
     * Kehrt die vorherige animation um.
     */
    public void revertAnimation() {
        if (animation.isRunning()) {
            // waehrend die Animation laeuft - erlaube keine reverts
            return;
        }

        // activate the old item
        activateItem(activeItemOld);

    }

    /**
     * Removes (disposes) all items, which are right from the current item.
     * Will be called, when a new Items is added.
     * Doesn't work when animation is running.
     * 
     */
    public void disposeItemsRightFromActive() {
        if (animation.isRunning()) {
            return;
        }
        ItemsIterator iterator = getItemsIterator();
        Deque<HAccordionItem> list = iterator.getAllItemsRightOf(iterator.getIndexOf(activeItem));
        for (HAccordionItem item : list) {
            item.dispose();
        }
    }

    /**
     * Gibt ein Iterator zurueck, welcher über {@link HAccordionItem}s navigieren kann.
     * 
     * @return - true on success
     */
    public ItemsIterator getItemsIterator() {
        return new ItemsIterator(this);
    }

    /**
     * Set the width
     * 
     * @param width
     *            - width
     */
    public void setWidth(int width) {
        layout.setWidth(width);
    }

    /**
     * Set the height
     * 
     * @param height
     *            - height
     */
    public void setHeight(int height) {
        layout.setHeight(height);
    }

    /**
     * Tells, if the Scrollbars should be made visible by the Haccordion children.
     * 
     * @return true on success
     */
    public boolean isShowingScrollbars() {
        return showScrollbars;
    }

    /**
     * Returns the active {@link HAccordionItem}
     * 
     * @return active {@link HAccordionItem} for this accordion
     */
    public HAccordionItem getActiveItem() {
        if (this.activeItem != null) {
            return this.activeItem;
        } else {
            // aktiviere das erste Item, wenn noch keins aktiv. Das Kind nummer 0 ist das
            // HaccordionItemStack
            this.setActive(new ItemsIterator(this).getItem(1));
            return activeItem;
        }
    }

    /**
     * Returns the {@link Stack} object.
     * 
     * @return the items stack representation.
     */
    public HAccordionItemsStack getStack() {
        return this.hAccordionItemsStack;
    }

    /**
     * The color of the labeltext on mouseover.
     * 
     * @param color
     *            - color
     */
    public void setColorTextMouseover(Color color) {
        colorTextMouseover = color;
    }

    /**
     * Enables or disables the drawing of icons in labels
     * 
     * @param enable
     */
    public void displayLabelIcons(boolean enable) {
        for (HAccordionItem item : getItemsIterator().getChildren()) {
            item.displayLabelIcons(enable);
        }
        this.displayIconsInLabels = enable;
    }

    /**
     * Adds a new Listener, which will be added to every existing and newly created
     * {@link HAccordionItem}'s {@link HAccordionLabel}s
     */
    public void addDefaultHAccordionLabelListener(Integer eventId, Listener listener) {
        if (!this.defaultHAccordionLabelListeners.containsKey(eventId)) {
            this.defaultHAccordionLabelListeners.put(eventId, new ArrayList<Listener>());
        }
        this.defaultHAccordionLabelListeners.get(eventId).add(listener);

        // add the listener to the existing HAccrodinItems too
        for (HAccordionItem item : this.getItemsIterator().getChildren()) {
            item.addLabelListener(eventId, listener);
        }
    }

    public boolean containsDefaultHAccordionLabelListener(Integer eventId, Listener listener) {
        if (!this.defaultHAccordionLabelListeners.containsKey(eventId)) {
            return false;
        }
        if (this.defaultHAccordionLabelListeners.get(eventId) == null) {
            return false;
        }
        if (this.defaultHAccordionLabelListeners.get(eventId).contains(listener)) {
            return true;
        }
        return false;
    }

    /**
     * From the set of Listeners, which will be added to every newly created child
     * {@link HAccordionItem} - remove the given Listener, which was registered for th given event
     * type.
     */
    public void removeDefaultHAccordionLabelListeners(Integer eventId, Listener listener) {
        if (this.defaultHAccordionLabelListeners.containsKey(eventId)) {
            if (this.defaultHAccordionLabelListeners.get(eventId) != null) {
                this.defaultHAccordionLabelListeners.get(eventId).remove(listener);
            }
        }
    }

    /**
     * From the set of Listeners, which will be added to every newly created child
     * {@link HAccordionItem} - remove the Listeners of the given type.
     */
    public void removeDefaultHAccordionLabelListeners(Integer eventId) {
        if (this.defaultHAccordionLabelListeners.containsKey(eventId)) {
            this.defaultHAccordionLabelListeners.remove(eventId);
        }
    }

    /**
     * Retrieves all Eventlisteners of the given event type, which will be added by default to the
     * {@link HAccordionLabel} of every new item.
     * The returned Object is a copy of the original ArrayList.
     * 
     * @return - a HashMap with, where the key is an EventId as provided by static fields in
     *         {@link SWT}, and the value is an ArrayList of Listeners.
     */
    public List<Listener> getDefaultHAccordionLabelListeners(Integer eventId) {
        return new ArrayList<Listener>(this.defaultHAccordionLabelListeners.get(eventId));
    }

    /**
     * Retrieves all Eventlisteners, which iwll be added by default to the {@link HAccordionLabel}
     * of every new item.
     * The returned Object is a copy of the original HashMap.
     * 
     * @return - a HashMap with, where the key is an EventId as provided by static fields in
     *         {@link SWT}, and the value is an ArrayList of Listeners.
     */
    public Map<Integer, List<Listener>> getDefaultHAccordionLabelListeners() {
        return new HashMap<Integer, List<Listener>>(this.defaultHAccordionLabelListeners);
    }

    /**
     * Adds a new itemactivation listener. The item activation listener will be triggered, after the
     * slide Animation was finished.
     * The Listener will receive null as Event.
     * 
     * @param listener
     *            - listener to add
     */
    public void addPostItemActivationListener(Listener listener) {
        this.postItemActivationListeners.add(listener);
    }

    /**
     * Removes the given listener.
     * 
     * @param listener
     *            - Listener to remove
     */
    public void removePostItemActivationListener(Listener listener) {
        this.postItemActivationListeners.remove(listener);
    }

    /**
     * Return true if the item activationListenerList contains the given Listener.
     * 
     * @param listener
     *            the listener to search for
     * @return - true if true
     */
    public boolean containsPostItemActivationListener(Listener listener) {
        return this.postItemActivationListeners.contains(listener);
    }

    /**
     * Checks, whether the accordion contains the given item.
     * 
     * @param item
     *            the item to search for
     * @return true if item is among the children of this accordion
     */
    public boolean containsItem(HAccordionItem item) {
        for (Object o : this.getChildren()) {
            if (item.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        animation.dispose();
        animationCommon.dispose();
    }

    // protected
    /**
     * Setzt ein Item als "aktiv" für den aktuellen HAccordion, ohne die animation zu starten.
     * <p>
     * Diese Method muss protected bleiben, weil das HaccordionItem sich gegebenfalls selbst
     * aktivieren muss, beim Einfuegen. Das kommt daher, weil man im SWT keine überschreibbare add
     * Methode zum Hinzufuegen von Kindern hat, sondern weil die Kinder die Logik zum Einfuegen im
     * Konstruktor speichern.
     * </p>
     * 
     * @param item
     *            - das neue Item, welches hinzugefuegt wurde
     */
    protected void setActive(HAccordionItem item) {
        // check if it is a valid item. Will cause massive performace problems
        if (Arrays.asList(this.getChildren()).contains(item)) {
            this.activeItemOld = activeItem;
            this.activeItem = item;
        }
    }

    /**
     * Returns the associated {@link AnimationCommon} object.
     * 
     * @return an Object, which will be used by other classes to trigger animation actions.
     */
    protected AnimationCommon getAnimationCommon() {
        return animationCommon;
    }

    /**
     * Stores the FontData for middle text in labels
     * 
     * @param fontData
     *            - the fontData
     */
    public void addMiddleStyle(Style style) {
        for (HAccordionItem item : getItemsIterator().getChildren()) {
            item.addMiddleStyle(style);
        }
        this.haccordionItemLabelMiddleStyles.add(style);
    }

    /**
     * Clears all Style at the bottom
     */
    public void clearMiddleStyle() {
        for (HAccordionItem item : getItemsIterator().getChildren()) {
            item.clearAllMiddleStyle();
        }
        this.haccordionItemLabelMiddleStyles.clear();
    }

    /**
     * Stores the Style for bottom text in labels
     * 
     * @param Style
     *            - the Style
     */
    public void addBottomStyle(Style style) {
        for (HAccordionItem item : getItemsIterator().getChildren()) {
            item.addBottomStyle(style);
        }
        this.haccordionItemLabelBottomStyles.add(style);
    }

    /**
     * Clears all Style at the bottom
     */
    public void clearBottomStyle() {
        for (HAccordionItem item : getItemsIterator().getChildren()) {
            item.clearAllBottomStyle();
        }
        this.haccordionItemLabelBottomStyles.clear();
    }

    // private helper

    /**
     * Listener to update the activeItem's size on Accordion resize.
     */
    private final class OnResizeListener implements Listener {
        private final HAccordion haccordion;

        public OnResizeListener(HAccordion haccordion) {
            this.haccordion = haccordion;
        }

        @Override
        public void handleEvent(Event event) {
            // die groesse des Haccordion hat sich geändert. Passe die groesse des aktiven items
            // an.
            changed(getChildren()); // invalidate children on resize

            // EDIT: die animation kann ruhig durchlaufen. Das Aktive Item wird verändert, die
            // Items,
            // welche gerade von der Animaiton verkleinert werden - beliben unangefasst.
            // Achte dadrauf, dass die Animation nicht laeuft. Falls der groessen transfair von
            // aktiven item in das andere gerade laeuft
            // - sollte das aktive item nicht mehr genau so breit sein wie das Haccoridon, weil ein
            // Teil der Breite bereits in das andere Item geflossen ist.

            List<HAccordionItem> children = haccordion.getItemsIterator().getChildren();
            final HAccordionItem activeHaccordionItem = haccordion.getActiveItem();

            // 1. passe die Hoehe aller Kinder an. Die Hoehe ist Animationsunabhängig.
            for (HAccordionItem child : children) {
                child.setHeight(haccordion.getSize().y);
            }

            // 2. EDIT:
            // gebe dem aktiven Item alles an Breite, was die anderen noch nicht konsumierten
            // setze (aktive Item Breite) = (verfügbare Breite) - (Breite nicht aktiver Kinder)
            // falls die animation laeuft - frisst sie die Breite nichtaktiver Kinder wenn noetig
            // auf.
            int redistributeWidth = haccordion.getSize().x + HAccordionLabel.WIDTH;
            for (HAccordionItem child : children) {
                if (activeHaccordionItem == child) {
                    continue;
                }
                redistributeWidth -= child.itemWidth;
            }
            activeHaccordionItem.setWidth(redistributeWidth);

            // 3. change the layout
            for (HAccordionItem child : children) {
                child.layout(true);
            }
        }

    }
    
    // XXX test
    void defferItemsLayout(boolean defer){
    	for(HAccordionItem item : getItemsIterator().getChildren() ){
    		item.getClientAreaObject().setLayoutDeferred(defer);
    	}
    }

}
