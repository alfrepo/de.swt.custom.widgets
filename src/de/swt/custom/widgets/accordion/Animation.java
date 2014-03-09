package de.swt.custom.widgets.accordion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class knows how to animate {@link HAccordionItem} instances.
 * One instance of {@link Animation} can only run once in parallel.
 * The Animanion can be reverted. Revertion happens only once, per start and only if the animation
 * is running.
 * */
public class Animation {

    private static final Logger LOG = LoggerFactory.getLogger(Animation.class);

    // callback routines, which will be informed, about animation stop. This list is cleared on
    // every animation stop.
    private List<ICallbackRoutine> callbackRoutines = new ArrayList<>();

    // Timestamp, which the workers will look at, to before they start to execute themselfes. If the
    // worker is outdated - it will not be executed.
    private long stopTimestampt = System.currentTimeMillis();

    // the thread, which will execute workers
    private Executor animationExecutor = new Executor(null);

    private HAccordion accordion;
    private AnimatableItem activeItemNew;
    private AnimatableItem activeItemOld;

    private static final int PERIOD = 10; // animation step every 10ms -> 1000/10=100 fps
    private static final int SPEED = 180; // move by 180px every 10ms
    private static final int SPEED_LOW = 5; // move by 5px every 10ms

    /**
     * Creates an animation
     * 
     * @param accordion
     *            - {@link HAccordion} instance for animation.
     */
    protected Animation(HAccordion accordion) {
        this.accordion = accordion;
    }

    // Public

    /**
     * Aktviert ein HAccordionItem (mit animiation).
     * Startet die Animation in die richtige Richtung, abhängig von der Position der zu
     * animierenden Items.
     * 
     * @param runActiveItemNew
     *            - das item, welches aktivert werden soll.
     * @param runActiveItemOld
     *            - das item, welches vorher aktiv war.
     * @return - true on success
     */
    protected synchronized boolean run(AnimatableItem runActiveItemNew, AnimatableItem runActiveItemOld) {
        if (runActiveItemNew == runActiveItemOld) {
            LOG.warn("Animation not started, because the activeItemNew and activeItemOld are equal");
            return false;
        } else if (runActiveItemOld == null || runActiveItemNew == null) {
            LOG.warn("Animation not started, because one of the items is null");
            return false;
        } else if (isRunning()) {
            return false;
        }

        this.activeItemNew = runActiveItemNew;
        this.activeItemOld = runActiveItemOld;
        
        // callback to make the new item visible again
        activeItemNew.setVisible(false);
        addCallbackRoutine(new ICallbackRoutine() {
			@Override
			public void callback() {
				activeItemNew.setVisible(true);
			}
		});
        
        // start the animation now
        if (runActiveItemOld.rightFrom(runActiveItemNew)) {
            startAnimationToTheRight();
        } else if (runActiveItemOld.leftFrom(runActiveItemNew)) {
            startAnimationToTheLeft();
        }
        return true;
    }

    /**
     * Will expand all {@link HAccordionItem}s left to the active {@link HAccordionItem}.
     * Each {@link HAccordionItem} is expanded to the width of the Label.
     * 
     * So this Animation makes the {@link HAccordionItemsStack} expand all Items, which are on the
     * stack.
     * 
     * @param activeItem
     *            - the currently active {@link HAccordionItem}.
     * @return
     *         - true, if the animation thread was started successfully
     */
    protected boolean expandAllItemsLeftFrom(AnimatableItem activeItem) {
        if (isRunning()) {
            return false;
        }

        // remember the active item
        this.activeItemNew = activeItem;

        ArrayList<Runnable> animationWorkers = new ArrayList<Runnable>();

        ItemsIterator itemIterator = accordion.getItemsIterator();
        int activeItemNewIndex = itemIterator.getIndexOf(activeItemNew);

        // get children which should be expanded
        Deque<HAccordionItem> itemsOnTheLeft = itemIterator.getAllItemsLeftOf(activeItemNewIndex);
        // do the item animation on the left size, if there are too many items visible

        // 1. expand all but one item, which is needed to collapse the stack.
        while (itemsOnTheLeft.size() > 1) {
            ExpandAnimation expandAnimation = new ExpandAnimation(itemsOnTheLeft.pollLast(), activeItemNew,
                    HAccordionLabel.WIDTH);
            expandAnimation.setSpeed(SPEED_LOW);
            animationWorkers.add(expandAnimation);
        }

        AnimatableItem lastitem = itemsOnTheLeft.pollLast();
        // 2. collapse the stack, transferring the width to the lastitem
        CollapseAnimation collapseStackAnimation = new CollapseAnimation(accordion.getStack(), lastitem);
        collapseStackAnimation.setSpeed(SPEED_LOW);
        animationWorkers.add(collapseStackAnimation);

        // 3. expand the rest of the last item, which is necessary, because the item is wider than
        // the stack
        ExpandAnimation expandAnimation = new ExpandAnimation(lastitem, activeItemNew, HAccordionLabel.WIDTH);
        expandAnimation.setSpeed(SPEED_LOW);
        animationWorkers.add(expandAnimation);

        // execute the animation
        new Executor(animationWorkers).start();
        return true;
    }

