package de.swt.custom.widgets.accordion;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import de.swt.custom.widgets.Colors;

public class Style {

    final Color color;

    final Font font;

    public Style(Color styleColor, FontData fontData) {
        if (styleColor != null) {
            color = styleColor;
        } else {
            color = Colors.DARK_SHADOW;
        }

        if (fontData != null) {
            font = new Font(Display.getDefault(), fontData);
        } else {
            font = new Font(Display.getDefault(), new FontData());
        }
    }
}
