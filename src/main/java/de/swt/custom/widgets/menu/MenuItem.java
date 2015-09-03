package de.swt.custom.widgets.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

public abstract class MenuItem{

	public String id;
	public ImageDescriptor icon;
	public String text;
	
	private Menu menu;
	private Color color;
	private MenuPage predecessorPage;
	
	private ArrayList<IMenuItemListener> listeners = new ArrayList<IMenuItemListener>();
	
	// this field contains the widget, which represents the given page after rendering
	public Object widget = null;
	
	public MenuItem(MenuItemDataBundle menuItemDataBundle) {
			this(menuItemDataBundle.id, menuItemDataBundle.icon, menuItemDataBundle.text, menuItemDataBundle.menu);
	}

	public MenuItem(String id, ImageDescriptor icon, String text, Menu menu) {
		if (id == null) {
			throw new IllegalArgumentException("The id must exist");
		}
		this.id = id.toLowerCase();
		this.icon = icon;
		this.text = text;
		this.menu = menu;
	}

	public static class MenuItemDataBundle {
		public String id;
		public ImageDescriptor icon;
		public String text;
		public Menu menu;
	}
	
	/**
	 * Sets the color to the item and its children
	 * @param color - the color
	 * @return - returns this  
	 */
	public MenuItem setColor(Color color){
		this.color = color;
		notifyListeners();
		return this;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public void setPredecessor(MenuPage predecessor) {
		this.predecessorPage = predecessor;
		notifyListeners();
	}
	
	public MenuPage getPredecessorPage() {
		return predecessorPage;
	}
	
	public void addMenuItemListener(IMenuItemListener menuItemListener){
		this.listeners.add(menuItemListener);
	}
	
	public void removeMenuItemListener(IMenuItemListener menuItemListener){
		this.listeners.remove(menuItemListener);
	}
	
	public void notifyListeners(){
		List<IMenuItemListener> listenersCopy = new ArrayList<IMenuItemListener>(listeners);
		for(IMenuItemListener listener: listenersCopy){
			listener.onChange(this);
		}
	}
	
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	/**
	 * Finds the item of given class within the {@link MenuItem} / {@link MenuPage} hierarchy
	 * @param id - the id of the item
	 * @param clazz - the class of the item
	 * @return - the {@link MenuItem} with the given class and id or null
	 */
	public abstract <T extends MenuItem> T findMenuItem(String id, Class<T> clazz);
	


}
