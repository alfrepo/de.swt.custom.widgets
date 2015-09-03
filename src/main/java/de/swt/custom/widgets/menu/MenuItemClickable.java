package de.swt.custom.widgets.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

public class MenuItemClickable extends MenuItem {
	
	private boolean selected = false; 
	private boolean onSelectionPath = false; 
	public MenuPage subPage = null;
	
	private ArrayList<OnMenuItemClickListener> menuItemClickListeners = new ArrayList<OnMenuItemClickListener>();
	
	public MenuItemClickable(String id, ImageDescriptor icon, String text, Menu menu, MenuPage subPage){
		super(id, icon, text, menu);
		this.setSubPage(subPage);
	}
	
	public static class MenuItemClickableData extends MenuItem.MenuItemDataBundle{
		public MenuPage menuPage;
	}
	
	public MenuPage getSubPage() {
		return subPage;
	}

	public void setSelected(boolean isSelected) {
		if(this.selected != isSelected){
			this.selected = isSelected;
			
			// update the pointer to selected item in Menu
			getMenu().setSelected(this);
			
			// update selection path
			setOnSelectionPath(isSelected);
			
			// update the model
			notifyListeners();
		}
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public void setOnSelectionPath(boolean onSelectionPath) {
		if(this.onSelectionPath != onSelectionPath){
			this.onSelectionPath = onSelectionPath;
			// pass the selection on though the hierarchy
			if(getPredecessorPage() !=null && getPredecessorPage().getPredecessor() !=null){
				getPredecessorPage().getPredecessor().setOnSelectionPath(onSelectionPath);
			}
			notifyListeners();			
		}
	}
	
	public boolean isOnSelectionPath() {
		return this.onSelectionPath;
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		if(getSubPage()!=null){
			getSubPage().setMenu(menu);
		}
	}

	
	public void setSubPage(MenuPage subPage) {
		this.subPage = subPage;
		if(subPage != null){
			subPage.setPredecessor(this);	
		}
		notifyListeners();
	}
	
	public boolean hasMenuItemClickListeners() {
		return !menuItemClickListeners.isEmpty();
	}
	
	public List<OnMenuItemClickListener> getMenuItemClickListenersCopy() {
		// copy
		return new ArrayList<OnMenuItemClickListener>(menuItemClickListeners);
	}

	public void addMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		this.menuItemClickListeners.add(menuItemClickListener);
	}
	
	public boolean removeMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		return this.menuItemClickListeners.remove(menuItemClickListener);
	}

	public MenuItemClickable(MenuItemClickableData menuItemDataBundle) {
		this(menuItemDataBundle.id, 
				menuItemDataBundle.icon, 
				menuItemDataBundle.text, 
				menuItemDataBundle.menu, 
				menuItemDataBundle.menuPage);
	}
	

	@Override
	public <T extends MenuItem> T findMenuItem(String id, Class<T> clazz) {
		T result = null;

		// compare the current item
		if(this.id.equals(id) && clazz.isAssignableFrom(getClass())){
			result = clazz.cast( this );

		// search in subPage
		}else{
			if(subPage != null){
				result = subPage.findMenuItem(id, clazz);	
			}
		}

		return result;
	}
	

	@Override
	public MenuItemClickable setColor(Color color) {
		super.setColor(color);
		if(this.subPage != null){
			this.subPage.setColor(color);	
		}
		notifyListeners();
		return this;
	}
}
