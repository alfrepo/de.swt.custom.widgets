package de.swt.custom.widgets.accordion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * This Control should draw a lot of stuff, to simulate a fully loaded Control.
 * 
 * @author alf
 * 
 */
public class HeavyControl extends Composite {

	public final static int maxRecursiveChildren = 3;
	ThreeInRowLayout tl = new ThreeInRowLayout();

	List<HeavyControl> children = new ArrayList<>();

	public HeavyControl(Composite parent, int style) {
		this(parent, style, maxRecursiveChildren);
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		tl.computeSize(this, wHint, hHint, true);
		return super.computeSize(wHint, hHint);
	}

	@Override
	public void layout(boolean changed, boolean all) {
		tl.layout(this, changed);
		super.layout(changed, all);
	}

	public HeavyControl(Composite parent, int style, int recursiveChildren) {
		super(parent, style);
		setBackground(getRandomColor());
		setBackgroundMode(SWT.INHERIT_NONE);
		this.setLayout(tl);
		if (recursiveChildren > 0) {
			generateChildren(recursiveChildren);
		}
	}

	public List<HeavyControl> generateChildren(int recursiveChildren) {
		int nextRecursiveChildren = recursiveChildren - 1;
		children.add(new HeavyControl(this, SWT.NONE, nextRecursiveChildren));
		children.add(new HeavyControl(this, SWT.NONE, nextRecursiveChildren));
		children.add(new HeavyControl(this, SWT.NONE, nextRecursiveChildren));
		return children;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		tl.computeSize(this, wHint, hHint, true);
		return super.computeSize(wHint, hHint, changed);
	}

	public void addChild(HeavyControl control) {
		children.add(control);
	}

	static Color getRandomColor() {
		int r = getRandomRGBValue();
		int g = getRandomRGBValue();
		int b = getRandomRGBValue();
		return new Color(Display.getDefault(), r, g, b);
	}

	static int getRandomRGBValue() {
		return (int) Math.round(Math.random() * 255);
	}
	
	// test
	@Override
	public void redraw() {
		super.redraw();
	}

	public void redraw(int x, int y, int width, int height, boolean all) {
		super.redraw(x, y, width, height, all);
	};
}
