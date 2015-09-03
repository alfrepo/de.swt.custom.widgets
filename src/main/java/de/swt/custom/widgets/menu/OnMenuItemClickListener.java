package de.swt.custom.widgets.menu;

/**
 * The listener which is activated, when some menu item is clicked
 *
 * @author alf
 *
 */
public interface OnMenuItemClickListener {

	public static final int MOUSE_BUTTON_LEFT = 1;
	public static final int MOUSE_BUTTON_RIGHT = 3;

	/**
	 * 
	 * The method which is activated, when corresponding control is clicked
	 *
	 * @param menuItem
	 *            - the MenuItem which was clicked
	 * @param mouseButtonNumber
	 *            - the number of the MouseButton which was clicked, numbers are
	 *            as in {@link #MOUSE_BUTTON_LEFT}, {@link #MOUSE_BUTTON_RIGHT}
	 * 
	 * @param x
	 *            - the mousePosition relative to the widget representing the
	 *            menuItem
	 * @param y
	 *            - the mousePosition relative to the widget representing the
	 *            menuItem
	 */
	void onMenuItemClick(MenuItem menuItem, int mouseButtonNumber, int x, int y);
}
