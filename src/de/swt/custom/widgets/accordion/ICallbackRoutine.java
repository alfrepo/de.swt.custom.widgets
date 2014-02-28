package de.swt.custom.widgets.accordion;

/**
 * Die Klassen welche dieses Interface implementieren k�nnen an
 * {@link Animation#addCallbackRoutine(ICallbackRoutine)} �bergeben werden,
 * um aufgerufen zu werden wenn die Animation beendet wird.
 */
public interface ICallbackRoutine {

    /**
     * Wird ausgef�hrt, wenn die {@link Animation} asynchron ausgef�hrt und beendet ist.
     * Die Instanzen dieser Klasse sollten per
     * {@link Animation#addCallbackRoutine(ICallbackRoutine)} registriert werden.
     */
    void callback();
}
