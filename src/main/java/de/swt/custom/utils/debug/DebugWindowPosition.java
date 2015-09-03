package de.swt.custom.utils.debug;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DebugWindowPosition {

    protected Shell shell;

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            DebugWindowPosition window = new DebugWindowPosition();
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

    Shell shellPopup;
    private Text textX;
    private Text textY;
    private Text textW;
    private Text textH;
    private Text textxywh;
    private Text textCursorLocation;

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        
        // make full screen
        int width = Display.getCurrent().getClientArea().width;
        int height = Display.getCurrent().getClientArea().height;
        shell.setSize(width, height);
        shell.setLocation(0, 0);
        shell.setText("SWT Application");

        textX = new Text(shell, SWT.BORDER);
        textX.setBounds(68, 160, 76, 21);

        textY = new Text(shell, SWT.BORDER);
        textY.setBounds(68, 214, 76, 21);

        textW = new Text(shell, SWT.BORDER);
        textW.setBounds(245, 160, 76, 21);

        textH = new Text(shell, SWT.BORDER);
        textH.setBounds(245, 214, 76, 21);

        Label lblX = new Label(shell, SWT.NONE);
        lblX.setBounds(68, 137, 55, 15);
        lblX.setText("x");

        Label lblY = new Label(shell, SWT.NONE);
        lblY.setBounds(68, 193, 55, 15);
        lblY.setText("y");

        Label lblH = new Label(shell, SWT.NONE);
        lblH.setBounds(245, 193, 55, 15);
        lblH.setText("h");

        Label lblH_1 = new Label(shell, SWT.NONE);
        lblH_1.setBounds(245, 137, 55, 15);
        lblH_1.setText("w");

        final Button btnGo = new Button(shell, SWT.NONE);
        btnGo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

            	try{
                    String xywh = textxywh.getText();
                    if (!xywh.isEmpty()) {
                        String[] xywhArray = xywh.split(",");
                        int x = Integer.parseInt(xywhArray[0]);
                        int y = Integer.parseInt(xywhArray[1]);
                        int w = Integer.parseInt(xywhArray[2]);
                        int h = Integer.parseInt(xywhArray[3]);
                        btnGo.getShell().setBounds(x, y, w, h);

                    } else {
                        int x = Integer.parseInt(textX.getText());
                        int y = Integer.parseInt(textY.getText());
                        int w = Integer.parseInt(textW.getText());
                        int h = Integer.parseInt(textH.getText());
                        btnGo.getShell().setBounds(x, y, w, h);
                    }
            	}catch(Exception exception){
            		System.out.println("Fill in the coordinated of the cursor");
            	}

            }
        });
        btnGo.setBounds(349, 227, 75, 25);
        btnGo.setText("Go");

        textxywh = new Text(shell, SWT.BORDER);
        textxywh.setBounds(68, 101, 253, 21);

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setBounds(68, 80, 55, 15);
        lblNewLabel.setText("xywh");
        
        Label lblCursorLocation = new Label(shell, SWT.NONE);
        lblCursorLocation.setBounds(68, 10, 253, 15);
        lblCursorLocation.setText("Cursor Location");
        
        textCursorLocation = new Text(shell, SWT.BORDER);
        textCursorLocation.setEditable(false);
        textCursorLocation.setBounds(68, 31, 253, 21);

        
        Display.getCurrent().addFilter(SWT.MouseMove, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				textCursorLocation.setText(textCursorLocation.toDisplay(event.x, event.y).toString());		
			}
		});
        
        
    }
	public Text getTextCursorLocation() {
		return textCursorLocation;
	}
}
