package de.swt.custom.widgets.menu;

import org.apache.commons.lang.Validate;
import org.eclipse.swt.widgets.Composite;

import de.swt.custom.widgets.menu.rendering.Renderer;


/**
 * Menu menu class.
 * @author alf
 *
 */
public class Menu {
	
	private Renderer renderer;
	private MenuPage mainPage;
	private MenuItemClickable selected = null;

	public Menu setRenderer(Renderer renderer){
		this.renderer = renderer;
		return this;
	}
	
	public Menu setMainPage(MenuPage mainPage){
		this.mainPage = mainPage;
		mainPage.setMenu(this);
		return this;
	}
	
	public MenuPage getMainPage() {
		return mainPage;
	}
	
	public MenuItemClickable getSelected() {
		return selected;
	}
	
	protected void setSelected(MenuItemClickable selected) {
		if(this.selected!=null){
			// deselect the old one
			this.selected.setSelected(false);
		}
		this.selected = selected;
	}

	/**
	 * Uses the given {@link Renderer} to generate a Menu Composite.
	 * This should be the last step, after 
	 * <ul>
	 * <li> setting {@link Renderer}
	 * <li> setting {@link MenuPage}
	 * <li> adding Listeners to single Items in {@link MenuPage}
	 * </ul>
	 * 
	 * The generated menu does not know about the model and so it doesn't track changes of the model.
	 * 
	 * @param parent
	 * @return
	 */
	public Composite renderMenu(Composite parent){
		Validate.notNull(renderer);
		Validate.notNull(mainPage);
		return renderer.render(Menu.this, parent);
	}

	public <T extends MenuItem> T findMenuItem(String id, Class<T> clazz) {
		if(mainPage==null){
			throw new IllegalStateException("Can't search hierarchy. MainPage is not set. Use setMainPage() to set the mainPage.");
		}
		return mainPage.findMenuItem(id, clazz);
	}
	
}
