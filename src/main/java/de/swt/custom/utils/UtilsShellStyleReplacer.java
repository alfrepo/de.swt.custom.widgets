package de.swt.custom.utils;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Is able to replace {@link Shell} which is not possible in SWT. 
 * @author alf
 *
 */
public class UtilsShellStyleReplacer {
	
	/**
	 * It is not possible to replace the shell style for opened windows.
	 * This creates a new shell with the requested style and moves the content to this shell. 
	 * @param shell - shell to copy
	 * @param shellStyle - the new shell-style
	 */
	public static void replaceShellStyle(Shell shell, int shellStyle){
		// create new, hidden shell
		Shell newShell = copyShell(shell, shellStyle);
		
		// move children but do not redraw yet, so that the shell looks the same and do not flicker 
		moveChildren(shell, newShell, false);
		
		// place the new shell above the old shell
		newShell.moveAbove(shell);
		
		// display the new shell
		newShell.open();
		
		// close and dispose the old shell
		shell.close();
	}
	
	/**
	 * Moves the children from one shell to another.
	 * @param source - move the Controls from this shell
	 * @param target - move the Controls to this shell
	 */
	private static void moveChildren(Shell source, Shell target, boolean redraw){
		System.out.println("Children: "+source.getChildren() + " size "+source.getChildren().length);
		for(Control child:source.getChildren()){
			System.out.println("Child: "+child);
			// child
			child.setParent(target);
		}
		
		target.layout(true, true);
		if(redraw){
			target.redraw();	
		}
		
	}

	/**
	 * Creates a new shell, which will have exactly the same 
	 * <li> position
	 * <li> size 
	 * <li> layout as the given, but a different, given style.
	 *  
	 *  The copied shell' style may still be modified, since it is returned in closed state and is  opened with {@link Shell#open()} yet.
	 *  
	 * @param shell - which shells position to copy?
	 * @param shellStyle - which style to apply to the new shell
	 * @return - the new shell
	 */
	private static Shell copyShell(Shell shell, int shellStyle){
		Shell copy = new Shell(shell.getDisplay(), shellStyle);
		if(shell.getParent()!=null){
			copy.setParent(shell.getParent());	
		}
		copy.setBounds(shell.getBounds());
		copy.setLayout(shell.getLayout());
		copy.setLayoutData(shell.getLayoutData());
		return copy;
	}
}
