package de.swt.custom.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class Constants {

    private Constants() {
    }

    private static GC defaultGc = new GC(new Shell());
    private static FontMetrics defaultFontMetric = defaultGc.getFontMetrics();

    public static final int MIN_BUTTON_WIDTH = Dialog.convertHorizontalDLUsToPixels(defaultFontMetric, 50); // http://msdn.microsoft.com/en-us/library/ms997619.aspx

    public static Shell getShell() {
        Shell shell = new Shell();
        shell.setLayout(new FillLayout());
        shell.setText("SWT Application");

        return shell;
    }

    public static void open(Shell shell) {
        Display display = Display.getDefault();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
