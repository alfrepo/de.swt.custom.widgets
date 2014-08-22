package de.swt.custom.tools;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ToolMath {

	
	
	/* INTERPOLATION */
	/**
	 * Interpolates two colors for the given percentage
	 * @param colorFrom
	 * @param colorTo
	 * @param percentage in the range from 0-1
	 * @return - interpolated color
	 */
	public static Color interpolate(Color colorFrom, Color colorTo, float percentage){
		float percentInRange = cutRange(percentage, 0, 1);
		int r1 = colorFrom.getRed();
		int r2 = colorTo.getRed();
		int r3 = (int)interpolate(r1, r2, percentInRange);
		
		int g1 = colorFrom.getGreen();
		int g2 = colorTo.getGreen();
		int g3 = (int)interpolate(g1, g2, percentInRange);

		int b1 = colorFrom.getBlue();
		int b2 = colorTo.getBlue();
		int b3 = (int)interpolate(b1, b2, percentInRange);
		
		return new Color(Display.getCurrent(), r3, g3, b3);
	}
	
	/**
	 * Intepolates from start to end number, by the given percentage
	 * @param numFrom - start with this number
	 * @param numTo - end with this number
	 * @param percentage - should respect the range 0 < percentage < 1. It will be forced to respect the borders.
	 * @return - the interpolated value
	 */
	public static float interpolate(float numFrom, float numTo, float percentage){
		percentage = cutRange(percentage, 0, 1);
		return numFrom * (1-percentage) + numTo*percentage;
	}
	
	/**
	 * Takes a min and max and makes the value respect the interval 
	 * @param value - the value to respect the borders
	 * @param min - the minimum
	 * @param max - the maximum
	 * @return - the value which matches the interval
	 */
	public static float cutRange(float value, float min, float max ){
		value = Math.max(min, value);
		value = Math.min(max, value);
		return value;
	}
}
