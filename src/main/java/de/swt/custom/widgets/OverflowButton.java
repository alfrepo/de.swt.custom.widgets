package de.swt.custom.widgets;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class OverflowButton extends Composite {
	
	HashMap<Listener, ListenerAdapter> listenerToAdapterMap = new HashMap<Listener, OverflowButton.ListenerAdapter>();

    StackLayout stackLayout;
    Label labelText;
    Composite labelComposite;
    Composite labelCenteredComposite;

    GridData labelCenteredCompositeGridData;

    Button button;
    Label labelIcon;

    public OverflowButton(Composite parent) {
        this(parent, SWT.DEFAULT);
    }

    /**
     * Create the composite.
     *
     * @param parent
     * @param style
     */
    public OverflowButton(Composite parent, int style) {
        super(parent, style);
        stackLayout = new StackLayout();
        setLayout(stackLayout);

        button = new Button(this, SWT.NONE);
        button.setText("Button");

        labelComposite = new Composite(this, SWT.NONE);
        GridLayout gl_labelComposite = new GridLayout(1, false);
        gl_labelComposite.marginLeft = 3;
        gl_labelComposite.verticalSpacing = 0;
        gl_labelComposite.marginWidth = 0;
        gl_labelComposite.marginHeight = 0;
        gl_labelComposite.horizontalSpacing = 0;
        labelComposite.setLayout(gl_labelComposite);

        labelCenteredComposite = new Composite(labelComposite, SWT.NONE);
        labelCenteredComposite.setLayoutData(labelCenteredCompositeGridData = new GridData(SWT.CENTER, SWT.CENTER,
                true, true, 1, 1));
        RowLayout rl_labelCenteredComposite = new RowLayout(SWT.HORIZONTAL);
        rl_labelCenteredComposite.marginTop = 0;
        rl_labelCenteredComposite.marginRight = 0;
        rl_labelCenteredComposite.marginBottom = 0;
        rl_labelCenteredComposite.marginLeft = 0;
        labelCenteredComposite.setLayout(rl_labelCenteredComposite);

        labelIcon = new Label(labelCenteredComposite, SWT.NONE);

        labelText = new Label(labelCenteredComposite, SWT.NONE);
        labelText.setText("Label");

        initValues();
        initListeners();
    }

    private void initValues() {
        setImage(null);
        setText("Button");
    }

    public void setImage(Image image) {
        this.button.setImage(image);
        this.labelIcon.setImage(image);
        if (image == null) {
            setRowLayoutObjectGone(labelIcon, true);
        } else {
            setRowLayoutObjectGone(labelIcon, false);
        }
    }

    @Override
    public void setBackground(Color color) {
        labelComposite.setBackground(color);
        labelCenteredComposite.setBackground(color);
        labelText.setBackground(color);
        labelIcon.setBackground(color);
    }

    public void setBackgroundForButton(Color color) {
        button.setBackground(color);
    }

    public Button getButton() {
        return button;
    }

    public Label getLabelWithText() {
        return labelText;
    }

    public Label getLabelWithIcon() {
        return labelIcon;
    }

    public void setText(String text) {
        this.labelText.setText(text);
        this.button.setText(text);
        if (text == null || text.isEmpty()) {
            setRowLayoutObjectGone(labelText, true);
        } else {
            setRowLayoutObjectGone(labelText, false);
        }
    }

    /**
     *
     * @param alignment
     *            SWT.CENTER, SWT.LEFT, SWT.RIGHT
     */
    public void setAlignment(int alignment) {
        labelCenteredCompositeGridData.horizontalAlignment = alignment;
        button.setAlignment(alignment);
    }

    private void setRowLayoutObjectGone(Control c, boolean isGone) {
        RowData rd_labelIcon = new RowData(SWT.DEFAULT, SWT.DEFAULT);
        rd_labelIcon.exclude = isGone;
        c.setLayoutData(rd_labelIcon);
    }

    private void initListeners() {
        MouseTrackAdapter mouseEnter = new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                stackLayout.topControl = button;
                OverflowButton.this.layout(true, true);
            }
        };
        MouseTrackAdapter mouseExit = new MouseTrackAdapter() {
            @Override
            public void mouseExit(MouseEvent e) {
                stackLayout.topControl = labelComposite;
                OverflowButton.this.layout(true, true);
            }
        };

        labelText.addMouseTrackListener(mouseEnter);
        labelComposite.addMouseTrackListener(mouseEnter);

        button.addMouseTrackListener(mouseExit);

        // init
        stackLayout.topControl = labelComposite;
    }
    
    /**
     * The events are not forwarded to the parent composite.
     * Forward them explicitely from the button and the labels.
     */
    @Override
    public void addListener(int eventType, Listener listener) {
    	super.addListener(eventType, listener);
    	
    	ListenerAdapter adapter = new ListenerAdapter(listener);
    	// remember which listener is associated with the adapter to be abel t oremove adapters by listener
    	listenerToAdapterMap.put(listener, adapter);
    	button.addListener(eventType, adapter);
    	labelComposite.addListener(eventType, adapter);
    	labelCenteredComposite.addListener(eventType, adapter);
    }
    
    @Override
    public void removeListener(int eventType, Listener listener) {
    	super.removeListener(eventType, listener);
    	
    	ListenerAdapter adapter = listenerToAdapterMap.remove(listener);
    	button.removeListener(eventType, adapter);
    	labelComposite.removeListener(eventType, adapter);
    	labelCenteredComposite.removeListener(eventType, adapter);
    }

    /** This Listener replaces the widget, which is associated with the event, by the overfloButton.
     *  So it feels, as if the event occurred inside the OverfloButton */
    class ListenerAdapter implements Listener{
    	Listener originalOverflowButtonListener;
    	
    	public ListenerAdapter(Listener originalOverflowButtonListener) {
			this.originalOverflowButtonListener = originalOverflowButtonListener;
		}
    	
		@Override
		public void handleEvent(Event event) {
			event.widget = OverflowButton.this;
			originalOverflowButtonListener.handleEvent(event);
		}
    }
    
}
