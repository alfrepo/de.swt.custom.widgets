package de.swt.custom.utils;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import de.swt.custom.utils.e4.UtilsOrientation;

public final class UtilsUI {


    public static Color hex2Color(int hexColorValue) {
        int r = (hexColorValue & 0xFF0000) >> 16;
        int g = (hexColorValue & 0xFF00) >> 8;
        int b = (hexColorValue & 0xFF);
        return new Color(Display.getCurrent(), r, g, b);
    }

    /**
     * Method will position the shell relative to the given control. The
     * position relative to the control is passed in form of style bits.
     *
     * @param shell
     * @param positionBits
     *            - the SWT position bits e.g. {@link SWT#RIGHT} | {@link SWT#TOP} or
     *            {@link SWT#LEFT} | {@link SWT#BOTTOM}
     * @param relativeToThisWidget
     */
    public static void positionShell(Shell shell, int positionBits, Control relativeToThisWidget) {
        Point p = getShellPosition(shell, inverseRightLeftInRtlMode(positionBits), relativeToThisWidget, false, 0);
        shell.setLocation(p.x, p.y);
    }

    /**
     * Does what {@link #positionShell(Shell, int, Control)} does. Additionally
     * the shell is mirrowed vertically, relative to the given control if the
     * shell does not fit on the display.
     *
     * @param shell
     * @param positionBits
     *            - the SWT position bits e.g. {@link SWT#RIGHT} | {@link SWT#TOP} or
     *            {@link SWT#LEFT} | {@link SWT#BOTTOM}
     * @param relativeToThisWidget
     */
    public static void positionShellEscapeVertically(Shell shell, int positionBits, Control relativeToThisWidget) {
        positionShellEscapeVertically(shell, positionBits, relativeToThisWidget, 0);
    }

    /**
     *
     * Does what {@link #positionShell(Shell, int, Control)} does. Additionally
     * the shell is mirrowed vertically, relative to the given control if the
     * shell does not fit on the display.
     *
     * @param shell
     * @param positionBits
     *            - the SWT position bits e.g. {@link SWT#RIGHT} | {@link SWT#TOP} or
     *            {@link SWT#LEFT} | {@link SWT#BOTTOM}
     * @param relativeToThisWidget
     * @param deltaX
     *            - if >0 - moves the shell to the right, if <0 then moves it to
     *            the left
     */
    public static void positionShellEscapeVertically(Shell shell, int positionBits, Control relativeToThisWidget,
            int deltaX) {

        // reflect the deltaX in RTL mode
        int orientation = UtilsOrientation.getDefaultOrientation();
        if (orientation == SWT.RIGHT_TO_LEFT) {
            // reflect the delta
            deltaX = -deltaX;
        }

        // retrieve the position
        Point p = getShellPosition(shell, inverseRightLeftInRtlMode(positionBits), relativeToThisWidget, true, deltaX);
        shell.setLocation(p.x, p.y);
    }

    private static Point getShellPosition(Shell shell, int positionBits, Control relativeToThisWidget,
            boolean escapeVertically, int deltaX) {
        int x = 0;
        int y = 0;

        // retrieve the infos about the widget
        Point controlAbsPosOnDisplay = getDisplayPos(relativeToThisWidget);
        Point controlSize = relativeToThisWidget.getSize();
        Point shellSize = shell.getSize();

        // position Shell horizontally
        if ((positionBits & SWT.RIGHT) == SWT.RIGHT) {
            x = controlAbsPosOnDisplay.x + controlSize.x;

        } else if ((positionBits & SWT.LEFT) == SWT.LEFT) {
            x = controlAbsPosOnDisplay.x - shellSize.x;

        } else {
            throw new IllegalArgumentException(String.format(
                    "Neither SWT.RIGHT nor the SWT.LEFT was set. Can not position the shell %s horizontally",
                    shell.getText()));
        }

        // respect deltaX
        x += deltaX;

        // position Shell vertically. If Shell does not fit onto the display -
        // escape vertically
        if ((positionBits & SWT.TOP) == SWT.TOP) {
            y = controlAbsPosOnDisplay.y;

        } else if ((positionBits & SWT.BOTTOM) == SWT.BOTTOM) {
            y = controlAbsPosOnDisplay.y + controlSize.y;

        } else {
            throw new IllegalArgumentException(String.format(
                    "Neither SWT.TOP nor the SWT.BOTTOM was passed. Can not position the shell %s horizontally",
                    shell.getText()));
        }

        if (escapeVertically) {
            boolean shellTooHighForScreen = false;
            boolean haveMoreSpaceAtTheTop = false;
            Rectangle monitorSize = getMonitorSize(shell);

            // check whether the shell is too long for the screen
            shellTooHighForScreen = monitorSize.height < (y + shellSize.y);

            // check whether there is more space at the top
            haveMoreSpaceAtTheTop = y > (monitorSize.height - y);

            if (shellTooHighForScreen && haveMoreSpaceAtTheTop) {
                if ((positionBits & SWT.TOP) == SWT.TOP) {
                    y = y - shellSize.y + controlSize.y;
                } else if ((positionBits & SWT.BOTTOM) == SWT.BOTTOM) {
                    y = y - shellSize.y - controlSize.y;
                }
            }
        }
        return new Point(x, y);
    }

