package de.swt.custom.widgets.jface.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * A Widget which is able to align children in columns or rows.
 * <ol>
 * <li>The width, height of this widget may be set explicitly.
 * <li>Alternatively the widget may become as wide, high as children are by
 * passing {@link GRIP_CONSTRAINTS#PACK} for height or width constraints.
 * <li>Alternatively the widget may FILL all available space and distribute it
 * among children. This is achieved by passing {@link GRIP_CONSTRAINTS#FILL} for
 * height or width constraints.
 * </ol>
 * 
 * Inherit from this class to provide a nice interface
 * 
 * <pre>
 * {@code
 * // Column layout which fills parent
 *  new WidgetWithEquallyDistributedSpace(shell, SWT.NONE, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.FILL, SWT.HORIZONTAL);
 *  // Row layout which fills parent
 *  new WidgetWithEquallyDistributedSpace(shell, SWT.NONE, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.FILL, SWT.VERTICAL);
 *  // Row layout which fils parent horizontally but measures children vertically
 *  new WidgetWithEquallyDistributedSpace(shell, SWT.NONE, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.PACK, SWT.VERTICAL);
 *  
 * }
 * </pre>
 * 
 * @author alf
 *
 */
public class WidgetWithEquallyDistributedSpace extends Composite {

	public enum GRIP_CONSTRAINTS {
		PACK, FILL
	};

	private GRIP_CONSTRAINTS gripConstrainHorizontal;
	private GRIP_CONSTRAINTS gripConstrainVertical;

	private Composite compositeGripSides;
	private Composite compositeDistributesSpace;

	private Integer minHeight = null; // only works, when PACK is chose for
										// verticalSpaceDistribution
	private Integer minWidth = null; // only works, when PACK is chose for
										// horizontalSpaceDistribution

	public WidgetWithEquallyDistributedSpace(Composite parent, int style) {
		this(parent, style, GRIP_CONSTRAINTS.FILL, GRIP_CONSTRAINTS.FILL,
				SWT.HORIZONTAL);
	}

	public WidgetWithEquallyDistributedSpace(Composite parent, int style,
			GRIP_CONSTRAINTS gripConstrainHorizontal,
			GRIP_CONSTRAINTS gripConstrainVertical) {
		this(parent, style, gripConstrainHorizontal, gripConstrainVertical,
				SWT.HORIZONTAL);
	}

	/**
	 * Creates a new Widget
	 * 
	 * @param parent
	 *            - the SWT parent, e.g. a shell
	 * @param style
	 *            - SWT Style bits
	 * @param gripConstraintHorizontal
	 *            - Should the Widget FILL the Parent HORIZONTALLY or should it
	 *            measure the children's WIDTH and become AS WIDE AS CHILDREN
	 *            ARE
	 * @param gripConstraintVertical
	 *            - Should the Widget FILL the Parent VERTICALLY or should it
	 *            measure the children's HEIGHT and become AS HIGH AS CHILDREN
	 *            ARE
	 * @param alignChildrenHorizontallyOrVertically
	 *            - accept {@link SWT#HORIZONTAL}, {@link SWT#VERTICAL}. When
	 *            children are created with {@link #addChild()} they may be
	 *            aligned horizontally or vertically
	 */
	public WidgetWithEquallyDistributedSpace(Composite parent, int style,
			GRIP_CONSTRAINTS gripConstraintHorizontal,
			GRIP_CONSTRAINTS gripConstraintVertical,
			int alignChildrenHorizontallyOrVertically) {
		super(parent, style);

		// create children
		createContent();

		// should the column fill the parent vertically or pack? And
		// horizontally?
		setUpLayoutGripConstraints(gripConstraintHorizontal,
				gripConstraintVertical, this.minWidth, this.minHeight);

		// default layout is the layout for columns (fillLayout alligns children
		// horizontally)
		setupLayoutSpaceDistributionAmongChildren(alignChildrenHorizontallyOrVertically);

		setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
	}

	/**
	 * Creates widget hierarchy! May only be called once!
	 */
	private void createContent() {
		compositeGripSides = new Composite(this, SWT.NONE);
		compositeGripSides.setData("label", "compositeGripSides");

		compositeDistributesSpace = new Composite(compositeGripSides, SWT.NONE);
		compositeGripSides.setData("label", "compositeDistributesSpace");
	}

	private void setupLayoutSpaceDistributionAmongChildren(
			int distributeSpaceHorizontallyOrVertically) {
		compositeDistributesSpace.setLayout(new FillLayout(distributeSpaceHorizontallyOrVertically));

	}

	/**
	 * When used - then the horizontal layout grip is reset to pack, because
	 * otherwise the width can not be controlled explicitely.
	 * 
	 * @param minWidth
	 */
	protected void setWidth(int minWidth) {
		this.minWidth = minWidth;
		setUpLayoutGripConstraints(GRIP_CONSTRAINTS.PACK,
				this.gripConstrainVertical, this.minWidth, this.minHeight);
	}

	protected void setHeight(int minHeight) {
		this.minHeight = minHeight;
		setUpLayoutGripConstraints(this.gripConstrainHorizontal,
				GRIP_CONSTRAINTS.PACK, this.minWidth, this.minHeight);
	}

	/**
	 * Should do the layout of the contianer. Horizontal or Vertical.
	 */
	private void setUpLayoutGripConstraints(GRIP_CONSTRAINTS gripConstrainHorizontal,
			GRIP_CONSTRAINTS gripConstrainVertical, Integer width,
			Integer height) {
		Composite parentComposite = this;

		// remember
		this.gripConstrainHorizontal = gripConstrainHorizontal;
		this.gripConstrainVertical = gripConstrainVertical;
		this.minHeight = height;
		this.minWidth = width;

		// 1. LAYOUT PARENT
		parentComposite.setLayout(new FormLayout());

		compositeGripSides.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_RED));

		FormData formDataGripSidesComposite = new FormData();

		// consider width and height if set
		if (this.minWidth != null && this.minWidth >= 0) {
			formDataGripSidesComposite.width = this.minWidth;
		}

		if (this.minHeight != null && this.minHeight >= 0) {
			formDataGripSidesComposite.height = this.minHeight;
		}

		// top
		formDataGripSidesComposite.top = new FormAttachment(0);
		// left
		formDataGripSidesComposite.left = new FormAttachment(0);

		// right
		// consider the space distribution constraints
		if (gripConstrainHorizontal.equals(GRIP_CONSTRAINTS.FILL)) {
			formDataGripSidesComposite.right = new FormAttachment(100);
		}
		// bottom
		if (gripConstrainVertical.equals(GRIP_CONSTRAINTS.FILL)) {
			formDataGripSidesComposite.bottom = new FormAttachment(100);
		}

		// apply the settings
		this.compositeGripSides.setLayoutData(formDataGripSidesComposite);

		// 2. LAYOUT CONTAINER to make the child distributng composite fill the gripCOnstraintsWidget
		compositeGripSides.setLayout(new FillLayout(SWT.HORIZONTAL));

		// 3. APPLY LAYOUT
		this.layout(true, true);
	}

	/**
	 * All Columns
	 * 
	 * @return
	 */
	protected List<Composite> getChildrenAsComposites() {
		ArrayList<Composite> result = new ArrayList<>();
		for (Control c : compositeDistributesSpace.getChildren()) {
			if (c instanceof Composite) {
				result.add((Composite) c);
			}
		}
		return result;
	}

	protected Composite getChildAsComposite(int position) {
		return getChildrenAsComposites().get(position);
	}

	protected Composite addChild() {
		return new Composite(compositeDistributesSpace, SWT.NONE);
	}
	
	protected boolean removeChild(int position) {
		Control[] children = getChildren();
		if (0 >= position && children.length > position) {
			Control c = children[position];
			c.setVisible(false);
			c.dispose();

			this.layout(true, true);
			return true;
		}
		return false;
	}


	// INTERFACE

	protected void setUpLayoutForRows() {
		setupLayoutSpaceDistributionAmongChildren(SWT.VERTICAL);
	}

	protected void setUpLayoutForColumns() {
		setupLayoutSpaceDistributionAmongChildren(SWT.HORIZONTAL);
	}

}
