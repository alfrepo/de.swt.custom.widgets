package de.swt.custom.widgets.menu;

public interface ListenerModelUpdate {

	/**
	 * Is triggered when the model is updated. 
	 * May be used to e.g. regenerate the menu. 
	 * @param menu
	 */
	void onModelUpdate(Menu menu);
}
