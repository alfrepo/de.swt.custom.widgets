package de.swt.custom.cropimage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class ShellTest extends Shell {
	private DataBindingContext m_bindingContext;

	private Model model = new Model();
	private Combo combo;
	
	private Combo comboViewerCombo;
	private ComboViewer comboViewer;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Display display = Display.getDefault();
					ShellTest shell = new ShellTest(display);
					shell.open();
					shell.layout();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ShellTest(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		combo = new Combo(this, SWT.NONE);
		combo.setBounds(44, 62, 91, 23);
		
		comboViewer = new ComboViewer(this, SWT.NONE);
		
		comboViewerCombo = comboViewer.getCombo();
		comboViewerCombo.setBounds(176, 62, 91, 23);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ComboViewer c = comboViewer;
				System.out.println("model.selectionObject: "+model.selectionObject);
				System.out.println("debug: "+((IStructuredSelection)comboViewer.getSelection()).getFirstElement());
				System.out.println("debug: "+(comboViewerCombo.getSelectionIndex()));
			}
		});
		btnNewButton.setBounds(110, 157, 75, 25);
		btnNewButton.setText("New Button");
		createContents();
		m_bindingContext = initDataBindings();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	
	class Model{
		List<Bean> list = new ArrayList<Bean>();
		Bean selectionObject;
		
		Model(){
			list.add(new Bean());
			list.add(new Bean());
			list.add(new Bean());
		}
		
		public Bean getSelectionObject() {
			return selectionObject;
		}
		
		public void setSelectionObject(Bean selectionObject) {
			this.selectionObject = selectionObject;
		}
		
		public List<Bean> getList() {
			return list;
		}
		
		public void setList(List<Bean> list) {
			this.list = list;
		}
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), Bean.class, "label");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = PojoProperties.list("list").observe(model);
		comboViewer.setInput(listModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue selectionObjectModelObserveValue = PojoProperties.value("selectionObject").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, selectionObjectModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
