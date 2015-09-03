package de.swt.custom.widgets.e4.overlay;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Represents an overlay, which is able to cover parts.
 * It hides, when a part is hidden and shows when the part becomes visible.
 * 
 * @author skip
 * 
 */
public abstract class PartOverlay extends Overlay {

	/** Mapping between parts UUID and the overlay  */
	public static final ConcurrentHashMap<String, PartOverlay> PARTID_TO_OVERLAY = new ConcurrentHashMap<String, PartOverlay>();
	public static final String TAG_PARTHASOVERLAY = "partHasAPartOverlay";
	
	// remember the composite shell to recognize detachement of parts
	Shell currentCompositeShell = null;
	
    String partUuid;
    MPart part;
    EPartService partService;
    
    EModelService modelService;
    
    public PartOverlay(MPart mpart, Composite parent, EPartService epartService, EModelService modelService) {
        super(parent);
        part = mpart;
        partService = epartService;

        // is the part already marked?
        partUuid = closeOldOverlayGetUuid(part);
        
        // mark the part
        if(partUuid == null){
        	partUuid = UUID.randomUUID().toString();
            part.getTags().add(partUuid);
            part.getTags().add(TAG_PARTHASOVERLAY);	
        }
        
        // associate this overlay with the part
        PARTID_TO_OVERLAY.put(partUuid, this);

        
        // Part listener
        partService.addPartListener(new IPartListener() {
        	
            @Override
            public void partHidden(MPart part) {
            	System.out.println("partHidden");
                if (!checkWidget(part, getShellOverlay(), this)) {
                    return;
                }
                getShellOverlay().setVisible(false);
            }

            @Override
            public void partDeactivated(MPart part) {
            	System.out.println("partDeactivated");
                // nothing
            }

            @Override
            public void partBroughtToTop(MPart part) {
            	System.out.println("partBroughtToTop");
            	// nothing
            }

            @Override
            public void partActivated(MPart part) {
            	System.out.println("partActivated");
                if (!checkWidget(part, getShellOverlay(), this)) {
                    return;
                }
                reconnectToNewShellOnPartDetach(part);
                getShellOverlay().setVisible(true);
            }
            
            @Override
            public void partVisible(MPart part) {
            	System.out.println("partVisible");
                if (!checkWidget(part, getShellOverlay(), this)) {
                    return;
                }            	
                // if the part was detached to a new Window - react on it's shell's size changes then
                getShellOverlay().setVisible(true);
            }
        });
    }
    
    

    /**  Closes the olde overlay, which was associated with this part.
     * @param thePart - the part, which's overlay should be closed 
     * @return - the uuid of the part, if the part is already known and marked with an overlay uuid
     */
    private String closeOldOverlayGetUuid(MPart thePart) {
    	String partUuid = null;
    	List<String> tags = thePart.getTags();
    	if(tags.contains(TAG_PARTHASOVERLAY)){
    		// search for the old overlay
    		for(String uuid:PARTID_TO_OVERLAY.keySet()){
    			if(tags.contains(uuid)){
    				partUuid = uuid;
    				break;
    			}
    		}
    		
    		// if found
    		if(partUuid != null){
    			PartOverlay overlay = PARTID_TO_OVERLAY.get(partUuid);
    			PARTID_TO_OVERLAY.remove(partUuid);
    			if(overlay != null){
    	    		// close it 
    				overlay.close();
    			}
    		}
    	}
		return partUuid;
	}

	private boolean checkWidget(MPart part, Shell shellOverlay, IPartListener partListener) {
        // disposed ?
        if (shellOverlay.isDisposed()) {
            partService.removePartListener(partListener);
            return false;
        }

        // wrong part?
        if (!part.getTags().contains(partUuid)) {
            return false;
        }

        return true;
    }
	
	
	
	private void reconnectToNewShellOnPartDetach(MPart part){
		if(hasNewShell(part)){
			Composite newParentComposite = part.getContext().get(Composite.class);
			
			/* 
			 * Reassigning the overlay Shell to the new shell does not really work well.
			 * The overlay-shell can only become a sub-shell of the content-shell on creation. 
			 *  This means I have to close the shell and recreate it with the new Shell as parent.
			 */
			close();
			
			// create a new overlay
			createOverlay(newParentComposite);
			
			// and open it!
			open();
		}
	}
	
	private boolean hasNewShell(MPart part){
		Shell partsShell = part.getContext().get(Shell.class);
		if(currentCompositeShell == null){
			currentCompositeShell = partsShell;
		}
		
		if(currentCompositeShell != partsShell){
			currentCompositeShell = partsShell;
			return true;
		}
		return false;
	}

}
