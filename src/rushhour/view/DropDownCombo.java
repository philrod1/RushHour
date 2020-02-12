package rushhour.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class DropDownCombo<T> extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8988647006988490352L;
	private final JComboBox jcb;
	private final List<T> items;
	private final Vector<String> names;
	private T selected;
	private final List<Observer> observers;
	
	public DropDownCombo (List<T> items) {
		observers = new LinkedList<Observer>();
		setLayout(new GridLayout(1, 1));
		this.items = items;
		names = new Vector<String>();
		for(T item : items) {
			names.add(item.getClass().getSimpleName());
		}
		jcb = new JComboBox(names);
		jcb.setPreferredSize(new Dimension(160, 20));
		jcb.addActionListener(this);
		add(jcb);
		selected = items.get(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		T newSelection = items.get(((JComboBox)e.getSource()).getSelectedIndex());
		if (newSelection != selected) {
			selected = newSelection;
			notifyObservers();
		}
	}
	
	public T getSelected() {
		return selected;
	}
	
	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
			notifyObservers();
		}
	}
	
	public void removeObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.remove(observer);
		}
	}
	
	public void clearObservers() {
		observers.clear();
	}
	
	private void notifyObservers() {
		
		for(Observer observer : observers) {
			observer.update(null, selected);
		}
	}
	
	public void setSelectedItem(String itemName) {
		jcb.setSelectedIndex(names.indexOf(itemName));
	}
}
