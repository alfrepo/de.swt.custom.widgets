package de.swt.custom.widgets.menu.rendering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.swt.custom.utils.UtilsUI;
import de.swt.custom.widgets.menu.FactoryLayouts;
import de.swt.custom.widgets.menu.IMenuItemListener;
import de.swt.custom.widgets.menu.Menu;
import de.swt.custom.widgets.menu.MenuItem;
import de.swt.custom.widgets.menu.MenuItemClickable;
import de.swt.custom.widgets.menu.MenuItemGroup;
import de.swt.custom.widgets.menu.MenuPage;
import de.swt.custom.widgets.menu.OnMenuItemClickListener;

public class Renderer {
	
	// This constant is used to attach Shells like context menus, to the FLowOutMenuShell's data. In order for the flow out not to be closed when mouse hovers over the attached Shell
	public static final String MANAGED_SHELLS_LIST = "MANAGED_WINDOWS";

	// all the settings, which affect the style of the widget
	private Style style;
	
	// TODO - extract composites to factory. Write down about SWT layouts by using SWT.
	// Layouts
	private FactoryLayouts factoryLayouts = new FactoryLayouts();
	
	private PopupController popupController;
	
	
	// TODO - use animation when opening / closing Shells
	// Animation
	private IMovement itemAnimationEasingFunction = new LinearInOut();
	private int animationDurationMs = 100;
	
	public Composite render(Menu menu, Composite parent){
		Validate.notNull(menu.getMainPage());
		return render(menu.getMainPage(), parent, Style.get(), menu);
	}
	
	public Composite render(MenuPage mainPage, Composite parent, Style style, Menu menu){
		//remember the style
		this.style = style;
		
		// transfer the Style from Style into the menu
		initStyleOnMenuBeforeRednering(style, mainPage);
		
		// init with shell
		popupController = new PopupController(parent.getShell());
		
		// render the menu now
		renderMenuPageRekursively(mainPage, parent, 1, menu);

		return parent;
	}
	
	/**
	 * Call this before rendering the menu to initialize the style.
	 * @param mainPage
	 */
	private void initStyleOnMenuBeforeRednering(Style style, MenuPage mainPage){
		
		int cnt = 1;
		for(MenuItem item:mainPage.getItems()){
			item.setColor(getColor(style, item, 1, cnt));
			cnt++;
		}
	}
	
	private void renderMenuPageRekursively(MenuPage menuPage, Composite parent, int lvl, Menu menu){
		// create lvl1 page
		AbstractCompositeMenuPage compositeMenuPage = createCompositeMenuPage(lvl, parent, menuPage);

		// store the widget reference inside the model
		menuPage.widget = compositeMenuPage;
		
		// render children in menupage
		renderMenuPageRekursively(menuPage.getItems(), compositeMenuPage, lvl, menu);
		
		// layout children using page's layout (add layoutData)
		for(Control child : compositeMenuPage.getClientAreaComposite().getChildren()){
			child.pack();
			compositeMenuPage.layoutChild(child, child.getSize().y);
		}
		parent.layout(true, true);
	}
	