    /**
     * Collapse all items, which are located to the left of the active item.
     * 
     * @param activeItem
     *            - the item, left ogf which the items should be collapsed
     * @return - true on success
     */
    protected boolean collapseToStackItemsLeftFrom(AnimatableItem activeItem) {
        if (isRunning()) {
            return false;
        }

        // remember the active item
        this.activeItemNew = activeItem;

        ArrayList<Runnable> animationWorkers = new ArrayList<Runnable>();
        addAnimationWorkers2CollapseLabelsToStack(animationWorkers, HAccordion.MAX_ITEMS_ON_LEFT);

        // execute the animation
        new Executor(animationWorkers).start();
        return true;
    }

    /**
     * Returns the Item, which will become active after animation.
     * 
     * @return - the item
     */
    protected AnimatableItem getactiveItemNew() {
        return activeItemNew;
    }

    /**
     * Sets the Item, which will become active after animation.
     * 
     * @param theActiveItemNew
     *            - the item
     */
    protected void setactiveItemNew(AnimatableItem theActiveItemNew) {
        this.activeItemNew = theActiveItemNew;
    }

    /**
     * Get the accordion which shall be animated by this animation.
     * 
     * @return - the {@link HAccordion} object
     */
    protected HAccordion getAccordion() {
        return accordion;
    }

    /**
     * Gets teh item, which will was active before animation.
     * 
     * @return - the item
     */
    protected AnimatableItem getactiveItemOld() {
        return activeItemOld;
    }

    /**
     * Sets the item, which will was active before animation.
     * 
     * @param theActiveItemOld
     *            - the item
     */
    protected void setactiveItemOld(AnimatableItem theActiveItemOld) {
        this.activeItemOld = theActiveItemOld;
    }

    /**
     * Returns the state of the current animation.
     * 
     * @return - the state of the current animation.
     */
    protected synchronized boolean isRunning() {
        return this.animationExecutor.isAlive();
    }

    /**
     * This method stops all the running animations, and only then sets the running state of the
     * current animation.
     * It will be called from other threads.
     */
    protected synchronized void stopAndWait() {
        while (this.animationExecutor.isAlive()) {
            // set the stop timestamp to the current time, so that the depending asyncronous workers
            // recognize themselfes as outdated and stop
            stopExecutor();
        }
    }

    protected synchronized long getStopTime() {
        return stopTimestampt;
    }

    /**
     * Adds a callback, which will be informed, that the animation is ready.
     * The callbacks will be executed on teh UI thread.
     * 
     * @param callback
     *            - callback routine
     */
    protected void addCallbackRoutine(ICallbackRoutine callback) {
        this.callbackRoutines.add(callback);
    }

    /**
     * Clear up resources
     */
    protected void dispose() {
        stopExecutor();
    }

    // helper

    /**
     * Haelt den AnimationExecutor an indem die Timestamp auf einen aktuelleren Wert gesetzt wird,
     * als der Wert der auf dem Executor laufenden anmationen.
     */
    private void stopExecutor() {
        // set the stop timestamp to the current time, so that the depending asyncronous workers
        // recognize themselfes as outdated and stop
        stopTimestampt = System.currentTimeMillis();
    }

