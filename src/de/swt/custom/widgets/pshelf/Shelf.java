package de.swt.custom.widgets.pshelf;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * This {@link PShelf} captures the mouse events to manually passt them to the {@link ShelfItem} 's
 * {@link ShelfItemWidget}s
 * 
 * @author alf
 * 
 */
public class Shelf extends PShelf {

    private final Listener[] nativeMouseUpListeners;
    private final Listener[] nativeMouseDownListeners;
    private final Listener[] nativeMouseOverListeners;
    private final Listener[] nativeMouseMoveListeners;
    private ShelfItem lastEnteredItem;

    public Shelf(Composite parent, int style) {
        super(parent, style);

        this.setRenderer(new ShelfRendererItemWidget());

        // unregister the mouseListener to handle the mouseEvents on my own
        nativeMouseUpListeners = this.getListeners(SWT.MouseUp);
        nativeMouseDownListeners = this.getListeners(SWT.MouseDown);
        nativeMouseOverListeners = this.getListeners(SWT.MouseEnter);
        nativeMouseMoveListeners = this.getListeners(SWT.MouseMove);

        for (Listener listener : nativeMouseUpListeners) {
            this.removeListener(SWT.MouseUp, listener);
        }
        for (Listener listener : nativeMouseDownListeners) {
            this.removeListener(SWT.MouseDown, listener);
        }
        for (Listener listener : nativeMouseOverListeners) {
            this.removeListener(SWT.MouseEnter, listener);
        }
        for (Listener listener : nativeMouseMoveListeners) {
            this.removeListener(SWT.MouseMove, listener);
        }

        // register own listeners
        this.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                // TODO Auto-generated method stub

            }
        });

        // handle mouse Up listeners
        this.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event e) {
                ShelfItem item = getItem(new Point(e.x, e.y));
                boolean eventHandled = false;
                eventHandled = item.mouseUp(e);
                if (!eventHandled) {
                    notifyNativeOnMouseUp(e);
                }
            }
        });

        // handle mouse Down listeners
        this.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                ShelfItem item = getItem(new Point(e.x, e.y));
                boolean eventHandled = false;
                eventHandled = item.mouseDown(e);
                if (!eventHandled) {
                    notifyNativeOnMouseDown(e);
                }
            }
        });

        // handle mouse over listeners
        this.addListener(SWT.MouseEnter, new Listener() {
            @Override
            public void handleEvent(Event e) {
                lastEnteredItem = getItem(new Point(e.x, e.y));
                boolean eventHandled = lastEnteredItem.mouseOver(e);
                if (!eventHandled) {
                    notifyNativeOnMouseOver(e);
                }
            }
        });

        // handle mouse out listeners
        this.addListener(SWT.MouseMove, new Listener() {
            @Override
            public void handleEvent(Event e) {
                ShelfItem item = getItem(new Point(e.x, e.y));
                if (lastEnteredItem != item && lastEnteredItem != null) {
                    lastEnteredItem.mouseOut(e);
                    lastEnteredItem = item;
                }
                boolean eventHandled = false;
                eventHandled = item.mouseMove(e);

                if (!eventHandled) {
                    notifyNativeOnMouseMove(e);
                }
            }
        });

        // handle mouse out listeners
        this.addListener(SWT.MouseExit, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (lastEnteredItem != null) {
                    lastEnteredItem.mouseOut(e);
                }
            }
        });
    }

    private void notifyNativeOnMouseUp(Event event) {
        for (Listener listener : nativeMouseUpListeners) {
            listener.handleEvent(event);
        }
    }

    private void notifyNativeOnMouseDown(Event event) {
        for (Listener listener : nativeMouseDownListeners) {
            listener.handleEvent(event);
        }
    }

    private void notifyNativeOnMouseOver(Event event) {
        for (Listener listener : nativeMouseOverListeners) {
            listener.handleEvent(event);
        }
    }

    private void notifyNativeOnMouseMove(Event event) {
        for (Listener listener : nativeMouseMoveListeners) {
            listener.handleEvent(event);
        }
    }

    @Override
    public ShelfItem getItem(Point point) {
        return (ShelfItem) super.getItem(point);
    }

}
