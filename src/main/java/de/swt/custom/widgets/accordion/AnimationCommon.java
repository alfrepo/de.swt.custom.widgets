package de.swt.custom.widgets.accordion;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Klasse bündelt die Animationen, welche von verschiedenen Klassen ausgelÃ¶st werden sollen.
 */
class AnimationCommon {

    private final TimeoutCollapseHandler timeoutCollapseHandler = new TimeoutCollapseHandler();

    private final Animation animation;
    private final HAccordion haccordion;

    /**
     * Erstelle ein AnimationCommon Objekt.
     * 
     * @param animation
     *            - animation, desse n funktionalität publik gemacht werden soll
     */
    AnimationCommon(Animation animation) {
        this.animation = animation;
        this.haccordion = animation.getAccordion();
    }

    /**
     * Öffnet alle items, welche sich links vom aktiven Item befinden.
     * Wird vom Stack ausgelöst, bei einem click auf den Stack.
     * 
     * Die Callbackroutine darf null sein.
     * 
     * @param callbackRoutine
     *            - die callback routine
     */
    protected void expandAllItems(ICallbackRoutine callbackRoutine) {
        if (callbackRoutine != null) {
            animation.addCallbackRoutine(callbackRoutine);
        }
        animation.expandAllItemsLeftFrom(haccordion.getActiveItem());
    }

    /**
     * Klappt items zusammen, zu einem Stack. LÃ¤sst nur do viel Ã¼brig, wie vom
     * {@link HAccordion#MAX_ITEMS_ON_LEFT} vorgeschrieben.
     * Wird nach durch MouseOut auf einem der Labels ausgelÃ¶st, nach einem Timeout.
     * 
     * Die Callbackroutine darf null sein.
     * 
     * @param callbackRoutine
     *            - die callbackRoutine routine
     */
    protected void collapseItems(ICallbackRoutine callbackRoutine) {
        if (callbackRoutine != null) {
            animation.addCallbackRoutine(callbackRoutine);
        }
        animation.collapseToStackItemsLeftFrom(haccordion.getActiveItem());
    }

    /**
     * Gibt ein Objekt zurueck, welches zum Verwalten des Timeout gesteuerten auf und zuklappen von
     * Items benutzt werden soll.
     * 
     * @return
     */
    protected TimeoutCollapseHandler getTimeoutCollapseHandler() {
        return timeoutCollapseHandler;
    }

    /**
     * Called on dispose.
     */
    public void dispose() {
        timeoutCollapseHandler.dispose();
    }

    // classes
    /**
     * Ein wiederverwendbares Objekt welches in aktiviertem Zustand gestartet werden kann.
     * In deaktivertem Zustand hat der Aufruf von {@link #start()} keinen Effekt.
     * Im aktivierten Zustand wird der Aufruf von {@link #start()} einen Timer auslösen, welcher
     * nach einem TimeOut alle Items zuklappen wird.
     */
    protected class TimeoutCollapseHandler {

        public static final int COLLAPSE_TIMEOUT_MS = 1000; // collapse after 100 milliseconds

        /**
         * {@link #start()} der Timer
         */
        private boolean active = false;
        private Timer timer = new Timer(true); // run as daemon, or it will run after the
                                               // application was closed
        private CollapseTask task = new CollapseTask();

        /**
         * Aktiviert das Objekt. Den Aufruf von {@link #start()} kann nun den Timer
         */
        protected void activate() {
            this.active = true;
        }

        /**
         * LÃ¶st den Timer aus, falls Objekt aktiviert.
         */
        protected void start() {
            if (!active) {
                return;
            }
            // beende das vorherige Task zuerst
            stop();

            task = new CollapseTask();
            timer.schedule(task, COLLAPSE_TIMEOUT_MS);

        }

        /**
         * Macht alle vorherigen Tasks - irrelevant.
         */
        protected void stop() {
            if (!active) {
                return;
            }

            task.stop();
        }

        /**
         * Macht den Aufruf von start wirklungslos.
         */
        private void deactivate() {
            this.active = false;
        }

        /**
         * TimerTask, welches weiss wann es erstellt wurde
         */
        class CollapseTask extends TimerTask {

            private boolean stopped = false;

            @Override
            public void run() {
                if (!stopped && !haccordion.isDisposed()) {
                    haccordion.getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            collapseItems(null);
                        }
                    });
                    deactivate(); // deactivate
                }
            };

            protected void stop() {
                stopped = true;
            }
        }

        /**
         * called on dispose
         */
        protected void dispose() {
            task.stop();
            timer.cancel();
        }
    }

}
