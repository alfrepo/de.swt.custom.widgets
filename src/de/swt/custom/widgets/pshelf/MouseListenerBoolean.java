package de.swt.custom.widgets.pshelf;

import org.eclipse.swt.widgets.Event;

/**
 * Implementors will have the ability to capture the MouseEvents.
 * For that they will return true in the right method.
 * 
 */
public interface MouseListenerBoolean {

    /**
     * Handles Mouse Down Events.
     * 
     * @param e
     *            - Mouse Event
     * @return - Should return true, if the mouse event was handled. Otherwise return false.
     */
    boolean mouseDown(Event e);

    /**
     * Handles Mouse Up Events.
     * 
     * @param e
     *            - Mouse Event
     * @return - Should return true, if the mouse event was handled. Otherwise return false.
     */
    boolean mouseUp(Event e);

    /**
     * Handles Mouseover Events.
     * 
     * @param e
     *            - Mouse Event
     * @return - Should return true, if the mouse event was handled. Otherwise return false.
     */
    boolean mouseOver(Event e);

    /**
     * Handles MouseOut Events.
     * 
     * @param e
     *            - Mouse Event
     * @return - Should return true, if the mouse event was handled. Otherwise return false.
     */
    boolean mouseOut(Event e);

    /**
     * Handles MouseMove Events.
     * 
     * @param e
     *            - Mouse Event
     * @return - Should return true, if the mouse event was handled. Otherwise return false.
     */
    boolean mouseMove(Event e);
}
