package acp;


//import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class MyInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	static final int xOffset = 0, yOffset = 0;
    static final int DefaultWidth = 400, DefaultHeight = 300;
    private boolean modal=false;

    public MyInternalFrame() {
        setSize(DefaultWidth,DefaultHeight);
        setLocation(xOffset, yOffset);
        setClosable(true);
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());
        setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);*/
    }
    
	public void Modal(boolean value) {
		if (value) {
			startModal();
			//stopModal();
		} else {
			stopModal();
		}
	}

	private synchronized void startModal() {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				EventQueue theQueue = getToolkit().getSystemEventQueue();
				while (isVisible()) {
					AWTEvent event = theQueue.getNextEvent();
					Object source = event.getSource();
					boolean dispatch=true;
					if (event instanceof MouseEvent) {
						MouseEvent e = (MouseEvent)event;
						MouseEvent m = SwingUtilities.convertMouseEvent ((Component) e.getSource(),e,this);
						if (!this.contains(m.getPoint()) && e.getID()!=MouseEvent.MOUSE_DRAGGED) dispatch=false;
					}
					if (dispatch)
						if (event instanceof ActiveEvent) {
							((ActiveEvent)event).dispatch();
						} else if (source instanceof Component) {
							((Component)source).dispatchEvent(event);
						} else if (source instanceof MenuComponent) {
							((MenuComponent)source).dispatchEvent(event);
						} else {
							System.err.println("Unable to dispatch: " + event);
						}
				}
			} else {
				while (isVisible()) {
					wait();
				}
			}
		} catch (InterruptedException ignored) {}
	}

	private synchronized void stopModal() {
		notifyAll();
	}

	public void setModal(boolean modal) {
		this.modal=modal;
	}

	public boolean isModal() {
		return this.modal;
	}
}