	private void addListenerToCreateShellOnMenuItemHover(final Composite menuItemComposite, final MenuPage subMenuPage, final int lvl, final Menu menu){
		if(menuItemComposite==null){
			return;
		}

		// create the shell in Listener - late, lazy creation
		MouseTrackListenerCompositeHierarchyAdapter mouseTrackListenerCompositeHierarchyAdapter = new MouseTrackListenerCompositeHierarchyAdapter() {
			Shell subMenuShell;
			
			@Override
			public void mouseEnter(MouseEvent e) {
				if(subMenuShell!=null && !subMenuShell.isDisposed()){
					// shell still open, no need to reopen it
					return;
				}
				
				// create subMenu Shell
				subMenuShell = new Shell(menuItemComposite.getShell(), SWT.TOOL | SWT.NO_TRIM ){
					@Override
					public String toString() {
						return "Shell {SWT Application} : flowout Shell";
					}
					@Override
					protected void checkSubclass() {
						// allow subclassing
					}
				};
				// set the shell under popupController's control. The popupController will close it when appropriate
				popupController.addShell(lvl, menuItemComposite, subMenuShell);
				// layout Shell
				subMenuShell.setLayout(new FillLayout()); // TODO move this shell and layout to own class
				// fill it
				renderMenuPageRekursively(subMenuPage, subMenuShell, lvl, menu);
				//set the size of the shell to wrap the children
				subMenuShell.pack();
				// set fixed width:
				subMenuShell.setSize(style.getMENU_POPUP_WIDTH(), subMenuShell.getSize().y);
				
				// position Shell near the menuItem
				Point rightTopCorner = menuItemComposite.toDisplay(menuItemComposite.getSize().x, 0);
				subMenuShell.setLocation(rightTopCorner.x, rightTopCorner.y);
				//and open it
				subMenuShell.open();
			}
			
			@Override
			void mouseExit(MouseEvent e) {}
		};
		
		// listen for mouse enter exit event on whole hierarchy
		mouseTrackListenerCompositeHierarchyAdapter.listen(menuItemComposite);

	}
	
	
	private void renderMenuPageRekursively(List<MenuItem> menuItems, AbstractCompositeMenuPage compositeMenuPage, int lvl, final Menu menu){
 		Composite parentForChildren = compositeMenuPage.getClientAreaComposite();
		
		// iterate all items in menuPage
		for(final MenuItem menuItem:menuItems){
			
			// add listener which opens next level, if necessary
			if(menuItem instanceof MenuItemGroup){
				final MenuItemGroup menuItemGroup = (MenuItemGroup) menuItem;

				// create group header composite
				createCompositeGroupHead(menuItemGroup, parentForChildren, lvl);
				
				// expand items and add them rekursively
				renderMenuPageRekursively(new ArrayList<MenuItem>(menuItemGroup.items), compositeMenuPage, lvl, menu);

				// create group closing composite
				createCompositeGroupBottom(menuItemGroup, parentForChildren, lvl);
				
			}else if(menuItem instanceof MenuItemClickable){
				final MenuItemClickable menuItemClickable = (MenuItemClickable) menuItem;
				
				// render the menuItem
				final AbstractCompositeMenuItem  menuItemComposite = createCompositeMenuItem(lvl, parentForChildren, menuItemClickable);
				
				// store the widget reference inside the model
				menuItem.widget = menuItemComposite;
				
				boolean hasClickListeners = (menuItemClickable.hasMenuItemClickListeners());
				boolean hasSubPage = menuItemClickable.getSubPage() != null;
				boolean hasIcon = menuItemClickable.icon != null;

				
				// if no item was rendered due to some reason - go on with other items
				if(menuItemComposite==null){
					continue;
				}
				
				if(hasIcon){
					menuItemComposite.setIcon(menuItemClickable.icon.createImage());
				}

				// highlight items with listeners behind it
				final Animator animator = setupHightlightAnimation(menuItemClickable, menuItemComposite, lvl);
				
				// set selection according to the model
				updateSelection(menuItemClickable, animator);
				
				
				// apply the click listeners
				if(hasClickListeners){
					// cursor turn to the hand
					menuItemComposite.setCursor(style.getCURSOR_HAND());
					
					// click listener from model are triggered
					final Listener clickListener = new Listener() {
						@Override
						public void handleEvent(Event event) {
							if(menuItemComposite.isDisposed()){
								return;
							}
		                    // modify the model if left click occured. 1 is for left mouseButton
							if(style.getMENU_PERSISTS_SELECTION() && event.button == 1){
								menuItemClickable.setSelected(true);	
							}
							
							// now do the semantics
							for(OnMenuItemClickListener listener:menuItemClickable.getMenuItemClickListenersCopy()){
								listener.onMenuItemClick(menuItem, event.button, event.x, event.y);
							}
						}
					};
					UtilsUI.addListenerRekursively(menuItemComposite, SWT.MouseDown, clickListener);
					// do not need to unregister listener, since it will be disposed together with composite 
				}
				
				// react on changes in menuItem Model
				menuItemClickable.addMenuItemListener(new IMenuItemListener() {
					@Override
					public void onChange(MenuItem menuItemWhichChanged) {
						try{
							// TODO - use animation to set Selection
							updateSelection(menuItemClickable, animator);
							menuItemComposite.redraw();							
						}catch(SWTException e){
							menuItemClickable.removeMenuItemListener(this);
						}
					}
				});
				
				// menuName
				menuItemComposite.setText(menuItem.text);

				// next level page if a nested shell exists AND a composite was created
				if(hasSubPage){
					addListenerToCreateShellOnMenuItemHover(menuItemComposite, menuItemClickable.getSubPage(), lvl+1, menu);
					
					// hide or show the subpage arrow
					menuItemComposite.setVisibleLblHasSubpages(hasSubPage);
				}
				
			}else{
				throw new IllegalArgumentException("Unknown type "+menuItem.getClass());
			}
		}
	}
	