    /**
     * Wird die Animation nach links starten.
     * Diese methode sollte benutzt weren, wenn das neue item sich links vom alten befindet.<br>
     * [ old ][new] <br>
     * [old][ new ] <br>
     * Das bedeutet, dass das neue Item sich nach <b>links</b> bewegen wird, während das alte
     * schrumpft.
     */
    private synchronized void startAnimationToTheLeft() {

        List<Runnable> animationWorkers = new ArrayList<>();

        // useful data
        ItemsIterator itemIterator = accordion.getItemsIterator();
        int activeItemNewIndex = itemIterator.getIndexOf(activeItemNew);
        int activetemOldIndex = itemIterator.getIndexOf(activeItemOld);

        // 0. expand all labels between old and new, which may have been collapsed during last
        // animations.
        // This may happen, if this is a revert animation to the left. [[][][ new ][][][ old ]]
        // activetemOldIndex > activeItemNewIndex, because its an animation to the left.
        Deque<HAccordionItem> itemsInbetween = itemIterator.getItems(activetemOldIndex + 1, activeItemNewIndex - 1);
        for (AnimatableItem item : itemsInbetween) {
            ExpandAnimation expandItemLabelAnimation = new ExpandAnimation(item, activeItemOld, HAccordionLabel.WIDTH);
            animationWorkers.add(expandItemLabelAnimation);
        }

        // 1. collapse the old item so that only the label is visible and expand the new item
        animationWorkers.add(new CollapseAnimation(activeItemOld, activeItemNew, HAccordionLabel.WIDTH));

        // 2. collapse the labels to stack
        addAnimationWorkers2CollapseLabelsToStack(animationWorkers, HAccordion.MAX_ITEMS_ON_LEFT);

        // start the animation
        animationExecutor = new Executor(animationWorkers);
        animationExecutor.start();
    }

    /**
     * Needed, when animating to the left.
     * Adds animation workers to the passed list, which will collapse all Items.
     * Just as many labels, as defined by {@link HAccordion#MAX_ITEMS_ON_LEFT} will left open.
     * The width of open items will be {@link HAccordionLabel#WIDTH}.
     * 
     * @param animationWorkers
     *            - the list of animation workers, to which the expanding workers should be
     *            appended.
     */
    private List<Runnable> addAnimationWorkers2CollapseLabelsToStack(List<Runnable> animationWorkers, int maxItemsOnLeft) {

        // useful data
        ItemsIterator itemIterator = accordion.getItemsIterator();
        int activeItemNewIndex = itemIterator.getIndexOf(activeItemNew);

        // EDIT: zum Zeitpunkt der Erzeugung der Animation kann man es noch nicht sagen, welche
        // items vor dem Aufspannen des Stack offen sein werden.
        // -> verlasse dich nicht auf die Info ber offene Items, animiere so, als ob unbekannt ist,
        // wieviele Items bereits zu und wieviele noch offen sind.
        // check if animating any labels is necessary, means if there are more items open, than
        // allowed by the max.
        Deque<HAccordionItem> itemsOnTheLeft = itemIterator.getAllItemsLeftOf(activeItemNewIndex);
        // do the item animation on the left size, if there are too many items visible
        if (itemsOnTheLeft.size() > HAccordion.MAX_ITEMS_ON_LEFT) {

            // 3. If the stack is not expanded yet - expand the stack. If the stack is already
            // expanded - the following loop will have no effect.
            for (AnimatableItem item : itemsOnTheLeft) {
                ExpandAnimation expandStackAnimation = new ExpandAnimation(accordion.getStack(), item);
                expandStackAnimation.setSpeed(SPEED_LOW);
                animationWorkers.add(expandStackAnimation);
            }

            // 4. now collapse as many open items as necessary
            int howManyItems2Collapse = itemsOnTheLeft.size() - maxItemsOnLeft;
            for (int i = 0; i < howManyItems2Collapse; i++) {
                CollapseAnimation collapseAnimation = new CollapseAnimation(itemsOnTheLeft.pollFirst(), activeItemNew);
                collapseAnimation.setSpeed(SPEED_LOW);
                animationWorkers.add(collapseAnimation);
            }
        }

        return animationWorkers;
    }

    /**
     * Wird die Animation nach rechts starten.
     * Diese methode sollte benutzt weren, wenn das neue item sich links vom alten befindet.
     * [new][ old ] <br>
     * [ new ][old] <br>
     * Das bedeutet, dass das neue Item sich nach <b>rechts</b> bewegen wird, während das alte
     * schrumpft.
     */
    private synchronized void startAnimationToTheRight() {
        List<Runnable> animationWorkers = new ArrayList<>();

        // useful information
        ItemsIterator ii = accordion.getItemsIterator();
        int indexNew = ii.getIndexOf(activeItemNew);
        int indexOld = ii.getIndexOf(activeItemOld);

        // 1. klappe so viele items auf der linken seite auf, wie vom HAccordion.MAX_ITEMS_ON_LEFT
        // vorgeschrieben.
        addAnimationWorkers2ExpandItemsFromStack(animationWorkers, HAccordion.MAX_ITEMS_ON_LEFT);

        // 2. klappe alle kinder, rechts vom neuen item zu. Das neue item konsumiert ihre pixels: es
        // gilt indexNew < indexOld, weil die animation sich nach rechts bewegt.
        Deque<HAccordionItem> items = ii.getItems(indexNew + 1, indexOld);
        while (!items.isEmpty()) {
            // mache die Slides von rechts nach links zu
            AnimatableItem item = items.pollLast();
            animationWorkers.add(new CollapseAnimation(item, activeItemNew));
        }

        // start the animation
        animationExecutor = new Executor(animationWorkers);
        animationExecutor.start();
    }

