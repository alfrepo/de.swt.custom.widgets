package de.swt.custom.widgets.menu.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CompositeMenuPageLevelTwo extends AbstractCompositeMenuPage {

	
	private Composite compositeStripeLeft;
	private Composite compositeClientArea;
	
	public int MENU_ITEM_LVL_TWO_DEFAULT_HEIGHT; //px
	public int MENUPAGE_LVL_TWO_LEFTSTRIPE_WIDTH; //px
	public Color MENUPAGE_LVL_TWO_LEFTSTRIPE_COLOR;
	
	public CompositeMenuPageLevelTwo(Composite parent, int styleBits) {
		this(parent, styleBits, Style.get());
	}
	
	@Override
	protected void initStyle(Style style) {
		super.initStyle(style);
		this.MENU_PAGE_COLOR = style.getMENU_PAGE_COLOR_LVL2();
		MENU_ITEM_LVL_TWO_DEFAULT_HEIGHT = style.getMENUITEM_HEIGHT_LVL2();
		MENUPAGE_LVL_TWO_LEFTSTRIPE_WIDTH = style.getMENU_PAGE_LEFTSTRIPE_WIDTH_LVL2();
		MENUPAGE_LVL_TWO_LEFTSTRIPE_COLOR = style.getMENU_PAGE_LEFTSTRIPE_COLOR_LVL2();
	}
	
	public CompositeMenuPageLevelTwo(Composite parent, int styleBits, Style style) {
		super(parent, styleBits, style);

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		compositeStripeLeft = new Composite(this, SWT.NONE);
		compositeStripeLeft.setBackground(MENUPAGE_LVL_TWO_LEFTSTRIPE_COLOR);
		GridData gd_compositeStripeLeft = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_compositeStripeLeft.widthHint = MENUPAGE_LVL_TWO_LEFTSTRIPE_WIDTH;
		compositeStripeLeft.setLayoutData(gd_compositeStripeLeft);
		
		// will be used in getClientAreaComposite() to create children in it
		compositeClientArea = new Composite(this, SWT.NONE);
		compositeClientArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		// layout client area
		GridLayout gl_compositeClientArea = new GridLayout(1, false);
		gl_compositeClientArea.marginBottom = 5;
		gl_compositeClientArea.marginTop = 5;
		gl_compositeClientArea.horizontalSpacing = 0;
		gl_compositeClientArea.marginWidth = 0;
		gl_compositeClientArea.verticalSpacing = 0;
		gl_compositeClientArea.marginHeight = 0;
		getClientAreaComposite().setLayout(gl_compositeClientArea);
	}
	
	@Override
	public Composite getClientAreaComposite() {
		return compositeClientArea;
	}
	
	public void layoutChild(Control child){
		this.layoutChild(child, MENU_ITEM_LVL_TWO_DEFAULT_HEIGHT);
	}
	
	public void layoutChild(Control child, int height){
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.heightHint = height;
		gridData.minimumHeight = height;
		child.setLayoutData(gridData);
	}
	
	public void setLeftStripeColor(Color color){
		this.compositeStripeLeft.setBackground(color);
	}
}
