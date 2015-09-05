package de.swt.custom.widgets.binders;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.swt.custom.utils.UtilsUI;

/**
 * <p>
 * This shell binder installs a set of listeners to a Control, in order to create a popup, when the
 * mouse is over the control. The popup disappears, when the mouse is moved away from control. The
 * popup is made persistant (it wont disappear any more, only on explicit close) when the user
 * performs a click inside the popup.
 * </p>
 *
 * <p>
 * There is only one binder, which may create popups for many controls. This is ok, since there is
 * always only one popup active.
 * </p>
 *
 * <p>
 * To use this class create an instance and use the {@link #bind(Control)} method to introduce
 * controls to the binder, over which popups should be created.
 *
 * Implement method {@link #createPopup(Control)} and {@link #createPopupContent(Control, Shell)} to
 * define the popup.
 * </p>
 *
 * @author alf
 *
 */
public abstract class BinderWindowPopup<T extends Control> implements Binder {

    private static final int OVERLAP_CONTROL_BY_PX = 15;

    public enum PopupTrigger {
        MOUSEOVER, MOUSECLICK
    }

    private Class<T> controlType;
    private PopupTrigger popupTrigger = PopupTrigger.MOUSEOVER;

    private ArrayList<PopupDetachedListener> popupDetachedListeners = new ArrayList<PopupDetachedListener>();

    /**
     * This field is updated, when the popup is opened. It is set to null, when the popup is closed
     * to unbound. Only one popup can be under {@link BinderWindowPopup}'s control.
     * Indeed the popups may be detached, so that they will not be under the binder's control any
     * more.
     *
     * ACHTUNG:
     * the Binder is bound to the Popup via this field only.
     * When a mousemove event occurs - then it is checked, whether the shell from this var is set
     * and is under the cursor.
     * To detach the shell - is is enough to unset this field
     */
    private Shell currentPop;

    /**
     * Remember the current control.
     * So that the popup is not reopened, when the mouse is moved over a control,
     * which already caused the popup to apear.
     *
     * ACHTUNG: should forget this control, when the popup is closed or detached
     */
    private T currentControl;

    private Display display = Display.getDefault();

    private MouseTrackAdapter mouseTrackListenerWhichDisplaysPopup;
    private MouseAdapter mouseClickListenerWhichDisplaysPopup;
    private Listener listenerStopHidingThePopupOnClick;
    private Listener listenerMouseMove;

    // reusable fields
    private Point cursorLocation;

