package de.swt.custom.widgets.accordion;

/**
 * Die Klassen welche dieses Interface implementieren können an
 * {@link Animation#addCallbackRoutine(ICallbackRoutine)} übergeben werden,
 * um aufgerufen zu werden wenn die Animation beendet wird.
 */
public interface ICallbackRoutine {

    /**
     * Wird ausgeführt, wenn die {@link Animation} asynchron ausgeführt und beendet ist.
     * Die Instanzen dieser Klasse sollten per
     * {@link Animation#addCallbackRoutine(ICallbackRoutine)} registriert werden.
     */
    void callback();
}
