package rushhour.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rushhour.controller.RushHour;
import rushhour.model.state.SixIntState;
import rushhour.model.state.State;
import rushhour.solver.BreadthFirst;
import rushhour.solver.Solver;
import rushhour.solver.heuristic.Heuristic;
import rushhour.solver.heuristic.ZeroHeuristic;

public class SetupPanel extends JPanel implements Observer {

	private static final long serialVersionUID = -3490324857229532132L;
	private DropDownCombo<State> stddc;
	private DropDownCombo<Solver> soddc;
	private DropDownCombo<Heuristic> heddc;
	private final RushHour controller;
	
	public SetupPanel(RushHour controller) {
		System.out.println("SETUPPANEL: " + (controller == null));
		this.controller = controller;
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		JLabel stateLabel = new JLabel("Model");
		stateLabel.setPreferredSize(new Dimension(20, 20));
		JLabel solverLabel = new JLabel("Solver");
		solverLabel.setPreferredSize(new Dimension(20, 20));
		JLabel heuristicLabel = new JLabel("Heuristic");
		heuristicLabel.setPreferredSize(new Dimension(20, 20));

		stddc = new DropDownCombo<State>(getAvailableInstances(State.class));
		soddc = new DropDownCombo<Solver>(getAvailableInstances(Solver.class));
		heddc = new DropDownCombo<Heuristic>(getAvailableInstances(Heuristic.class));
		
		heddc.setSelectedItem("ZeroHeuristic");
		stddc.setSelectedItem("SixIntState");
		soddc.setSelectedItem("BreadthFirst");
		
		stddc.addObserver(this);
		soddc.addObserver(this);
		heddc.addObserver(this);

		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 0.5;
		c.weighty = 0.33;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(stateLabel,c);
		
		c.gridx = 0;
		c.gridy = 1;
		this.add(solverLabel,c);
		
		c.gridx = 0;
		c.gridy = 2;
		this.add(heuristicLabel,c);
		
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		this.add(stddc,c);
		
		c.gridx = 1;
		c.gridy = 1;
		this.add(soddc,c);
		
		c.gridx = 1;
		c.gridy = 2;
		this.add(heddc,c);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getAvailableInstances(Class<T> type) {
		System.out.println(type.getName());

		URLClassLoader classLoader = null;
		List<T> items = new LinkedList<T>();
		URL url = type.getResource("");

		String me = type.getName().replace(".", "/")+".class";
        URL dirURL = type.getClassLoader().getResource(me);
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { new File(".").toURI().toURL() });
		} catch (MalformedURLException e2) {}
		JarFile jar = null;
		try {
			jar = new JarFile(URLDecoder.decode(dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")), "UTF-8"));
		} catch (Exception e) {
			
		}
		
		if (jar != null) {
	        Enumeration<JarEntry> entries = jar.entries();
	        System.out.println(entries.hasMoreElements());
	        while(entries.hasMoreElements()) {
	            String name = entries.nextElement().getName().replace('/', '.');
	            if(name.startsWith(type.getName().substring(0,type.getName().lastIndexOf("."))) 
	            		&& name.endsWith(".class")) {   
	            	Class<?> theClass;
	    			try {
	    				theClass = Class.forName(name.substring(0, name.length()-6),true,classLoader);
	    				Constructor<?>[] cons = theClass.getConstructors();
	    				if(cons.length > 0) {
	    					Object obj = cons[0].newInstance();
	    					if(type.isInstance(obj)) {
	    						items.add((T) obj);
	    					}
	    				}
	    			} catch (Throwable e) {} 
	            }
	        }
		} else {
			File dir = new File(url.getFile().replaceAll("%20", " "));
			String classpath = type.getName().replaceAll("^(.*\\.)[^\\.]*$","$1");
			System.out.println(classpath);
			System.out.println(dir.list().length);
			for (String file : dir.list()) {
				// System.out.println(file);
				Class<?> theClass;
				try {
					theClass = Class.forName(classpath + file.substring(0, file.length() - 6));
					// System.out.println(classpath + file.substring(0,
					// file.length() - 6));
					Constructor<?>[] cons = theClass.getConstructors();
					if (cons.length > 0) {
						Object obj = cons[0].newInstance();
						if (type.isInstance(obj)) {
							items.add((T) obj);
						}
					}
				} catch (Throwable e) {
				}
			}
		}
		return items;
	}
	
	public State getSelectedState () {
		return stddc.getSelected();
	}
	
	public State getSelectedSolver () {
		return stddc.getSelected();
	}
	
	public State getSelectedHeuristic () {
		return stddc.getSelected();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof State) {
			controller.setState((State) arg);
		} else if (arg instanceof Solver) {
			controller.setSolver((Solver) arg);
		} else if (arg instanceof Heuristic) {
			controller.setHeuristic((Heuristic) arg);
		}
	}
}