    /**
     * Creates a new Binder which will control the popup
     *
     * @param popup
     *            - the popup which should appear, when the mouse is over mouseOverControl
     */
    public BinderWindowPopup(Class<T> controlType) {
    	
        Validate.notNull(controlType);
        this.controlType = controlType;

        // on mouseOver listener displays the popup
        this.mouseTrackListenerWhichDisplaysPopup = new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                T mouseOverControl = getEventControl(e);
                showPopup(mouseOverControl);
                System.out.println("Show popup");
            }
        };

        // alternative to mouseOver. On mouseClick display the popup
        this.mouseClickListenerWhichDisplaysPopup = new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                T mouseOverControl = getEventControl(e);
                showPopup(mouseOverControl);
                System.out.println("Show popup");
            }
        };

        // onClick on shell stop hiding the shell on exit
        this.listenerStopHidingThePopupOnClick = new Listener() {
            @Override
            public void handleEvent(Event e) {
                detachShell(BinderWindowPopup.this.currentPop);
            }
        };

        // onMouseMove should hide popup and forget the control, on mouseOut
        this.listenerMouseMove = new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (BinderWindowPopup.this.currentPop == null || BinderWindowPopup.this.currentPop.isDisposed()) {
                    return;
                }

                if (!isCursorInControlOrPopup(BinderWindowPopup.this.currentControl, BinderWindowPopup.this.currentPop)) {
                    hidePopup();
                    BinderWindowPopup.this.currentControl = null;
                }
            }
        };

        display.addFilter(SWT.MouseMove, listenerMouseMove);
    }

    /**
     * Switches the binder behaviour. Makes popups appear according to the new trigger.
     * Old bindings are not affected.
     * If a control was added with a MouseOver as trigger, it wont be switched to MouseClick as
     * trigger. It has to be undound and bound manually.
     *
     * @param popupTrigger
     */
    public void setPopupTrigger(PopupTrigger popupTrigger) {
        this.popupTrigger = popupTrigger;
    }

    /**
     * Binds the popup shell to the control, so that the popup behaves as specified in the class
     * Description.
     *
     * @param mouseOverControl
     *            - the control which the mouse has to be moved over to make the shell appear
     */
    public void bind(T mouseOverControl) {
        // register listeners to the control
        if (popupTrigger.equals(PopupTrigger.MOUSEOVER)) {
            mouseOverControl.addMouseTrackListener(this.mouseTrackListenerWhichDisplaysPopup);

        } else if (popupTrigger.equals(PopupTrigger.MOUSECLICK)) {
            mouseOverControl.addMouseListener(this.mouseClickListenerWhichDisplaysPopup);

        } else {
            throw new IllegalStateException("Binding failed, illegal trigger.");
        }
    }

    public void unbind(T mouseOverControl) {
        // unregister listeners to the control
        mouseOverControl.removeMouseTrackListener(this.mouseTrackListenerWhichDisplaysPopup);
    }

    /**
     * The listener will be triggered, after the popup is detached and is not under the control of
     * the binder anymore.
     *
     * @param listener
     */
    public void addPopupDetachListener(PopupDetachedListener listener) {
        popupDetachedListeners.add(listener);
    }

    public void removePopupDetachListener(PopupDetachedListener listener) {
        popupDetachedListeners.remove(listener);
    }

    @Override
    public void dispose() {
        if (!currentControl.isDisposed()) {
            currentControl.removeMouseTrackListener(mouseTrackListenerWhichDisplaysPopup);
        }
        display.removeFilter(SWT.MouseDown, BinderWindowPopup.this.listenerStopHidingThePopupOnClick);
        display.removeFilter(SWT.MouseMove, BinderWindowPopup.this.listenerMouseMove);
        this.currentControl = null;
        this.currentPop = null;
    }

    /**
     * Computes the position of the popup,
     * depending on the control, where the mouseover occurred and
     * the shell, which represents the popup.
     *
     * @param mouseOverControl
     * @param shell
     * @return - the coordinates of the popup
     */
    protected Point computePopupPosition(T mouseOverControl, Shell shell) {
        Point size = shell.getSize();
        Point pos = mouseOverControl.toDisplay(OVERLAP_CONTROL_BY_PX, -size.y + OVERLAP_CONTROL_BY_PX);
        return new Point(pos.x, pos.y);
    }

    /**
     * Is triggered after creation, before the popup opens
     *
     * @param control
     *            - the control which the mouse is over
     * @param shell
     *            - the shell of the popup
     */
    protected void createPopupContent(T control, Shell shell) {
        // nothing. may be overridden
    }

    /**
     * Is triggered BEFORE a popup is closed
     *
     * @param shell
     *            - the shell of the popup
     */
    protected void onPopupClosing(Shell shell) {
        // nothing. may be overridden
    }

    /**
     * Creates a popup shell.
     * Used to generate popups on Label mouseover.
     *
     * @param control
     *            - the control which the mouse is over
     * @return
     */
    protected abstract Shell createPopup(T control);

    /**
     * Shows the popup.
     * Remembers the current control, which the mouse is currently over now
     *
     * @param mouseOverControl
     *            - the control which the mouse is over now
     */
    private void showPopup(T mouseOverControl) {
        // do not reopen the popup for the same control
        if (mouseOverControl.equals(this.currentControl)) {
            return;
        }

        // remember the current control
        this.currentControl = mouseOverControl;

        // try closing the last active popup if necessary
        hidePopup();

        // generate a new popup
        this.currentPop = createPopup(mouseOverControl);

        // hide the popup so that it wont appear immediately on wrong location
        this.currentPop.setVisible(false);

        Validate.notNull(this.currentPop);

        // and create the content
        createPopupContent(mouseOverControl, currentPop);

        // calculate the position
        Point pos = computePopupPosition(mouseOverControl, this.currentPop);

        // position the popup and open it
        this.currentPop.setLocation(pos.x, pos.y);
        this.currentPop.moveAbove(mouseOverControl);
        this.currentPop.setVisible(true);
        this.currentPop.open();
        display.addFilter(SWT.MouseDown, BinderWindowPopup.this.listenerStopHidingThePopupOnClick);
    }

    /**
     * Detach the popup from the binder. The popup then becomes an independent window.
     * It will not be under the control of the binder anymore.
     *
     * @param popup
     *            - which popup should be detached?
     */
    private void detachShell(Shell popup) {
        // notify the listeners
        notifyPopupDetachedListeners(BinderWindowPopup.this.currentPop, BinderWindowPopup.this.currentControl);

        /*
         * forget the current popup without closing it
         * the window will remain open
         */
        BinderWindowPopup.this.currentPop = null;

        // forget the current composit
        BinderWindowPopup.this.currentControl = null;

    }

    /**
     * Closes the current popup.
     */
    private void hidePopup() {
        if (this.currentPop != null && !this.currentPop.isDisposed()) {
            this.onPopupClosing(this.currentPop);
            this.currentPop.close();
        }
        this.currentPop = null;
    }

    /**
     * Checks whether the cursor is currently over the window or the control
     *
     * @param currentControl
     *            - the control which the cursor was last over
     * @param currentPopup
     *            - the popup which was opened at least
     * @return
     */
    private boolean isCursorInControlOrPopup(Control currentControl, Shell currentPopup) {
        boolean isInPop = false;
        boolean isInControl = false;
        cursorLocation = display.getCursorLocation();

        if (currentControl != null) {
            isInControl = UtilsUI.isCursorInControl(currentControl, cursorLocation.x, cursorLocation.y);
        }

        if (currentPopup != null) {
            // already display coordinates
            isInPop = UtilsUI.isCursorInControl(currentPopup, cursorLocation.x, cursorLocation.y);
        }

        return isInControl || isInPop;
    }

    @SuppressWarnings("unchecked")
    private T getEventControl(TypedEvent e) {
        if (e.widget == null) {
            throw new RuntimeException("The event's e.widget is null");
        }
        if (!(controlType.isAssignableFrom(e.widget.getClass()))) {
            throw new RuntimeException("A Control is expected because this class is only able to bind controls");
        }
        return (T) e.widget;
    }

    private void notifyPopupDetachedListeners(Shell popup, Control control) {
        if (popup == null || control == null || popup.isDisposed() || control.isDisposed()) {
            return;
        }
        for (PopupDetachedListener l : this.popupDetachedListeners) {
            l.onPopupDetached(popup, control);
        }
    }

    public abstract static class PopupDetachedListener {
        public abstract void onPopupDetached(Shell popup, Control control);
    }
}
