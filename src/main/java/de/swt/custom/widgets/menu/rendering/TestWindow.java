package de.swt.custom.widgets.menu.rendering;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import de.swt.custom.widgets.menu.Menu;
import de.swt.custom.widgets.menu.MenuItem;
import de.swt.custom.widgets.menu.MenuItemClickable;
import de.swt.custom.widgets.menu.MenuPage;
import de.swt.custom.widgets.menu.OnMenuItemClickListener;

public class TestWindow {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestWindow window = new TestWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = generateMenu();
		addListeners(menu, shell);
		
		// at least render it
		menu.renderMenu(shell);
	}

	
	public Menu generateMenu(){
		Menu menu = new Menu();
		
		MenuPage mainPage = getMainPage(menu);
		MenuPage buchhaltungPage = getGeraetePage(menu);
		MenuPage geraetePage = getGeraetePage(menu);
		MenuPage verkaufsPage = getGeraetePage(menu);
		mainPage.findMenuItem("buchhaltung", MenuItemClickable.class).setSubPage(buchhaltungPage);
		mainPage.findMenuItem("geraete", MenuItemClickable.class).setSubPage(geraetePage);
//		mainPage.findMenuItem("einnahmekontrolle", MenuItemClickable.class).setSubPage(geraetePage);
		mainPage.findMenuItem("verkaufsdaten", MenuItemClickable.class).setSubPage(verkaufsPage);
		
		MenuPage subPage11 = getSubPage1(menu);
		MenuPage subPage12 = getSubPage1(menu);
		MenuPage subPage13 = getSubPage1(menu);
		MenuPage subPage14 = getSubPage1(menu);
		mainPage.findMenuItem("verkaufsgeraete", MenuItemClickable.class).setSubPage(subPage11);
		mainPage.findMenuItem("nutzermedien", MenuItemClickable.class).setSubPage(subPage12);
		mainPage.findMenuItem("sams", MenuItemClickable.class).setSubPage(subPage13);
		mainPage.findMenuItem("hascontextmenu", MenuItemClickable.class).setSubPage(subPage14);
		
		MenuPage subPage21 = getSubPage2(menu);
		MenuPage subPage22 = getSubPage2(menu);
		MenuPage subPage23 = getSubPage2(menu);
		mainPage.findMenuItem("subitem2a", MenuItemClickable.class).setSubPage(subPage21);
		mainPage.findMenuItem("subitem2b", MenuItemClickable.class).setSubPage(subPage22);
		mainPage.findMenuItem("subitem2c", MenuItemClickable.class).setSubPage(subPage23);
		
		// fill menu object
		menu.setRenderer(new Renderer());
		menu.setMainPage(mainPage);
		
		return menu;
	}
	
	private void addListeners(Menu menu, final Shell shell){
		// subpage
		OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(MenuItem menuItem, int mouseButton, int x, int y) {
				// only handle left click
				if(mouseButton != OnMenuItemClickListener.MOUSE_BUTTON_LEFT){
					return;
				}
				
				MessageDialog messageDialog = new MessageDialog(shell,
						"WizardClosingDialog.title", //$NON-NLS-1$
						null,
						"WizardClosingDialog.message", //$NON-NLS-1$
						MessageDialog.QUESTION,
						new String[] { IDialogConstants.OK_LABEL }, 0);
				messageDialog.open();
				
			}
		};
		menu.findMenuItem("geraete", MenuItemClickable.class).addMenuItemClickListener(onMenuItemClickListener);
		menu.findMenuItem("buchhaltung", MenuItemClickable.class).addMenuItemClickListener(onMenuItemClickListener);
		menu.findMenuItem("nutzermedien", MenuItemClickable.class).addMenuItemClickListener(onMenuItemClickListener);
		menu.findMenuItem("subitem2a", MenuItemClickable.class).addMenuItemClickListener(onMenuItemClickListener);
		
		menu.findMenuItem("hascontextmenu", MenuItemClickable.class).addMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(MenuItem menuItem, int mouseButtonNumber, int x, int y) {
				Control controlMenuItem = (Control)menuItem.widget;
				Shell shellMenuSubPage = controlMenuItem.getShell();
						
				
				org.eclipse.swt.widgets.Menu menu = createContextMenu(shellMenuSubPage);
				Point absPosition = ((Control)menuItem.widget).toDisplay(x, y);
				menu.setLocation(absPosition.x, absPosition.y);
				menu.setVisible(true);
				
				
				// TODO: add ContextMenu to the FlowOutMenu's managed Shells, which are used to 
				List<Widget> managedWidgets = new ArrayList<Widget>();
				managedWidgets.add(menu);
				shellMenuSubPage.setData(Renderer.MANAGED_SHELLS_LIST, managedWidgets);
			}
		});
	}
	
	
	// testMenu
	private static MenuPage getMainPage(Menu menu) {
		MenuPage menuPage = new MenuPage(menu);

		ImageDescriptor big = ImageDescriptor.createFromURL(TestWindow.class.getResource("evasion_40.png"));
		
		// sales
		MenuItem menuItem;
		menuItem = new MenuItemClickable(
				"buchhaltung", 	// id  
				big, 			// icon 
				"Buchhaltung", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);

		menuItem = new MenuItemClickable(
				"geraete", 		// id  
				big, 			// icon 
				"Geräte",	 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		
		menuItem = new MenuItemClickable(
				"einnahmekontrolle", 		// id  
				big, 						// icon 
				"Einnahmekontrolle",	 	// text 
				null, 						// menu
				null); 						// subpage
		menuPage.addItem(menuItem);
		
		menuItem = new MenuItemClickable(
				"verkaufsdaten", 	// id  
				null, 				// icon 
				"Verkaufsdaten",	// text 
				null, 				// menu
				null); 				// subpage
		menuPage.addItem(menuItem);
		
		return menuPage;
	}
	
	private MenuPage getGeraetePage(Menu menu){
		MenuPage menuPage = new MenuPage(menu);

		ImageDescriptor small = ImageDescriptor.createFromURL(TestWindow.class.getResource("evasion_16.png"));
		
		// sales
		MenuItem menuItem;
		menuItem = new MenuItemClickable(
				"sams",		 	// id  
				small, 			// icon 
				"SAMs", 		// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);

		menuItem = new MenuItemClickable(
				"nutzermedien", // id  
				small, 			// icon 
				"Nutzermedien",	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		
		menuItem = new MenuItemClickable(
				"verkaufsgeraete", 		// id  
				null, 					// icon 
				"Verkaufsgeräte",	 	// text 
				null, 					// menu
				null); 					// subpage
		menuPage.addItem(menuItem);
		
		menuItem = new MenuItemClickable(
				"hascontextmenu", 		// id  
				small, 				// icon 
				"hascontextmenu",	 		// text 
				null, 				// menu
				null); 				// subpage
		menuPage.addItem(menuItem);
		
		return menuPage;
	}
	
	private MenuPage getSubPage1(Menu menu){
		MenuPage menuPage = new MenuPage(menu);

		// subitem1
		MenuItem menuItem;
		menuItem = new MenuItemClickable(
				"subitem2a",		// id  
				null, 			// icon 
				"subitem2a", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		menuItem = new MenuItemClickable(
				"subitem2b",		// id  
				null, 			// icon 
				"subitem2b", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		menuItem = new MenuItemClickable(
				"subitem2c",		// id  
				null, 			// icon 
				"subitem2c", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		return menuPage;
	}
	
	private MenuPage getSubPage2(Menu menu){
		MenuPage menuPage = new MenuPage(menu);

		// subitem1
		MenuItem menuItem;
		menuItem = new MenuItemClickable(
				"subitem3a",		// id  
				null, 			// icon 
				"subitem3a", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		menuItem = new MenuItemClickable(
				"subitem3b",		// id  
				null, 			// icon 
				"subitem3b", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		menuItem = new MenuItemClickable(
				"subitem3c",		// id  
				null, 			// icon 
				"subitem3c", 	// text 
				null, 			// menu
				null); 			// subpage
		menuPage.addItem(menuItem);
		return menuPage;
	}
	
	
	
	
	private org.eclipse.swt.widgets.Menu createContextMenu(Shell shell){
		org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu(shell);

		org.eclipse.swt.widgets.MenuItem mntmItem = new org.eclipse.swt.widgets.MenuItem(menu, SWT.NONE);
		mntmItem.setText("Item1");
		mntmItem.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Item1");
			}
		});
		
		org.eclipse.swt.widgets.MenuItem mntmItem_1 = new org.eclipse.swt.widgets.MenuItem(menu, SWT.NONE);
		mntmItem_1.setText("Item 2");
		
		org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menu, SWT.SEPARATOR);
		menuItem.setText("Checks");
		
		org.eclipse.swt.widgets.MenuItem mntmCheckitem = new org.eclipse.swt.widgets.MenuItem(menu, SWT.CHECK);
		mntmCheckitem.setText("Checkitem 1");
		
		org.eclipse.swt.widgets.MenuItem mntmCheckitem_1 = new org.eclipse.swt.widgets.MenuItem(menu, SWT.CHECK);
		mntmCheckitem_1.setText("CheckItem2");
		
		org.eclipse.swt.widgets.MenuItem menuItem_1 = new org.eclipse.swt.widgets.MenuItem(menu, SWT.SEPARATOR);
		menuItem_1.setText("Radios");
		
		org.eclipse.swt.widgets.MenuItem mntmRadiobuttonitem = new org.eclipse.swt.widgets.MenuItem(menu, SWT.RADIO);
		mntmRadiobuttonitem.setText("RadioButtonItem");
		
		org.eclipse.swt.widgets.MenuItem menuItem_2 = new org.eclipse.swt.widgets.MenuItem(menu, SWT.SEPARATOR);
		menuItem_2.setText("Cascade");
		
		org.eclipse.swt.widgets.MenuItem mntmSubmenu = new org.eclipse.swt.widgets.MenuItem(menu, SWT.CASCADE);
		mntmSubmenu.setText("Submenu");
		
		return menu;
	}
}