    private static int inverseRightLeftInRtlMode(int positionBits) {
        int orientation = UtilsOrientation.getDefaultOrientation();

        if (orientation == SWT.RIGHT_TO_LEFT) {
            boolean hadRightBitSet = false;
            boolean hadLeftBitSet = false;

            // inverse
            if ((positionBits & SWT.RIGHT) == SWT.RIGHT) {
                hadRightBitSet = true;
                positionBits ^= SWT.RIGHT;
            }

            if ((positionBits & SWT.LEFT) == SWT.LEFT) {
                hadLeftBitSet = true;
                positionBits ^= SWT.LEFT;
            }

            if (hadRightBitSet) {
                positionBits |= SWT.LEFT;
            }

            if (hadLeftBitSet) {
                positionBits |= SWT.RIGHT;
            }
        }
        return positionBits;
    }

    /**
     * Tells whether the given coordinates occur within the bounds of the given
     * control
     *
     * @param control
     *            - the control
     * @param displayCursX
     *            - the x coordinate
     * @param displayCursY
     *            - the y coordinate
     * @return true if true
     */
    public static boolean isCursorInControl(Control control, int displayCursX, int displayCursY) {

        Rectangle rect = control.getBounds();
        Point absPos = getDisplayPos(control);
        Rectangle absRect = new Rectangle(absPos.x, absPos.y, rect.width, rect.height);
        boolean result = absRect.contains(displayCursX, displayCursY);

        return result;
    }
    
    /**
     * Tells whether the given coordinates occur within the bounds of the given
     * Widget. Not all Widgets are supported. This method may throw an IllegalStateException. 
     *
     * @param widget
     *            - the widget
     * @param displayCursX
     *            - the x coordinate
     * @param displayCursY
     *            - the y coordinate
     * @return true if true
     */
    public static boolean isCursorInWidget(Widget widget, int displayCursX, int displayCursY) {
    	if(widget instanceof Control){
    		return isCursorInControl((Control)widget, displayCursX, displayCursY);
    	}
    	else if(widget instanceof Menu){
    		return isCursorInMenu((Menu)widget, displayCursX, displayCursY);
    	}
    	throw new IllegalStateException("Could not compute Widget's bounds!");
    }
    
