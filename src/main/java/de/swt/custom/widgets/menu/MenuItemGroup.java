package de.swt.custom.widgets.menu;

import java.util.Collection;

import org.eclipse.swt.graphics.Color;

public class MenuItemGroup  extends MenuItem {
	
	public Collection<MenuItem> items;
	
	public MenuItemGroup(MenuItemGroupData menuItemGroupData) {
		super(menuItemGroupData);
		this.items = menuItemGroupData.items;
	}
	
	public static class MenuItemGroupData extends MenuItem.MenuItemDataBundle{
		Collection<MenuItem> items;
		{
			this.id = ""; // default
		}
	}

	@Override
	public <T extends MenuItem> T findMenuItem(String id, Class<T> clazz) {
		T result = null;

		// compare the current item
		if(id.equals(id) && clazz.isAssignableFrom(getClass())){
			result = clazz.cast( this );

		// search in group items
		}else{
			if(items != null){
				for(MenuItem item:items){
					result = item.findMenuItem(id, clazz);
					if(result!=null) break;
				}
			}
		}

		return result;
	}
	
	@Override
	public MenuItemGroup setColor(Color color) {
		super.setColor(color);
		for(MenuItem item:this.items){
			item.setColor(color);
		}
		return this;
	}
	
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		for(MenuItem item:items){
			item.setMenu(menu);
		}
	}
}
