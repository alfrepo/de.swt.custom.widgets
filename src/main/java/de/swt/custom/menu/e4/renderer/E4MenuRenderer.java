package de.swt.custom.menu.e4.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.MenuElement;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.Assert;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDynamicMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.impl.DynamicMenuContributionImpl;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.UIEvents.MenuContribution;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.e4.ui.workbench.swt.util.ISWTResourceUtilities;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

@SuppressWarnings("restriction")
public class E4MenuRenderer {
	
	@Inject
	ECommandService	commandService;
	
	@Inject 
	EHandlerService handlerService;

	@Inject 
	IContributionFactory contributionFactory;
	
	@Inject
	MApplication app;
	
	@Inject
	EModelService modelService;
	
	@Inject 
	IEclipseContext eclipseContext;
	
	ISWTResourceUtilities resUtils;
	
	@PostConstruct
	public void init(){
		resUtils = (ISWTResourceUtilities) eclipseContext.get(IResourceUtilities.class.getName());
	}

	/**
	 * Given a {@link MMenu} searches the {@link MenuContribution} for contributions to this menu.
	 * @param mmenu - the menu which's id to search
	 */
	public MMenu mergeMenuFromContributions(MMenu mmenu){
		// assemble the menu, consider the Menu-Contributions with the same parent-Id
		MenuManagerRenderer mmr = getMenuManagerRenderer();
		mmr.processContributions(mmenu, mmenu.getElementId(), false, false);
		return mmenu;
	}

	/**
	 * Same as the {@link #renderMenu(Menu, MMenuElement)} but skips the first menu object.
	 * Usable to flattern a menu object 
	 * 
	 * @param parentMenu - the parent menu
	 * @param menuElement - the element
	 */
	public void renderMenuSkipFirstLevel(Menu parentMenu, MMenu menuElement){
		for(MMenuElement childMenuElement : menuElement.getChildren()){
			renderMenu(parentMenu, childMenuElement);
		}
	}
	
	/**
	 * Method which iterates all {@link MenuContribution} children and renders them
	 * @param parentMenu - the menu which should be used as the parent for rendered {@link MenuItem} objects
	 * @param mMenuContribution - the element from the e4 model which contains menu items
	 */
	public void renderMenu(Menu parentMenu, MMenuContribution mMenuContribution){
		for(MMenuElement menuElement : mMenuContribution.getChildren()){
			renderMenu(parentMenu, menuElement);
		}
	}
	
	/**
	 * Renders the given {@link MenuElement} as a {@link Menu} and {@link MenuItem} structure.
	 * 
	 * @param parentMenu - the menu which should be used as the parent for rendered {@link MenuItem} objects 
	 * @param menuElement - the element from the e4 model which represents a menuItem
	 */
	public void renderMenu(Menu parentMenu, MMenuElement menuElement){
		// not null
		Assert.isNotNull(parentMenu);
		
		if(menuElement instanceof MHandledItem){
			MHandledItem item = (MHandledItem) menuElement;
			MCommand mcommand = item.getCommand();
			String commandId = mcommand.getElementId();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("de.mine.e4.styleinspector.commandparameter.teenantId", "einz");
			params.put("de.mine.e4.styleinspector.commandparameter.moduleid", "einz");
			
			final ParameterizedCommand paramCommand =  commandService.createCommand(commandId, params);
			
			// swt
			createMenu(parentMenu, item.getLabel(), getIcon(item.getIconURI()), createSelectionListener(paramCommand));
			
		}else if(menuElement instanceof MMenuSeparator){
			
			// swt
			new MenuItem(parentMenu, SWT.SEPARATOR);
			
			
		}else if(menuElement instanceof MDirectMenuItem){
			MDirectMenuItem item = (MDirectMenuItem) menuElement;
			String contributionUri = item.getContributionURI();
			final Object handler = contributionFactory.create(contributionUri,eclipseContext);
			
			// swt
			createMenu(parentMenu, item.getLabel(), getIcon(item.getIconURI()), createSelectionListener(handler));
			
			
		}else if(menuElement instanceof MDynamicMenuContribution){
			DynamicMenuContributionImpl item = (DynamicMenuContributionImpl) menuElement;
			String contributionUri = item.getContributionURI();
			final Object handler = contributionFactory.create(contributionUri,eclipseContext);
			
			// swt
			createMenu(parentMenu, item.getLabel(), getIcon(item.getIconURI()), createSelectionListener(handler));
			
			
		}else if(menuElement instanceof MMenu){
			MMenu menu = (MMenu) menuElement;
			
			// swt			
			MenuItem mnItemSubmenu = new MenuItem(parentMenu, SWT.CASCADE);
			mnItemSubmenu.setText(menu.getLabel());
			mnItemSubmenu.setImage(getIcon(menu.getIconURI()));
			
			Menu swtSubMenu = new Menu(mnItemSubmenu);
			mnItemSubmenu.setMenu(swtSubMenu);
					
			for(MMenuElement subElement : ((MMenu) menuElement).getChildren()){
				renderMenu(swtSubMenu, subElement);
			}
		}
	}

	
	// HELPER
	
	private SelectionAdapter createSelectionListener(final ParameterizedCommand paramCommand){
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlerService.executeHandler(paramCommand, eclipseContext);
			}
		};
	}
	
	private SelectionAdapter createSelectionListener(final Object handler){
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContextInjectionFactory.invoke(handler, Execute.class, eclipseContext);
			}
		};
	}
	
	private Image getIcon(String iconUri){
		if(iconUri == null){
			return null;
		}
		ImageDescriptor imageDescriptor =  resUtils.imageDescriptorFromURI(org.eclipse.emf.common.util.URI.createURI(iconUri) );
		return imageDescriptor.createImage();
	}
	
	private MenuItem createMenu(Menu parentMenu, String label, Image image,  SelectionListener selectionListener ){
		MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
		menuItem.setText(label);
		menuItem.setImage(image);
		menuItem.addSelectionListener(selectionListener);
		return menuItem;
	}

	/**
	 * Creates a new MenuManagerRender, which injects context data into it
	 * @return - the MenuManagerRenderer
	 */
	private MenuManagerRenderer getMenuManagerRenderer(){
		MenuManagerRenderer mmr = new MenuManagerRenderer();
		mmr.init(eclipseContext);
		ContextInjectionFactory.inject(mmr, eclipseContext);
		return mmr;
	}
}
