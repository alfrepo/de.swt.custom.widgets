package de.swt.custom.widgets.sash;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class SashFormExpandable extends Composite {

	private SashForm sashForm;
	private Composite compositeTop;
	private Composite compositeBottomContainer;
	private Composite compositeBottom;
	
	private Sash sashMiddle;
	private Sash sashBottom;
	
	private FormData formDataSashBottomVisible;
	private FormData formDataSashBottomHidden;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SashFormExpandable(Composite parent, int style) {
		super(parent, style);
		createContents(parent);
		sashForm.SASH_WIDTH=0;
	}

	protected void createContents(Composite parent){
		setLayout(new FormLayout());
		
		sashForm = new SashForm(this,  SWT.VERTICAL);
		compositeTop = new Composite(sashForm, SWT.NONE);
		
		compositeBottomContainer = new Composite(sashForm, SWT.NONE);
		sashMiddle = new Sash(compositeBottomContainer, SWT.NONE);
		compositeBottom = new Composite(compositeBottomContainer, SWT.NONE);
		
		sashBottom = new Sash(this, SWT.NONE);
		
		//LAYOUT
		compositeBottomContainer.setLayout(new FormLayout());
		
		// layout sash above sash
		FormData fd_sashForm = new FormData();
		fd_sashForm.right = new FormAttachment(100);
		fd_sashForm.top = new FormAttachment(0, 0);
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.bottom = new FormAttachment(sashBottom, 0);
		sashForm.setLayoutData(fd_sashForm);
		
		//layout sash bottom
		formDataSashBottomVisible = new FormData();
		formDataSashBottomVisible.right = new FormAttachment(100);
		formDataSashBottomVisible.top = new FormAttachment(100,-15);
		formDataSashBottomVisible.left = new FormAttachment(0, 0);
		formDataSashBottomVisible.bottom = new FormAttachment(100);
		sashBottom.setLayoutData(formDataSashBottomVisible);
		// this layout is used, when the bottom sash is visible
		formDataSashBottomHidden = new FormData();
		formDataSashBottomHidden.bottom = new FormAttachment(100, 0);
		formDataSashBottomHidden.top = new FormAttachment(100, 0);
		
		//layout sash top
		FormData fd_sashTop = new FormData();
		fd_sashTop.right = new FormAttachment(100);
		fd_sashTop.top = new FormAttachment(0,0);
		fd_sashTop.left = new FormAttachment(0, 0);
		fd_sashTop.bottom = new FormAttachment(0,15);
		sashMiddle.setLayoutData(fd_sashTop);
		
		// layout composite bottom
		FormData fd_compositeBottom = new FormData();
		fd_compositeBottom.right = new FormAttachment(100);
		fd_compositeBottom.top = new FormAttachment(sashMiddle, 0);
		fd_compositeBottom.left = new FormAttachment(0, 0);
		fd_compositeBottom.bottom = new FormAttachment(100, 0);
		compositeBottom.setLayoutData(fd_compositeBottom);
		
		// OTHERS
		addDecor(sashMiddle, sashBottom);
		addFunctionalityToSashTokens(sashMiddle, sashBottom);
		
		//initial state
		expandBoth();
	}
	
	private void addDecor(Sash middle, Sash bottom){
		middle.getSashTokenLeft().setBg(SashToken.IMAGE_ARROWDOWN);
		middle.getSashTokenRight().setBg(SashToken.IMAGE_ARROWUP);
		
		bottom.getSashTokenLeft().setBg(SashToken.IMAGE_ARROWDOWN);
		bottom.getSashTokenRight().setBg(SashToken.IMAGE_ARROWUP);
	}
	
	private void addFunctionalityToSashTokens(Sash middle, Sash bottom){

		// make the sash dragging redistribute the sashForm weight 
		sashMiddle.resizeOnDrag(sashForm, this);
		sashBottom.resizeOnDrag(sashForm, this);
		
		// TOP
		// 	arrow-down expands both OR collapses the top
		middle.getSashTokenLeft().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(sashForm.getMaximizedControl() == null){
					// if both are expanded - maximize top
					maximizeTop();
				}else if(compositeBottomContainer.equals(sashForm.getMaximizedControl())){
					// if top is maximized - expand both
					expandBoth();
				}
			}
		});
		
		//  arrow-up
		middle.getSashTokenRight().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(sashForm.getMaximizedControl() == null){
					// if both are expanded - maximize bottom
					maximizeBottom();
				}
			}
		});
		
		// BOTTOM
		bottom.getSashTokenRight().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(compositeTop.equals(sashForm.getMaximizedControl())){
					// if top is expanded - expand both
					expandBoth();
				}
			}
		});
	}
	
	// Functional helpers
	public void expandBoth(){
		sashBottom.setVisible(false);
		sashBottom.setLayoutData(formDataSashBottomHidden);
		
		sashMiddle.setVisible(true);
		sashForm.setMaximizedControl(null);
		
		sashForm.layout(true); 
	}
	
	public void maximizeTop(){
		sashBottom.setVisible(true);
		sashBottom.setLayoutData(formDataSashBottomVisible);
		
		sashMiddle.setVisible(false);
		sashForm.setMaximizedControl(compositeTop);
		
		sashForm.layout(true);
	}
	
	public void maximizeBottom(){
		sashBottom.setVisible(false);
		sashBottom.setLayoutData(formDataSashBottomHidden);
		
		sashMiddle.setVisible(true);
		sashForm.setMaximizedControl(compositeBottomContainer);

		sashForm.layout(true);
	}
}
