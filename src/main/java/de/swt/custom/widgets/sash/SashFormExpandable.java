package de.swt.custom.widgets.sash;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class SashFormExpandable extends Composite {
	
	private static final int HEIGHT_SASH = 15;

	private SashForm sashForm;
	private Composite compositeTop;
	private Composite compositeBottomContainer;
	private Composite compositeBottom;
	
	// there are two Sashes (belts). One of them is always hidden, depending on expansion state
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
		
		// create the usual SWT SashForm
		sashForm = new SashForm(this,  SWT.VERTICAL);
		
		// TOP content container 
		compositeTop = new Composite(sashForm, SWT.NONE);
		
		// bottom content container AND middle sash container		
		compositeBottomContainer = new Composite(sashForm, SWT.NONE);

			// MIDDLE Sash
			sashMiddle = new Sash(compositeBottomContainer, SWT.NONE);
		
			// BOTTOM content container
			compositeBottom = new Composite(compositeBottomContainer, SWT.NONE);
		
		// BOTTOM Sash container
		sashBottom = new Sash(this, SWT.NONE);
		
		// LAYOUT
		compositeBottomContainer.setLayout(new FormLayout());
		
		// layout sashForm to be over the sash (belt) at the bottom
		FormData fd_sashForm = new FormData();
		fd_sashForm.right = new FormAttachment(100);
		fd_sashForm.top = new FormAttachment(0, 0);
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.bottom = new FormAttachment(sashBottom, 0);
		sashForm.setLayoutData(fd_sashForm);
		
		// layout, in which  the BOTTOM SASH is VISIBLE. Used when TOP container of the sashForm is maximized
		formDataSashBottomVisible = new FormData();
		formDataSashBottomVisible.right = new FormAttachment(100);
		formDataSashBottomVisible.top = new FormAttachment(100,-HEIGHT_SASH);
		formDataSashBottomVisible.left = new FormAttachment(0, 0);
		formDataSashBottomVisible.bottom = new FormAttachment(100);
		sashBottom.setLayoutData(formDataSashBottomVisible);
		
		// layout, in which  the BOTTOM SASH is HIDDEN. Used when BOTTOM container of the sashForm is maximized
		// in this case the middle Sash is already visible
		formDataSashBottomHidden = new FormData();
		formDataSashBottomHidden.bottom = new FormAttachment(100, 0);
		formDataSashBottomHidden.top = new FormAttachment(100, 0);
		
		// layout sash top
		FormData fd_sashTop = new FormData();
		fd_sashTop.right = new FormAttachment(100);
		fd_sashTop.top = new FormAttachment(0,0);
		fd_sashTop.left = new FormAttachment(0, 0);
		fd_sashTop.bottom = new FormAttachment(0,HEIGHT_SASH);
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
		
		// initial state
		disableMaximizationOfShellContainers();
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
					disableMaximizationOfShellContainers();
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
					disableMaximizationOfShellContainers();
				}
			}
		});
	}
	
	// Functional helpers
	public void disableMaximizationOfShellContainers(){
		// bottom
		sashBottom.setVisible(false);
		sashBottom.setLayoutData(formDataSashBottomHidden);
		// mid		
		sashMiddle.setVisible(true);
		
		// disable fullscreen
		sashForm.setMaximizedControl(null);
		
		// layout
		sashBottom.getParent().layout(true); 
	}
	
	public void maximizeTop(){
		// bottom
		sashBottom.setVisible(true);
		sashBottom.setLayoutData(formDataSashBottomVisible);

		// hide the middle sash
		sashMiddle.setVisible(false);

		// maximize the top
		sashForm.setMaximizedControl(compositeTop);
		
		// have to update the layout of the parent, which contains sashForm and bottom Sash (Belt), 
		// so that it redistributes space among sash and sashForm
		sashBottom.getParent().layout(true, true);
	}
	
	public void maximizeBottom(){
		// bottom
		sashBottom.setVisible(false);
		sashBottom.setLayoutData(formDataSashBottomHidden);
		
		// mid
		sashMiddle.setVisible(true);
		
		// maximize bottom
		sashForm.setMaximizedControl(compositeBottomContainer);

		// have to update the layout of the parent, which contains sashForm and bottom Sash (Belt), 
		// so that it redistributes space among sash and sashForm
		sashBottom.getParent().layout(true, true);
	}
}
