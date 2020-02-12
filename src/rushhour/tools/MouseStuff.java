package rushhour.tools;

import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.event.MouseInputListener;

import rushhour.controller.RushHour;
import rushhour.exceptions.FileLoadErrorException;
import rushhour.view.GridPanel;
import rushhour.view.VehiclePanel;

public class MouseStuff implements MouseInputListener {

	private boolean selected;
	private VehiclePanel vehicle;
	private boolean block;
	private RushHour controller;
	private boolean fail = false;
	private final ThreadPoolExecutor exec;

	public MouseStuff(RushHour controller) {
		super();
		this.controller = controller;
		exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		block = false;
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (!fail
				&& selected
				&& !block
				&& e.getSource() instanceof GridPanel
				&& vehicle != null) {
					controller.moveSelectedTo(e.getPoint(), vehicle);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (!block) {
			Runnable r = null;
			if (!selected && e.getSource() instanceof GridPanel
					&& exec.getQueue().size() == 0) {
				fail = false;
				selected = true;
				r = new Runnable() {
					public void run() {
						VehiclePanel vp = ((GridPanel) e.getSource())
								.getVehicleAt(e.getPoint());
						vehicle = vp;
						if (vp != null) {
							vp.canSelect = false;
							vp.setOffset(e.getPoint());
							controller.select(vp);
						} else {
							System.out.println("NULL");
						}
					}
				};
			} else if (e.getSource() instanceof JButton) {
				JButton jb = (JButton) e.getSource();
				if (jb.getText().equals("Back")) {
					r = new Runnable() {
						public void run() {
							controller.back();
						}
					};
				} else if (jb.getText().equals("Next")) {
					r = new Runnable() {
						public void run() {
							controller.next();
						}
					};
				} else if (jb.getText().equals("Solve")) {
					r = new Runnable() {
						public void run() {
							controller.solve();
						}
					};
				} else if (jb.getText().equals("Play")) {
					r = new Runnable() {
						public void run() {
							controller.play();
						}
					};
				} else if (jb.getText().equals("Load")) {
					r = new Runnable() {
						public void run() {
							try {
								controller.load();
							} catch (FileLoadErrorException e) {
								controller.setMessage(e.getMessage());
							}
						}
					};
				} else if (jb.getText().equals("Reset")) {
					r = new Runnable() {
						public void run() {
							try {
								controller.reload();
							} catch (FileLoadErrorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
				}
			}
			if (r != null) {
				exec.execute(r);
			}
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (fail) {
			fail = false;
			return;
		} else if (!block && vehicle != null ) {
			Runnable r = new Runnable() {
				public void run() {
					controller.releaseSelected(vehicle);
					vehicle = null;
				}
			};
			exec.execute(r);
		}
		selected = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// ((VehiclePanel)e.getSource()).flash();

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public void block() {
		block = true;
	}

	public void unblock() {
		block = false;
	}

}