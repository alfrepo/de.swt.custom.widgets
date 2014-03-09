package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CompositeSnapper {
	static Shell shell = null;

	public static Image snapShot(Composite composite) {
		// will store the screenshot here
		Point size = composite.getSize();
		Image image = new Image(Display.getDefault(), size.x, size.y);

		GC gc = new GC(image);
		composite.print(gc);

		gc.dispose();
		return image;
	}

	public static void popup(final Image image) {
		Rectangle r = image.getBounds();

		shell = new Shell();
		shell.setSize(r.x, r.y);
		shell.setText("Image");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});
		
		shell.open();
	}

}
