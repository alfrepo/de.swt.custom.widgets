package de.swt.custom.widgets.menu;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;

public class MenuPage {

	private Menu menu;
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private LayoutMenuPage layoutMenuPage;
	private Color color;
	private MenuItemClickable predecessor;
	
	// this field contains the widget, which represents the given page after rendering
	public Object widget = null;
	
	public MenuPage(Menu menu) {
		this(menu, null);
	}

	public MenuPage(Menu menu, ArrayList<MenuItem> items) {
		this.menu = menu;
		if(items != null){
			for(MenuItem item: items){
				addItem(item);
			}
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		for(MenuItem item:items){
			item.setColor(color);
		}
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
		for(MenuItem item : this.getItems()){
			item.setMenu(menu);
		}
	}

	public ArrayList<MenuItem> getItems() {
		return items;
	}

	public LayoutMenuPage getLayoutMenuPage() {
		return layoutMenuPage;
	}

	public void addItem(MenuItem menuItem) {
		this.items.add(menuItem);
		menuItem.setPredecessor(this);
	}
	
	public MenuItemClickable findMenuItemClickable(String id){
		return findMenuItem(id, MenuItemClickable.class);
	}
	
	public MenuItemGroup findMenuItemGroup(String id){
		return findMenuItem(id, MenuItemGroup.class);
	}
	
	public MenuItemClickable getPredecessor() {
		return predecessor;
	}
	
	public void setPredecessor(MenuItemClickable predecessor) {
		this.predecessor = predecessor;
	}

	public <T extends MenuItem> T findMenuItem(String id, Class<T> clazz) {
		T result = null;
		
		for (MenuItem item : items) {
			result = item.findMenuItem(id, clazz);
			
			if (result != null)
				break;
		}
		return result;
	}
}
