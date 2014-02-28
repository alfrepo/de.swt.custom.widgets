package de.swt.custom.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * Button, which displays only it's icon on default.
 * The Button looks like a Button on MouseOverflow.
 * Use this Button for lightweight MenuEnter access, in order not to overload the Interface.
 * 
 * @author alf
 * 
 */
public class OverflowButton extends Composite {

    Composite parent;
    private int mouse = 0;
    private boolean hit = false;
    private Image imageDefault;
    private Image imageOver;
    private Image imagePressed;
    private Color backgroundColor;
    private Color foregroundColor;
    private Button button;
    private String buttonText = "lable";

    /**
     * Button, which displays only it's icon on default.
     * The Button looks like a Button on MouseOverflow.
     * 
     * @param parentcomp
     *            - hte parent Composite
     * @param style
     *            - the {@link SWT} Style bits
     */
    public OverflowButton(Composite parentcomp, int style) {
        super(parentcomp, style);

        parentcomp.setBackgroundMode(SWT.INHERIT_FORCE);

        // this.setLayout(new FillLayout());
        button = new Button(this, SWT.PUSH);
        button.setVisible(false);

        parent = parentcomp;
        backgroundColor = parent.getBackground();
        foregroundColor = parent.getDisplay().getSystemColor(SWT.COLOR_BLACK);

        this.setBackground(backgroundColor);

        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setBackground(backgroundColor);
                e.gc.setForeground(foregroundColor);

                switch (mouse) {
                case 0:
                    // Default state
                    e.gc.drawImage(imageDefault, 0, 0);
                    break;
                case 1:
                    // Mouse over
                    e.gc.drawImage(imageOver, 0, 0);
                    break;
                case 2:
                    // Mouse down
                    e.gc.drawImage(imagePressed, 0, 0);
                    break;
                default:
                    break;
                }
            }
        });
        this.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                if (!hit) {
                    return;
                }
                mouse = 2;
                if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
                    mouse = 0;
                }
                redraw();
            }
        });
        this.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                mouse = 1;
                redraw();
            }

            @Override
            public void mouseExit(MouseEvent e) {
                mouse = 0;
                redraw();
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                hit = true;
                mouse = 2;
                redraw();
            }

            @Override
            public void mouseUp(MouseEvent e) {
                hit = false;
                mouse = 1;
                if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
                    mouse = 0;
                }
                redraw();
                if (mouse == 1) {
                    notifyListeners(SWT.Selection, new Event());
                }
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == '\r' || e.character == ' ') {
                    Event event = new Event();
                    notifyListeners(SWT.Selection, event);
                }
            }
        });
        this.addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                button.setSize(getSize());
                updateImage();
            }

            @Override
            public void controlMoved(ControlEvent e) {

            }
        });
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point result = super.computeSize(wHint, hHint, changed);
        // calculate image size
        return result;
    }

    @Override
    public Point computeSize(int wHint, int hHint) {
        return super.computeSize(wHint, hHint);
    }

    /**
     * Sets the icon of the Button.
     * 
     * @param image
     */
    public void setImage(Image image) {
        button.setImage(image);
        updateImage();
    }

    public void setBackgroundColor(Color backgroundColorPar) {
        backgroundColor = backgroundColorPar;
    }

    public void setTextColor(Color textColorPar) {
        foregroundColor = textColorPar;
        setForeground(textColorPar);
    }

    public void setText(String buttonText) {
        this.buttonText = buttonText;
    }

    private void updateImage() {
        if (button.getSize().y <= 0 || button.getSize().x <= 0) {
            return;
        }

        // adopt image size
        Image icon = button.getImage();

        float iconWidth = icon.getBounds().width;
        float iconHeight = icon.getBounds().height;

        float buttonWidth = button.getSize().x;
        float buttonHeight = button.getSize().y;

        float scaleheight = 1f;
        float scaleWidth = 1f;

        if (iconHeight > buttonHeight) {
            scaleheight = buttonHeight / iconHeight;
        }

        if (iconWidth > buttonWidth) {
            scaleWidth = buttonWidth / iconWidth;
        }

        float scale = Math.min(scaleWidth, scaleheight);
        // now calculate scale to satisfy padding
        // TODO

        // update image size
        icon = CommonResourceUtil.resize(icon, scale);
        button.setImage(icon);

        int imgWidth = button.getSize().x;
        int imgHeight = button.getSize().y;

        if (imgWidth % 2 == 0) {
            imgWidth--;
        }
        if (imgHeight % 2 == 0) {
            imgHeight--;
        }

        imageDefault = new Image(parent.getDisplay(), button.getSize().x - 1, button.getSize().y);
        // imageDefault = new Image(parent.getDisplay(), button.getSize().x - 1,
        // button.getSize().y);
        imageOver = new Image(parent.getDisplay(), button.getSize().x - 1, button.getSize().y);
        imagePressed = new Image(parent.getDisplay(), button.getSize().x - 1, button.getSize().y);

        GC gc;

        // on default only draw the Button-Icon
        gc = new GC(imageDefault);
        if (buttonText != null) {
            Point textSize = gc.textExtent(buttonText);
            int x = (button.getSize().x - textSize.x) / 2;
            int y = (button.getSize().y - textSize.y) / 2;
            gc.setForeground(foregroundColor);
            gc.drawText(buttonText, x, y);
        } else if (button.getImage() != null) {
            int x = (button.getSize().x - button.getImage().getBounds().width) / 2;
            int y = (button.getSize().y - button.getImage().getBounds().height) / 2;
            gc.drawImage(button.getImage(), x, y);
        }

        // on mouseover look like a Button
        gc = new GC(imageOver);
        button.print(gc);

        // on mousePressed
        imagePressed = imageDefault;

        gc.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        button.dispose();
    }
}
