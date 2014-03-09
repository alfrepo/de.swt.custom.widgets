package de.swt.custom.widgets.accordion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.AdminPermission;


public class Main {

	public static void main(String[] args) {
	
		Display display = new Display();
		
//		//Fill Layout
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		//create an Accordion with an AccordionITem inside of the shell
		final HAccordion haccordion = new HAccordion(shell,SWT.None);
		haccordion.setHeight(200);
		haccordion.setWidth(1000);
		
		
		//add some items to the accordion
		final HAccordionItem item1 = new HAccordionItem(haccordion);
		final HAccordionItem item2 = new HAccordionItem(haccordion);
		final HAccordionItem item3 = new HAccordionItem(haccordion);
		final HAccordionItem item4 = new HAccordionItem(haccordion);
		final HAccordionItem item5 = new HAccordionItem(haccordion);
		final HAccordionItem item6 = new HAccordionItem(haccordion);

		
		//add some text to the labels
		item1.setLabelText( "Fahrerkontofuehrung");
		item2.setLabelText( "Ticketlayouteditor");
		item3.setLabelText("Layout Editor" );
		item4.setLabelText( "ICC" );
		item5.setLabelText( "Umfahrten");
		item6.setLabelText( "Preise");
		
		FontData fontData = new FontData();
    	fontData.setHeight(11);
    	fontData.setStyle(SWT.BOLD);
    	
    	Color fontColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    	Color fontColor2 = Display.getDefault().getSystemColor(SWT.COLOR_RED);
//    	item1.addBottomStyle(new Style(fontColor, fontData));
    	item1.addBottomStyleAllLabels(new Style(fontColor2, fontData));
    	haccordion.displayLabelIcons(true);
		
		
		//TODO: hide content
		//add some content to the slides
		for(HAccordionItem i: haccordion.getItemsIterator().getChildren()){
			createHeavyContent(i.getClientAreaObject());
		}
		
        //TODO DELETE:
            
        //TODO del. add an animation trigger
        shell.addListener(SWT.KeyDown, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                
                if(event.keyCode == 27){ //ESC
                    System.out.println("[WARN] ESC Key: "+event.keyCode);
                    //reverting
                    haccordion.revertAnimation();
                }
                
                if(event.keyCode == 115){ //s Button
                    System.out.println("[WARN] s Key: "+event.keyCode);
                    displayScreenshot(haccordion.getActiveItem().getClientAreaObject());
                }
                
                if(event.keyCode == 16777296){ //ENTER rechts               
                    System.out.println("[WARN] ENTER Key: "+event.keyCode);
                    haccordion.getActiveItem().getClientAreaObject().setLayoutDeferred(false);
                    
                    haccordion.getActiveItem().getClientAreaObject().setVisible(false);
                    
                    haccordion.getActiveItem().getClientAreaObject().hideChildren(true);
                    haccordion.getActiveItem().getClientAreaObject().redraw();
                    System.out.println("peng");
                }
                
//                else if(event.keyCode == 16777219){ //left                  
//                    System.out.println("[WARN] ENTER Key: "+event.keyCode);
//                    animationCommon.collapseItems();
//                }
//                else if(event.keyCode == 16777220){ //right                 
//                    System.out.println("[WARN] ENTER Key: "+event.keyCode);
//                    haccordion.animationCommon.expandAllItems();
//                }
                else{
                    System.out.println("[WARN] Key "+event.keyCode);
                    haccordion.activateItem(haccordion.getNextItem());
                }
            }
        });
        //TODO DELETE:
		
		
		
		//min. size for shell
		shell.setMinimumSize(new Point(300, 400));

		//lets the shell be minimal
		shell.layout();
		shell.pack();
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		haccordion.dispose();
	}
	
	
	private static void createHeavyContent(Composite content){
		final ThreeInRowLayout tl = new ThreeInRowLayout();
		content.setLayout(tl);
		FormLayout formLayout = new FormLayout();
		content.setLayout(formLayout);
		
		
		// initial Heavy
		HeavyControl h = new HeavyControl(content, SWT.NONE, 3);
		FormData fd = new FormData();
		fd.top = new FormAttachment(content, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(content, 50, SWT.TOP);
		h.setLayoutData(fd);
		
		// other heavies below
		createHeavies(20, content, h);
	}
	
	private static void createHeavies(int count, Composite parent, Composite positionAbove){
		if(count>0){
			count--;
			HeavyControl h = create( parent,  positionAbove);
			createHeavies(count, parent, h);
		}
	}
	
	private static HeavyControl create(Composite parent, Composite positionBelowThis){
		HeavyControl h = new HeavyControl(parent, SWT.NONE, 3);
		FormData fd = new FormData();
		fd.top = new FormAttachment(positionBelowThis, 50, SWT.TOP);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(positionBelowThis, 100, SWT.TOP);
		h.setLayoutData(fd);
		return h;
	}
	
	// Create a Screenshot of the Slide
	
	private static void displayScreenshot(Composite comp){
		Image img = CompositeSnapper.snapShot(comp);
		CompositeSnapper.popup( img );
	}
	
}