    /**
     * Needed, when animating to the right.
     * Adds animation workers to the passed list, which will expand as many items as given by
     * MAX_ITEMS_ON_LEFT
     * 
     * @param animationWorkers
     *            - the list of animation workers which will be filled with new workers.
     * @param maxItemsOnLeft
     *            - maximum number of items which will be expanded.
     * @return
     */
    private List<Runnable> addAnimationWorkers2ExpandItemsFromStack(List<Runnable> animationWorkers, int maxItemsOnLeft) {

        // useful information
        ItemsIterator ii = accordion.getItemsIterator();
        int indexNew = ii.getIndexOf(activeItemNew);

        Deque<HAccordionItem> closedItemsLeft = ii.getClosedItemsLeftOf(indexNew);
        Deque<HAccordionItem> openItemsleft = ii.getOpenItemsLeftOf(indexNew);

        // 1. expand as many items on the left side, as given by the MAXIMUM.
        if (maxItemsOnLeft > openItemsleft.size()) {
            int howManyItemsToExpand = maxItemsOnLeft - openItemsleft.size();
            for (int i = 0; i < howManyItemsToExpand; i++) {
                AnimatableItem item = closedItemsLeft.pollLast();

                // not enough items, to satisfy the maximum of items on the left side.
                if (item == null) {
                    break;
                }

                // 1.1 collapse the stack
                if (closedItemsLeft.isEmpty()) {
                    CollapseAnimation closeStackAnimation = new CollapseAnimation(accordion.getStack(), item);
                    closeStackAnimation.setSpeed(SPEED_LOW);
                    animationWorkers.add(closeStackAnimation);
                }

                // 1.2 expand the item to the width of the label
                ExpandAnimation expandLabelsAnimation = new ExpandAnimation(item, activeItemOld, HAccordionLabel.WIDTH);
                expandLabelsAnimation.setSpeed(SPEED_LOW);
                animationWorkers.add(expandLabelsAnimation);
            }
        }

        return animationWorkers;
    }

    // internal classes

    /**
     * Wird eine collection von workers ausfuehren.
     * Der Reihe nach, so dass man eine animation in Teilanimationen aufspalten kann:
     * z.B. Expandiere Slide1,
     */
    class Executor extends Thread {
        Collection<Runnable> workers;

        public Executor(Collection<Runnable> workers) {
            this.workers = workers;

            // set the priority to low
            this.setPriority(1);

            // catch all SWTExceptions, which occur on this thread. They occur when widgt is
            // disposed and should not stop the executon of the app.
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof SWTException) {
                        e.printStackTrace(); // bypass
                    } else {
                        try {
                            throw e;
                        } catch (Throwable e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            this.setName("Animation Executor");
        }

        @Override
        public void run() {
        	if(workers.isEmpty()){
        		return;
        	}
        	
        	// run on UI thread
        	Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
		        	// test: disable laying out of accordion items during the animation
		        	accordion.defferItemsLayout(true);
				}
			});
        	
