package de.swt.custom.widgets.e4.overlay;

import java.util.UUID;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.swt.custom.widgets.overlay.Overlay;

/**
 * Represents an overlay, which is able to cover parts.
 * It hides, when a part is hidden and shows when the part becomes visible.
 * @author skip
 *
 */
public abstract class PartOverlay extends Overlay{
	
	String partUuid;
	MPart part;
	EPartService partService;
	Shell parentShell;
	Composite parentComposite;
	IPartListener partListener;
	
	
	public PartOverlay(MPart mpart, EPartService epartService){
		super();
		
		bindToPart(mpart, epartService);
	}
	
	void bindToPart(final MPart mpart, final EPartService epartService){
		partService = epartService;
		part = mpart;
		parentComposite = mpart.getContext().get(Composite.class);
		parentShell = parentComposite.getShell();
		
		// bind part'S composite to the overlay
		bind(parentComposite);
		
		//mark the part 
		partUuid = UUID.randomUUID().toString();
		part.getTags().add(partUuid);
		
				
		partListener = new IPartListener() {
			@Override
			public void partVisible(MPart part) {
				if(!checkWidget(part, getShellOverlay(), this)) return;
				getShellOverlay().setVisible(true);
				System.out.println("partVisible");
			}
			
			@Override
			public void partHidden(MPart part) {
				if(!checkWidget(part, getShellOverlay(), this)) return;
				getShellOverlay().setVisible(false);
				System.out.println("partHidden");
			}
			
			@Override
			public void partDeactivated(MPart part) {
				if(!checkWidget(part, getShellOverlay(), this)) return;
//				getShellOverlay().setVisible(false);
				System.out.println("partDeactivated");
			}
			
			@Override
			public void partBroughtToTop(MPart part) {
				if(!checkWidget(part, getShellOverlay(), this)) return;
				rebindToShell(part, epartService);
				System.out.println("partBroughtToTop");
			}
			
			@Override
			public void partActivated(MPart part) {
				if(!checkWidget(part, getShellOverlay(), this)) return;
				getShellOverlay().setVisible(true);
				System.out.println("partActivated");
			}
		};
		
		// Part  listener
		partService.addPartListener(partListener);
		
		// init
		this.setVisible(part.isVisible());
	}
	
	void unbindFromPart(){
		partService.removePartListener(partListener);
		partListener = null;
		part.getTags().remove(partUuid);
		
		partUuid = null;
		part  = null;
		partService  = null;
		parentShell  = null;
		parentComposite  = null;
		partListener  = null;
		
		// unbind from Composite & Shell
		unbind();
	}
	
	
	/* Parts may be dragged to another shells. This method will rebind the part to the new shell */
	private void rebindToShell(MPart part2, EPartService partService){
		Composite actualParentComposite = part.getContext().get(Composite.class);
		if(actualParentComposite.getShell().equals(parentShell)) return;
		unbindFromPart();
		bindToPart(part2, partService);  // bind to the new shell
		update(); // update the overlay, so that it fits the new shell's position and size
	}
	
	private boolean checkWidget(MPart part, Shell shellOverlay, IPartListener partListener ){
		// disposed ?
		if (shellOverlay.isDisposed()){
			partService.removePartListener(partListener);
			return false;
		} 
		
		// wrong part?
		if(!part.getTags().contains(partUuid)){
			return false;
		}
		
		return true;
	}
	
}