    /**
     * Tells whether the given coordinates occur within the bounds of the given Menu. 
     *
     * @param menu
     *            - the control
     * @param displayCursX
     *            - the x coordinate
     * @param displayCursY
     *            - the y coordinate
     * @return true if true
     */
    public static boolean isCursorInMenu(Menu menu, int displayCursX, int displayCursY) {
        try {
            Method m = org.eclipse.swt.widgets.Menu.class.getDeclaredMethod("getBounds", null);
            m.setAccessible(true);
            // retrieve the absolute, Display relative Menu coordinates via reflections
            Rectangle r = (Rectangle) m.invoke(menu, null);
            return r.contains(displayCursX, displayCursY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
    

    /**
     * Retrieves the ABSOLUTE position of event on display if
     * <ul>
     * <li>there is a widget within the event ({@link Event#widget})
     * <li>the widget is of type {@link Control}
     * </ul>
     *
     * @param event
     *            the event
     * @return the absolute Positon or -1,-1
     */
    public static Point getDisplayPos(Event event) {
        return getDisplayPos(event.widget, event.x, event.y);
    }

    /**
     * Retrieves the ABSOLUTE position of event on display if
     * <ul>
     * <li>there is a widget within the event ({@link Event#widget})
     * <li>the widget is of type {@link Control}
     * </ul>
     *
     * @param event
     *            the event
     * @return the absolute Positon or -1,-1
     */
    public static Point getDisplayPos(MouseEvent event) {
        return getDisplayPos(event.widget, event.x, event.y);
    }

    private static Point getDisplayPos(Widget widget, int widgetRelX, int widgetRelY) {
        if (widget != null) {
            if (widget instanceof Control) {
                return ((Control) widget).toDisplay(widgetRelX, widgetRelY);
            }
        }
        return new Point(-1, -1);
    }

    /**
     * Returns ABSOLUTE position of the given control on display.
     *
     * @param control
     *            - the control whichs position should be found
     * @return - the ABSOLUTE position of the given control on display.
     */
    public static Point getDisplayPos(Control control) {

        Point result = null;

        /*
         * If the control has no parent - the coordinates are already relative
         * to the Display! converting them to display would mean, that the
         * Display realative coords will be treated as composite local coords,
         * which is wrong!
         * 
         * The shell coordinates are relative to the display too!
         */
        if (control instanceof Shell || control.getParent() == null) {
            Point r = ((Shell) control).getLocation();
            result = new Point(r.x, r.y);

            /*
             * If there control has a parent and is not a shell - the
             * coordinates are relative to the parent!
             */
        } else {
            result = control.getParent().toDisplay(control.getLocation());

            /*
             * In Right to left mode the #toDisplay() funciton will return the
             * coordinates of the right corner, instead of the left corner. So the
             * coordinates have to be changed back in RTL mode.
             */
            if (UtilsOrientation.getDefaultOrientation() == SWT.RIGHT_TO_LEFT) {
                result.x -= control.getSize().x;
            }
        }

        return result;
    }

    /**
     * Adds listeners to the whole composite hierarchy
     *
     * @param composite
     * @param eventType
     * @param listener
     */
    public static void addListenerRekursively(Composite composite, int eventType, Listener listener) {
        composite.addListener(eventType, listener);
        if (composite.getChildren().length > 0) {
            for (Control child : composite.getChildren()) {
                if (child instanceof Composite) {
                    addListenerRekursively((Composite) child, eventType, listener);
                } else {
                    child.addListener(eventType, listener);
                }
            }
        }
    }

    /**
     * Removes listeners from the whole composite hierarchy
     *
     * @param composite
     * @param eventType
     * @param listener
     */
    public static void removeListenerRekursively(Composite composite, int eventType, Listener listener) {
        composite.removeListener(eventType, listener);
        if (composite.getChildren().length > 0) {
            for (Control child : composite.getChildren()) {
                if (child instanceof Composite) {
                    removeListenerRekursively((Composite) child, eventType, listener);
                } else {
                    child.removeListener(eventType, listener);
                }
            }
        }
    }

    /**
     * The primary monitor is used to retrieve the size
     *
     * @return
     */
    public static Rectangle getMonitorSize() {
        final Display display = Display.getCurrent();
        final Monitor monitor = display.getPrimaryMonitor();
        return getMonitorSize(display, monitor);
    }

    /**
     * The Monitor the size of the monitor, where the shell is displayed is
     * used.
     *
     * @param shell
     *            - the shell
     * @return
     */
    public static Rectangle getMonitorSize(Shell shell) {
        final Display display = shell.getDisplay();
        final Monitor monitor = shell.getMonitor();
        return getMonitorSize(display, monitor);
    }

    private static Rectangle getMonitorSize(Display display, Monitor monitor) {
        final Rectangle rect;
        if (monitor != null) {
            rect = monitor.getClientArea();
        } else {
            // In case we cannot find the primary monitor get the entire display
            // rectangle
            // Note that it may include the dimensions of multiple monitors.
            rect = display.getBounds();
        }
        return rect;
    }

    private UtilsUI() {
    }

}