            Iterator<Runnable> iter = workers.iterator();
            while (iter.hasNext()) {
                iter.next().run();
            }
            
          Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					accordion.defferItemsLayout(false);
		            accordion.layout(true, true);
		            accordion.redraw();
		            accordion.update();
				}
			});

            new AnonymousCallbackExecutor(callbackRoutines).start();
            callbackRoutines.clear();
        }
        // animation not running anymore, the thread will die
    }

    /**
     * Anonymous executor of the callbacks.
     * The callbacks are executed on an own thread, so that the current Animation will not be
     * delayed by the callbacks.
     * By executing callbacks on an own thread - they can be executed syncronously, but the
     * Animation thread will run out and the animation will stop.
     */
    class AnonymousCallbackExecutor extends Thread {

        private List<ICallbackRoutine> callBackRoutinesCopy;

        /**
         * Constructor creates an own copy of the callBackRoutines.
         * 
         * @param callBackRoutines
         */
        AnonymousCallbackExecutor(List<ICallbackRoutine> callBackRoutines) {
            callBackRoutinesCopy = new ArrayList<>(callBackRoutines);
        }

        @Override
        public void run() {
            for (final ICallbackRoutine callbackRoutine : callBackRoutinesCopy) {

                // execute callbacks on the UI thread, synchroniously
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        callbackRoutine.callback();
                    }
                });
            }
        }
    }

    /**
     * This Object will animate two items by transferring the width from one AnimatableItem(which
     * should be collapsed) to another.
     * The animation happens step by step, with a min. pause which is defined in the PERIOD constant
     * This object should be executed on a separate thread.
     */
    class CollapseAnimation implements Runnable {
        private AnimatableItem item4collapse;
        private AnimatableItem to;
        private int partlyOpen;
        private long creationTimestamp = System.currentTimeMillis();
        private int animationSpeed = SPEED;

        /**
         * Partly collapses an item.
         * 
         * @param item4collapse
         *            - the item, which will be collapsed
         * @param to
         *            - the item, which will receive the pixels, from the newly collapsed item
         * @param partlyOpen
         *            - how many pixels should be let for the (partly?)collapsed item
         */
        CollapseAnimation(AnimatableItem item4collapse, AnimatableItem to, int partlyOpen) {
            this.item4collapse = item4collapse;
            this.to = to;
            this.partlyOpen = Math.max(0, partlyOpen);
        }

        /**
         * Fully item4collapses an item.
         * 
         * @param from
         *            - the item, which will be collapsed
         * @param to
         *            - the item, which will receive the pixels, from the newly collapsed item
         */
        CollapseAnimation(AnimatableItem item4collapse, AnimatableItem to) {
            this.item4collapse = item4collapse;
            this.to = to;
            this.partlyOpen = 0;
        }

        protected void setSpeed(int speed) {
            this.animationSpeed = speed;
        }

        @Override
        public void run() {
            long startTime;

            while ((item4collapse.itemWidth > partlyOpen) && (creationTimestamp > stopTimestampt)
                    && !accordion.isDisposed()) {
                startTime = Calendar.getInstance().getTimeInMillis(); // start time

                /*
                 * IMPORTANT1: use sync execution, because otherwise the current thread will produce
                 * too many Runnables,
                 * as long as the Animation is happening, while the collapsable item is not
                 * collapsed yet.
                 * 
                 * Those runnables will be actively changing the layout of acordion, even when the
                 * accordion the collapsable item is allready collapsed, which wastes performance.
                 * 
                 * IMPORTANT2: RUN on UI thread
                 */
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {

                        // this action is outdated - do not execute it asynchronously
                        if (creationTimestamp < stopTimestampt) {
                            LOG.warn("CollapseAnimation outdated. Do not execute it.");
                            return;
                        }

                        // damit das Item nicht zu sehr zusammengeklappt wird, zB im Fall
                        // item4collapse.itemWidth=24, partlyOpen=20
                        int restWidth = item4collapse.itemWidth - partlyOpen;

                        // so dass die zur VerfÃ¼gung stehende Breite nicht Ã¼berschritten wird!
                        int stepSize = Math.min(restWidth, animationSpeed);

                        // umverteilen der Breite zwischen der Items
                        item4collapse.setWidth(item4collapse.getWidth() - stepSize);
                        to.setWidth(to.getWidth() + stepSize);

                        // redraw
                        accordion.layout(true, true);
                        accordion.redraw();
                        accordion.update();
                    }// run
                }); // Runnable for UI Thread

                long endtime = Calendar.getInstance().getTimeInMillis(); // end time
                long steptime = Math.max(0, (endtime - startTime));

                long timeToSleep = Math.max(0, (PERIOD - steptime));
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // stop the animation on interruption
                    stopExecutor();
                }
            } // while

            LOG.debug("Stop collapsing");
            LOG.debug("{} (item4collapse.itemWidth > partlyOpen) ", (item4collapse.itemWidth > partlyOpen));
            LOG.debug("{} (creationTimestamp>stopTimestampt) ", (creationTimestamp > stopTimestampt));
            LOG.debug("{} (!accordion.isDisposed())", (!accordion.isDisposed()));
        }// run
    }// CollapseAnimation

    /**
     * This Object will let one AnimatableItem A absorb the width from one AnimatableItem B.
     * This will make the item A grow and Item B shrink.
     * The animation happens step by step, with a min. pause which is defined in the PERIOD constant
     * This object should be executed on a separate thread.
     * 
     * The difference between ExpandAnimation and the CollapseAnimation is, that there is another
     * break condition:
     * 
     * ExpandAnimation will stop when the desired item is expanded to the requested width.
     * So ExpandAnimation looks at the Item, which gets the width pixels.
     * In every animation step it should check, whether the width of the item2shrink is consumed
     * completele.
     * 
     * CollapseAnimation will stop, when the collapsable item is shrinked to the requested width.
     * So CollapseAnimation looks at the Item, which loses the width pixels.
     */
    class ExpandAnimation implements Runnable {
        private AnimatableItem item4expand;
        private AnimatableItem item2shrink;
        private int requestedWidth;
        private long creationTimestamp = System.currentTimeMillis();
        private int animationSpeed = SPEED;

        /**
         * Partly collapses an item.
         * 
         * @param item4expand
         *            - the item, which will be expanded
         * @param item2shrink
         *            - the item, which will donate the pixels, to the item4expand-item
         * @param requestedWidth
         *            - how wide should be the expanded item after expansion?
         */
        ExpandAnimation(AnimatableItem item4expand, AnimatableItem item2shrink, int requestedWidth) {
            this.item4expand = item4expand;
            this.item2shrink = item2shrink;
            this.requestedWidth = Math.max(0, requestedWidth);
        }

        /**
         * Fully expands an item.
         * 
         * @param item4expand
         *            - the item, which will be expanded
         * @param item2shrink
         *            - the item, which will donate the pixels, to the item4expand-item
         */
        ExpandAnimation(AnimatableItem item4expand, AnimatableItem item2shrink) {
            this.item4expand = item4expand;
            this.item2shrink = item2shrink;
            this.requestedWidth = item4expand.getMaxWidth(); // ask the Item, to which amount it
                                                             // should grow maximally
        }

        protected void setSpeed(int speed) {
            this.animationSpeed = speed;
        }

        @Override
        public void run() {
            long startTime;

            while ((item4expand.itemWidth < requestedWidth) && (item2shrink.itemWidth > 0)
                    && (creationTimestamp > stopTimestampt && !accordion.isDisposed())) {
                startTime = Calendar.getInstance().getTimeInMillis(); // start time

                /*
                 * IMPORTANT1: use sync execution, because otherwise the current thread will produce
                 * too many Runnables,
                 * as long as the Animation is happening, while the expandable item is not
                 * expanded yet.
                 * 
                 * Those runnables will be actively changing the layout of acordion, even when the
                 * accordion the expandable item is allready expanded, which wastes performance.
                 * 
                 * IMPORTANT2: RUN on UI thread
                 */
                Display.getDefault().syncExec(new Runnable() {

                    // the resize action on the GUI thread should not be executed, when they are
                    // allready outdated, means when stop was called inbetween
                    private long creationTimestamp = System.currentTimeMillis();

                    @Override
                    public void run() {

                        // this action is outdated - do not execute it asynchronously
                        if (creationTimestamp < stopTimestampt) {
                            return;
                        }

                        // do not expand over the requestedWidth
                        int restWidth = requestedWidth - item4expand.itemWidth;

                        // so dass die zur Verfügung stehende Breite nicht überschritten wird!
                        int stepSize = Math.min(restWidth, animationSpeed);

                        // umverteilen der Breite zwischen der Items
                        item4expand.setWidth(item4expand.getWidth() + stepSize);
                        item2shrink.setWidth(item2shrink.getWidth() - stepSize);

                        // redraw
                        accordion.layout(true, true);
                        accordion.redraw();
                        accordion.update();
                    }// run
                }); // Runnable for UI Thread

                long endtime = Calendar.getInstance().getTimeInMillis(); // end time
                long steptime = Math.max(0, (endtime - startTime));
                long timeToSleep = Math.max(0, (PERIOD - steptime));
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // stop the animation on interruption
                    stopExecutor();
                }
            } // while

            LOG.warn("Stop expanding");

            LOG.warn((item4expand.itemWidth < requestedWidth) + " item4expand.itemWidth < requestedWidth ");
            LOG.warn((item2shrink.itemWidth > 0) + " item2shrink.itemWidth > 0 ");
            LOG.warn((creationTimestamp > stopTimestampt) + " creationTimestamp>stopTimestampt ");
            LOG.warn((!accordion.isDisposed()) + " !accordion.isDisposed() ");
        }// run
    }// ExpandAnimation

}
