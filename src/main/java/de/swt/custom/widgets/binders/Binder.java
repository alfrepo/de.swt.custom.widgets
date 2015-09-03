package de.swt.custom.widgets.binders;

/**
 * This object - is a shell binder.
 * It installs a set of listeners on a shell and optionally on a set of controls to make the shell
 * behave on a predefined way.
 * 
 * E.g. the shell may behave like a popup, it may disapear on click or on doubleclick.
 * Or it may apear when mouseover occurs on a predefined control.
 * 
 * @author alf
 * 
 */
public interface Binder {

    /** Clean up all the listeners and resources here */
    void dispose();
}
