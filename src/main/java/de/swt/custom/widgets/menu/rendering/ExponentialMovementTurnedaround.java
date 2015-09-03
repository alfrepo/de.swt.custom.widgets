package de.swt.custom.widgets.menu.rendering;

import org.eclipse.nebula.animation.movement.AbstractMovement;

/**
 * Increases fast first, then goes slower. 
 * A constant can be provided.
 * 
 * @author alf
 *
 */
public class ExponentialMovementTurnedaround extends AbstractMovement {
	double constant = 1;
	
	public ExponentialMovementTurnedaround() {
		this(1);
	}
	
	/**
	 * This constant makes the function grow slower.
	 * @param constant
	 */
	public ExponentialMovementTurnedaround(double constant) {
		if(constant>0 && constant<=1){
			this.constant = constant;
		}
	}
	
	@Override
	public void init(double min, double max, int steps) {
		
	}

	@Override
	public double getValue(double step) {
		double state =  1-Math.exp(-constant*step);
		return state;
	}

}