	// HELPER FUNCTIONS
	
	private void updateSelection(MenuItemClickable menuItemClickable, Animator itemAnimator){
		if(menuItemClickable.isSelected()){
			itemAnimator.highlightMenuItem();
			
		}else if(menuItemClickable.isOnSelectionPath()){
			itemAnimator.highlightMenuItem();
			
		}else{
			itemAnimator.unhighlightMenuItem();
		}
	}
	
	private Animator setupHightlightAnimation(final MenuItemClickable menuItemClickable, final AbstractCompositeMenuItem compositeMenuItem, final int lvl){
		final Animator animator = new Animator(menuItemClickable, compositeMenuItem, style, lvl);
		MouseTrackListenerCompositeHierarchy mouseTrackListenerCompositeHierarchy = new MouseTrackListenerCompositeHierarchy(){
			@Override
			void mouseEnter(MouseEvent e) {
				try{
					animator.highlightMenuItem();	
				}catch(SWTException diposedException){
					this.forget(compositeMenuItem);
				}
			}
			@Override
			void mouseExit(MouseEvent e) {
				if(menuItemClickable.isSelected() || menuItemClickable.isOnSelectionPath()){
					return;
				}
				try{
					animator.unhighlightMenuItem();	
				}catch(SWTException diposedException){
					this.forget(compositeMenuItem);
				}
			}
		};
		
		// start listening
		mouseTrackListenerCompositeHierarchy.listen(compositeMenuItem);

		return animator;
	}
	
	//TODO - move layouts to factory
	
	private AbstractCompositeMenuPage createCompositeMenuPage(int lvl, Composite parent, MenuPage menuPage){
		if(lvl <= 1){
			return new CompositeMenuPageLevelOne(parent, SWT.NONE, style);
		}
		CompositeMenuPageLevelTwo compositeMenuPageLevelTwo = new CompositeMenuPageLevelTwo(parent, SWT.NONE, style);
		compositeMenuPageLevelTwo.setLeftStripeColor(menuPage.getColor());
		return compositeMenuPageLevelTwo;
	}
	
	private AbstractCompositeMenuItem createCompositeMenuItem(int lvl, Composite parent, MenuItemClickable item){
		//lvl 1
		if(lvl <= 1){
			return new CompositeMenuItemLvlOne(parent, SWT.NONE, style);
			
		//lvl others
		}else{
			return new CompositeMenuItemLvlTwo(parent, SWT.NONE, style);
		}
			
	}
	
	/**
	 * Group widget is represented as two composites:
	 * <ul>
	 * <li> a composite which is placed at the START of the group. It contains the name of the group.
	 * <li> a composite which is placed at the END of the group. It is a dummy with some space.
	 * </ul>
	 * @param menuItemGroup
	 * @param parent
	 * @param lvl
	 * @return - the group Start widget 
	 */
	private AbstractCompositeMenuGroup createCompositeGroupHead(MenuItemGroup menuItemGroup, Composite parent, int lvl){
		if(lvl<=1){
			return null;
		}else{
			return new CompositeMenuGroupLevelTwo(parent, SWT.NONE).setGroupName(menuItemGroup.text);	
		}
	}
	
	/**
	 * Group widget is represented as two composites:
	 * <ul>
	 * <li> a composite which is placed at the START of the group. It contains the name of the group.
	 * <li> a composite which is placed at the END of the group. It is a dummy with some space.
	 * </ul>
	 * @param menuItemGroup
	 * @param parent
	 * @param lvl
	 * @return - the group End widget 
	 */
	private Composite createCompositeGroupBottom(MenuItemGroup menuItemGroup, Composite parent, int lvl){
		// no group closing composites yet
		return null;
	}
	
	
	/**
	 * Retrieves the color which should be applied to the given menuItem.
	 * The strategy is teh following:
	 * <ul>  
	 * <li> The existing color as previously set using {@link MenuItem#setColor} are respected
	 * <li> The colors are only applied to the lvl1 items, as given by {@link Style#getMenuItemColor(int)} and passed on by lvl1 items recursively to it's subpages and subitems.
	 * </ul>
	 * @param item
	 * @param itemLvl - the hierarchy level, of the given menuitem
	 * @param menuItemCnt - the order, in which this item occured within the menu
	 * @return
	 */
	public Color getColor(Style style, MenuItem item, int itemLvl, int menuItemCnt){
		if(item.getColor() != null){
			return item.getColor();
		}
		if(itemLvl==1){
			return style.getMenuItemColor(menuItemCnt);	
		}
		return null;
	}
	
}
