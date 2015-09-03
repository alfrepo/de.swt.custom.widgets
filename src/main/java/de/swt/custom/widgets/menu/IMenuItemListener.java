package de.swt.custom.widgets.menu;

public interface IMenuItemListener {
	/**
	 * 
	 * When the item is changed - the listener will be notified
	 * 
	 * @param menuItem - the menuItem which has changed
	 * @param classOfMenuItem
	 */
	void onChange(MenuItem menuItem);
}
