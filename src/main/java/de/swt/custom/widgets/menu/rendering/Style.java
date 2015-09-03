package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import de.swt.custom.utils.UtilsUI;

public class Style {
	static Style singleton;
	
	private final boolean MENU_PERSISTS_SELECTION = true; //px
	
	private final int MENUITEM_ICON_SIZE_LVL1 = 40; //px

	private final int MENUITEM_ICON_SIZE_LVL2 = 16;
	
	private final int MENUITEM_HEIGHT_LVL1 = 40; //px
	private final int MENUITEM_HEIGHT_LVL2 = 16;

	private final int MENU_POPUP_WIDTH = 200;
	
	// Backgorund color of pages and items
	private final Color MENU_PAGE_COLOR_LVL1 = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private final Color MENU_PAGE_COLOR_LVL2 = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	private final int MENU_PAGE_LEFTSTRIPE_WIDTH_LVL2 = 5;
	
	private final Color MENU_PAGE_LEFTSTRIPE_COLOR_LVL2 =  new Color(Display.getCurrent(), 204, 255, 204);
	
	private final Cursor CURSOR_HAND = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
	
	private final Font MENUITEM_FONT_LVL1 = SWTResourceManager.getFont("Arial", 15, SWT.NORMAL); // px
	private final Font MENUITEM_FONT_LVL2 = SWTResourceManager.getFont("Arial", 9, SWT.NORMAL); // px

	private final int MENUITEM_HIGHLIGHTING_DURATION_MS = 300;
	
	/**
	 * Every Menu item on level 1 owns a color.
	 * This color is used for mouse-over effects on items, for subpage decoration etc.
	 * These colors will be assigned to items on level 1 in the given order. 
	 */
	private final Color[] MENUITEM_COLORS_ORDERED = new Color[]{
		UtilsUI.hex2Color(0xfdee8d),
		UtilsUI.hex2Color(0xe3ea75),
		UtilsUI.hex2Color(0xbaf379),
		UtilsUI.hex2Color(0x75eae7),
		UtilsUI.hex2Color(0x7acdf3),
		UtilsUI.hex2Color(0x78b8ef),
		UtilsUI.hex2Color(0x7784ea),
		UtilsUI.hex2Color(0xf37ac5),
		UtilsUI.hex2Color(0xf37a9d)
	};
	
	public void dispose(){
		CURSOR_HAND.dispose();
		MENUITEM_FONT_LVL1.dispose();
		MENUITEM_FONT_LVL2.dispose();
		MENU_PAGE_COLOR_LVL1.dispose();
		MENU_PAGE_COLOR_LVL2.dispose();
		MENU_PAGE_LEFTSTRIPE_COLOR_LVL2.dispose();
		for(Color c: MENUITEM_COLORS_ORDERED){
			c.dispose();
		}
	}
	
	public int getMENUITEM_HIGHLIGHTING_DURATION_MS() {
		return MENUITEM_HIGHLIGHTING_DURATION_MS;
	}

	public int getMENU_PAGE_LEFTSTRIPE_WIDTH_LVL2() {
		return MENU_PAGE_LEFTSTRIPE_WIDTH_LVL2;
	}


	public Color getMENU_PAGE_LEFTSTRIPE_COLOR_LVL2() {
		return MENU_PAGE_LEFTSTRIPE_COLOR_LVL2;
	}


	public static Style getSingleton() {
		return singleton;
	}

	public Color getMENU_PAGE_COLOR_LVL1() {
		return MENU_PAGE_COLOR_LVL1;
	}


	public Color getMENU_PAGE_COLOR_LVL2() {
		return MENU_PAGE_COLOR_LVL2;
	}

	public int getMENUITEM_ICON_SIZE_LVL1() {
		return MENUITEM_ICON_SIZE_LVL1;
	}

	public int getMENUITEM_ICON_SIZE_LVL2() {
		return MENUITEM_ICON_SIZE_LVL2;
	}

	public int getMENUITEM_HEIGHT_LVL1() {
		return MENUITEM_HEIGHT_LVL1;
	}

	public int getMENUITEM_HEIGHT_LVL2() {
		return MENUITEM_HEIGHT_LVL2;
	}

	public Cursor getCURSOR_HAND() {
		return CURSOR_HAND;
	}

	public Font getMENUITEM_FONT_LVL1() {
		return MENUITEM_FONT_LVL1;
	}

	public Font getMENUITEM_FONT_LVL2() {
		return MENUITEM_FONT_LVL2;
	}

	public int getMENU_POPUP_WIDTH() {
		return MENU_POPUP_WIDTH;
	}

	public Color getMenuItemColor(int menuItemCount){
		return MENUITEM_COLORS_ORDERED[menuItemCount % MENUITEM_COLORS_ORDERED.length];
	}
	
	boolean getMENU_PERSISTS_SELECTION(){
		return MENU_PERSISTS_SELECTION;
	}
	
	public static void setSingleton(Style singleton) {
		Style.singleton = singleton;
	}
	
	

	public static final Style get() {
		if (singleton == null){
			singleton = new Style();
		}
		return singleton;
	}


}
