package de.swt.custom.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import de.swt.custom.widgets.jface.wizards.FactoryWidgets;
import de.swt.custom.widgets.jface.wizards.Style;

/**
 * Abstract widget which can display a warning and info icons.
 * In its core another widget should be used:
 * <ul>
 * <li> A TestWidget
 * <li> A DateWidget
 * <li> ...
 * </ul>
 * To implement - override the method {@link #createContent(Composite)}
 * @author alf
 *
 */
public abstract class WidgetValidatedLabeled extends Composite {

	private Label lblDescribtion;
	private Label lblWarningIcon;
	private Label lblDetailsIcon;
	private Label lblWarningText;

	/**
	 * Widget with text 
	 * 
	 * @param parent - the SWT parent
	 * @param style - SWT style
	 * @param describtion - Set the describiton of the Textfield
	 */
	public WidgetValidatedLabeled(Composite parent, int style, String describtion) {
		this(parent, style);
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WidgetValidatedLabeled(Composite parent, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginBottom = Style.WIDGET_VALIDATED_LABLED_TEXT_MARGIN_BOTTOM_IN_GROUP;
		setLayout(formLayout);
		
		lblDescribtion = FactoryWidgets.getLabel(this, SWT.NONE);
		FormData fd_lblDescribtion = new FormData();
		fd_lblDescribtion.top = new FormAttachment(0);
		fd_lblDescribtion.left = new FormAttachment(0);
		lblDescribtion.setLayoutData(fd_lblDescribtion);
		lblDescribtion.setText("Describtion");
			
		lblWarningIcon = FactoryWidgets.getLabel(this, SWT.NONE);
		FormData fd_lblWarningIcon = new FormData();
		fd_lblWarningIcon.width = Style.WIDGET_VALIDATED_LABLED_TEXT_TEXTFIELD_HEIGHT;
		fd_lblWarningIcon.top = new FormAttachment(lblDescribtion);
		fd_lblWarningIcon.right = new FormAttachment(100);
		lblWarningIcon.setImage(Style.WIDGET_VALIDATED_LABLED_TEXT_IMAGE_WARNING);
		lblWarningIcon.setLayoutData(fd_lblWarningIcon);
		
		lblDetailsIcon = FactoryWidgets.getLabel(this, SWT.NONE);
		lblDetailsIcon.setImage(Style.WIDGET_VALIDATED_LABLED_TEXT_IMAGE_INFO);
		FormData fd_lblDetailsIcon = new FormData();
		fd_lblDetailsIcon.right = new FormAttachment(lblWarningIcon, -Style.WIDGET_VALIDATED_LABLED_TEXT_ICON_MARGIN);
		fd_lblDetailsIcon.width = Style.WIDGET_VALIDATED_LABLED_TEXT_TEXTFIELD_HEIGHT;
		fd_lblDetailsIcon.top = new FormAttachment(lblDescribtion);
		lblDetailsIcon.setLayoutData(fd_lblDetailsIcon);
		
		Composite textComposite = new Composite(this, SWT.NONE);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblDetailsIcon, -Style.WIDGET_VALIDATED_LABLED_TEXT_ICON_MARGIN);
		fd_text.height = Style.WIDGET_VALIDATED_LABLED_TEXT_TEXTFIELD_HEIGHT;
		fd_text.top = new FormAttachment(lblDescribtion);
		fd_text.left = new FormAttachment(0);
		textComposite.setLayoutData(fd_text);
		// can hook in here to create some widgets
		createContent(textComposite);
		
		lblWarningText = FactoryWidgets.getLabel(this, SWT.NONE);
		lblWarningText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		FormData fd_lblWarningText = new FormData();
		fd_lblWarningText.top = new FormAttachment(textComposite);
		fd_lblWarningText.left = new FormAttachment(0);
		lblWarningText.setLayoutData(fd_lblWarningText);
		lblWarningText.setText("Warning");
		
		
		initValues();
		layout();
	}
	

	public Label getLblDescribtion() {
		return lblDescribtion;
	}

	public Label getLblWarningIcon() {
		return lblWarningIcon;
	}

	public Label getLblDetailsIcon() {
		return lblDetailsIcon;
	}

	public Label getLblWarningText() {
		return lblWarningText;
	}
	
	public void setLblWarningIconVisible(boolean isVisible){
		makeBeGoneInGridLayout(lblWarningIcon, false, !isVisible);
	}
	
	public void setLblWarningToolTipText(String toolTipText){
		lblWarningIcon.setToolTipText(toolTipText);
	}
	
	public void setLblDescribtionIconVisible(boolean isVisible){
		makeBeGoneInGridLayout(lblDetailsIcon, false, !isVisible);
	}
	
	public void setLblDescribtionToolTipText(String toolTipText){
		lblDetailsIcon.setToolTipText(toolTipText);
	}
	
	public void setWarning(String warning) {
		setTextHideIfEmpty(this.lblWarningText, warning, true, false); 
		showLabelHideIfEmpty(lblWarningIcon, warning, false, true);
	}
	
	public void setDescribtionText(String describiton){
		setTextHideIfEmpty(this.lblDescribtion, describiton, true, false);
	}
	
	/**
	 * Creates the text input widget or widgets
	 * @return
	 */
	protected abstract void createContent(Composite textFieldParent);
	
	private void initValues(){
		setWarning(null);
		setLblDescribtionIconVisible(false);
		setDescribtionText(null);
	}
	
	private void showLabelHideIfEmpty(Label iconLabel, String text, boolean reduceWidth, boolean reduceHeight){
		if(isEmpty(text)){
			// hide icon
			makeBeGoneInGridLayout(iconLabel, reduceWidth, reduceHeight);
		}else{
			// show icon
			makeBeGoneInGridLayout(iconLabel, false, false);			
		}
	}
	
	private void setTextHideIfEmpty(Label label, String text, boolean reduceWidth, boolean reduceHeight){
		if(isEmpty(text)){
			makeBeGoneInGridLayout(label, reduceWidth, reduceHeight);
		}else{
			label.setText(text);	
			makeBeGoneInGridLayout(label, false, false);
		}
	}
	
	private void makeBeGoneInGridLayout(Control control, boolean reduceWidth, boolean reduceHeight){
			// hide?
			if(reduceWidth==true || reduceHeight==true){
				control.setVisible(false);
			}else{
				control.setVisible(true);
			}
			
			// width
			FormData layoutData = (FormData) control.getLayoutData();
			if(reduceWidth){
				layoutData.width = 0;
			}else{
				layoutData.width = SWT.DEFAULT;
			}
			// height
			if(reduceHeight){
				layoutData.height = 0;
			}else{
				layoutData.height = SWT.DEFAULT;
			}
			// save
			control.setLayoutData(layoutData);
	}
	
	private boolean isEmpty(String text){
		return (text==null || text.isEmpty());
	}

}
