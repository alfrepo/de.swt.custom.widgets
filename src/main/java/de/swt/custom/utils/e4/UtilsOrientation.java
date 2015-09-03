package de.swt.custom.utils.e4;

import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.ui.internal.WorkbenchMessages;

public class UtilsOrientation {

	private static final String LEFT_TO_RIGHT = "ltr"; //$NON-NLS-1$
	private static final String RIGHT_TO_LEFT = "rtl";//$NON-NLS-1$
	private static final String ORIENTATION_COMMAND_LINE = "-dir";//$NON-NLS-1$
	private static final String ORIENTATION_PROPERTY = "eclipse.orientation";//$NON-NLS-1$
	private static final String NL_USER_PROPERTY = "osgi.nl.user"; //$NON-NLS-1$

	/**
	 * Get the default orientation from the command line arguments. If there are
	 * no arguments imply the orientation.
	 *
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 */
	public static int getDefaultOrientation() {

		String[] commandLineArgs = Platform.getCommandLineArgs();

		int orientation = getCommandLineOrientation(commandLineArgs);

		if (orientation != SWT.NONE) {
			return orientation;
		}

		orientation = getSystemPropertyOrientation();

		if (orientation != SWT.NONE) {
			return orientation;
		}

		return checkCommandLineLocale(); // Use the default value if there is
											// nothing specified
	}

	/**
	 * Find the orientation in the commandLineArgs. If there is no orientation
	 * specified return SWT#NONE.
	 *
	 * @param commandLineArgs
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 */
	public static int getCommandLineOrientation(String[] commandLineArgs) {
		// Do not process the last one as it will never have a parameter
		for (int i = 0; i < commandLineArgs.length - 1; i++) {
			if (commandLineArgs[i].equalsIgnoreCase(ORIENTATION_COMMAND_LINE)) {
				String orientation = commandLineArgs[i + 1];
				if (orientation.equals(RIGHT_TO_LEFT)) {
					System.setProperty(ORIENTATION_PROPERTY, RIGHT_TO_LEFT);
					return SWT.RIGHT_TO_LEFT;
				}
				if (orientation.equals(LEFT_TO_RIGHT)) {
					System.setProperty(ORIENTATION_PROPERTY, LEFT_TO_RIGHT);
					return SWT.LEFT_TO_RIGHT;
				}
			}
		}

		return SWT.NONE;
	}

	/**
	 * Check to see if the orientation was set in the system properties. If
	 * there is no orientation specified return SWT#NONE.
	 *
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 */
	private static int getSystemPropertyOrientation() {
		String orientation = System.getProperty(ORIENTATION_PROPERTY);
		if (RIGHT_TO_LEFT.equals(orientation)) {
			return SWT.RIGHT_TO_LEFT;
		}
		if (LEFT_TO_RIGHT.equals(orientation)) {
			return SWT.LEFT_TO_RIGHT;
		}
		return SWT.NONE;
	}

	/**
	 * Check to see if the command line parameter for -nl has been set. If so
	 * imply the orientation from this specified Locale. If it is a
	 * bidirectional Locale return SWT#RIGHT_TO_LEFT. If it has not been set or
	 * has been set to a unidirectional Locale then return SWT#NONE.
	 *
	 * Locale is determined differently by different JDKs and may not be
	 * consistent with the users expectations.
	 *
	 *
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 */
	public static int checkCommandLineLocale() {
		// Check if the user property is set. If not, do not rely on the VM.
		if (System.getProperty(NL_USER_PROPERTY) == null) {
			Boolean needRTL = isBidiMessageText();
			if (needRTL != null && needRTL.booleanValue()) {
				return SWT.RIGHT_TO_LEFT;
			}
		} else {
			String lang = Locale.getDefault().getLanguage();
			boolean bidiLangauage = "iw".equals(lang) || "he".equals(lang) || "ar".equals(lang) || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"fa".equals(lang) || "ur".equals(lang); //$NON-NLS-1$ //$NON-NLS-2$
			if (bidiLangauage) {
				Boolean needRTL = isBidiMessageText();
				if (needRTL == null) {
					return SWT.RIGHT_TO_LEFT;
				}
				if (needRTL.booleanValue()) {
					return SWT.RIGHT_TO_LEFT;
				}
			}
		}
		return SWT.NONE;
	}

	/**
	 * Check whether the workbench messages are in a Bidi language. This method
	 * will return <code>null</code> if it is unable to determine message
	 * properties.
	 */
	private static Boolean isBidiMessageText() {
		// Check if the user installed the NLS packs for bidi
		String message = WorkbenchMessages.Startup_Loading_Workbench;
		if (message == null) {
			return null;
		}

		try {
			// use qualified class name to avoid import statement
			// and premature attempt to resolve class reference
			boolean isBidi = com.ibm.icu.text.Bidi.requiresBidi(
					message.toCharArray(), 0, message.length());
			return new Boolean(isBidi);
		} catch (NoClassDefFoundError e) {
			// the ICU Base bundle used in place of ICU?
			return null;
		}
	}

}
