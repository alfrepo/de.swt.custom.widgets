package de.swt.custom.widgets.jface.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class WidgetValidatedLabeledText extends Composite {

	private Text text;
	private Label lblDescribtion;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WidgetValidatedLabeledText(Composite parent, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginBottom = Style.WIDGET_VALIDATED_LABLED_TEXT_MARGIN_BOTTOM_IN_GROUP;
		setLayout(formLayout);
		
		Label lblDescribtion = new Label(this, SWT.NONE);
		FormData fd_lblDescribtion = new FormData();
		fd_lblDescribtion.top = new FormAttachment(0);
		fd_lblDescribtion.left = new FormAttachment(0);
		lblDescribtion.setLayoutData(fd_lblDescribtion);
		lblDescribtion.setText("Describtion");
			
		Label lblWarningIcon = new Label(this, SWT.NONE);
		FormData fd_lblWarningIcon = new FormData();
		fd_lblWarningIcon.width = Style.WIDGET_VALIDATED_LABLED_TEXT_ICON_WIDTH;
		fd_lblWarningIcon.top = new FormAttachment(lblDescribtion);
		fd_lblWarningIcon.right = new FormAttachment(100);
		lblWarningIcon.setImage(Style.WIDGET_VALIDATED_LABLED_TEXT_IMAGE_WARNING);
		lblWarningIcon.setLayoutData(fd_lblWarningIcon);
		
		Label lblDetailsIcon = new Label(this, SWT.NONE);
		lblDetailsIcon.setImage(Style.WIDGET_VALIDATED_LABLED_TEXT_IMAGE_INFO);
		FormData fd_lblDetailsIcon = new FormData();
		fd_lblDetailsIcon.width = Style.WIDGET_VALIDATED_LABLED_TEXT_ICON_WIDTH;
		fd_lblDetailsIcon.top = new FormAttachment(lblDescribtion);
		fd_lblDetailsIcon.right = new FormAttachment(lblWarningIcon);
		lblDetailsIcon.setLayoutData(fd_lblDetailsIcon);
		
		text = new Text(this, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.height = Style.WIDGET_VALIDATED_LABLED_TEXT_TEXTFIELD_HEIGHT;
		fd_text.top = new FormAttachment(lblDescribtion);
		fd_text.left = new FormAttachment(0);
		fd_text.right = new FormAttachment(lblDetailsIcon);
		text.setLayoutData(fd_text);
		
		
		Label lblWarningText = new Label(this, SWT.NONE);
		lblWarningText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		FormData fd_lblWarningText = new FormData();
		fd_lblWarningText.top = new FormAttachment(text);
		fd_lblWarningText.left = new FormAttachment(0);
		lblWarningText.setLayoutData(fd_lblWarningText);
		lblWarningText.setText("Warning");
		

	}

}
