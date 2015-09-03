package de.swt.custom.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.swt.custom.widgets.jface.wizards.Style;

/**
 * Widget creates rows, which are layed out in a Grid. It differs from the Grid,
 * because it's rows fill the whole horizontal space.
 * 
 * <p>
 * It is useful to implement The methods {@link #createRow()} should be used to
 * get the parent for each row. The method {@link #createGroupClosingDummy()}
 * should be used to insert some space after a form, e.g. to separate a logical
 * group of forms.
 * </p>
 * 
 * @author alf
 *
 */
public class WidgetGrid extends Composite {
	public static final int height_group_closing_dummy = Style.WIDGET_GRID_GROUP_CLOSING_DUMMY_HEIGHT; // px

	public WidgetGrid(Composite parent, int style) {
		super(parent, style);
		layoutThis();
		this.setBackgroundMode(SWT.COLOR_MAGENTA);
	}

	private void layoutThis() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
	}

	public Composite createRow() {
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1);
		gd_composite.heightHint = Style.WIDGET_GRID_ROW_HEIGHT;
		composite.setLayoutData(gd_composite);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		layoutNewRow(composite);

		return composite;
	}

	/**
	 * Override to modify the default layout, which is used to initialize the
	 * new row composite
	 * 
	 * @param newRowComposite
	 *            - the new row composite
	 */
	protected void layoutNewRow(Composite newRowComposite) {
		newRowComposite.setLayout(new FillLayout());
	}

	public void createGroupClosingDummy() {
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1);
		gd_composite.heightHint = height_group_closing_dummy;
		composite.setLayoutData(gd_composite);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
	}

}
