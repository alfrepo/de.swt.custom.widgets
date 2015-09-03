package de.swt.custom.widgets.e4.overlay;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class PartOverlayTitleAreaDialog {

    private PartOverlay partOverlay;
    private MTitleAreaDialog titleAreaDialog;

    public PartOverlayTitleAreaDialog(MPart mpart, Composite parent, EPartService epartService, EModelService modelService) {
        titleAreaDialog = new MTitleAreaDialog(parent.getShell());
        partOverlay = new PartOverlay(mpart, parent, epartService, modelService) {
            @Override
            protected void createOverlayContent(Shell parent) {
                PartOverlayTitleAreaDialog.this.createContent(parent, this);
            }
        };
    }

    /**
     * Use the returned dialog to fill in the data
     *
     * @return
     */
    public TitleAreaDialog getTitleAreaDialog() {
        return this.titleAreaDialog;
    }

    public void open() {
        this.partOverlay.open();
    }
    
    public void close() {
    	this.titleAreaDialog.close();
    	this.partOverlay.close();
    }
    
    public void setDialog(MTitleAreaDialog mTitleAreaDialog){
    	if(mTitleAreaDialog != null){
    		this.titleAreaDialog = mTitleAreaDialog;
    	}
    }

    private void createContent(Shell parent, PartOverlay partOverlay) {
        titleAreaDialog.create();

        // pass size
        Point dialogSize = titleAreaDialog.getShell().getSize();
        partOverlay.setContentShellSize(dialogSize.x, dialogSize.y);

        titleAreaDialog.configureShell(parent);
        titleAreaDialog.createContents(parent);
        
    }

    // class opens some interfaces, so that the dialog may be applied to my own embedded shell
    public static class MTitleAreaDialog extends TitleAreaDialog {

        public MTitleAreaDialog(Shell parentShell) {
            super(parentShell);
        }

        @Override
        public Control createContents(Composite parent) {
            return super.createContents(parent);
        }

        @Override
        public void configureShell(Shell newShell) {
            super.configureShell(newShell);
        }
    }
}
