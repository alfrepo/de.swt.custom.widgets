package de.swt.custom.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public final class Colors {

	private Colors() {

	}

	public static final int MAX_BASE_COLOR_VALUE = 256;

	public static final Color SELECTION_HEADER_BACKGROUND_COLOR = new Color(
			Display.getCurrent(), 156, 209, 103);

	public static final Color ANCHOR_BACKGROUND_COLOR = new Color(
			Display.getCurrent(), 65, 113, 43);

	public static final Color SELECTION_BACKGROUND_COLOR = new Color(
			Display.getCurrent(), 217, 232, 251);

	public static final Color SELECTION_COLOR = new Color(Display.getCurrent(), 0,0,0);

	public static final Color SELECTION_BORDER_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);

	public static final Color DIRTY_ROW_COLOR = new Color(Display.getCurrent(),
			255, 229, 159); // light orange

	public static final Color DIRTY_CELL_COLOR = new Color(
			Display.getCurrent(), 255, 170, 0); // orange

	public static final Color REQUIRED_CELL_BORDER_COLOR = new Color(
			Display.getCurrent(), 255, 93, 0); // roeliches
	// orange

	public static final Color CONFLICT_ROW_COLOR = new Color(
			Display.getCurrent(), 250, 175, 186); // light pink

	public static final Color DELETED_ROW_FG_COLOR = new Color(
			Display.getCurrent(), 255, 0, 0); // red

	public static final Color DELETED_ROW_BG_COLOR = new Color(
			Display.getCurrent(), 224, 224, 224); // light
	// grey

	public static final Color INVALID_ROW_FG_COLOR = new Color(
			Display.getCurrent(), 192, 192, 192); // medium
	// grey

	public static final Color INVALID_ROW_BG_COLOR = new Color(
			Display.getCurrent(), 224, 224, 224); // light
	// grey

	public static final Color ODD_ROW_COLOR = new Color(Display.getCurrent(),
			255, 249, 239);

	public static final Color EVEN_ROW_COLOR = new Color(Display.getCurrent(),
			255, 255, 255);

	public static final Color ODD_ROW_BG_COLOR_UNASSOCIATED = new Color(
			Display.getCurrent(), 204, 204, 204);

	public static final Color EVEN_ROW_BG_COLOR_UNASSOCIATED = new Color(
			Display.getCurrent(), 224, 224, 224);

	public static final Color TABLE_BODY_BG_COLOR = new Color(
			Display.getCurrent(), 249, 172, 7);

	public static final Color TABLE_BODY_FG_COLOR = new Color(
			Display.getCurrent(), 30, 76, 19);

	public static final Color FILTER_ROW_COLOR = new Color(
			Display.getCurrent(), 197, 212, 231);

	public static final Color DARK_SHADOW = Display.getDefault()
			.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);

	public static final Color SHADOW_GRADIENT_COLOR = new Color(
			Display.getCurrent(), 136, 212, 215);
}
