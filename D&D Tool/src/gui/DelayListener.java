package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import logic.Controller;

public class DelayListener implements ActionListener {

	private JFrame frame;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private ButtonGroup leftGroup;
	private ButtonGroup rightGroup;
	private String delayer; //name of delaying character
	private String target; //name of character the delaying character wants to go after
	private Controller controller;

	public DelayListener(Controller c){
		controller = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		frame = new JFrame();
		leftPanel = new JPanel(new GridLayout(0,1));
		rightPanel = new JPanel(new GridLayout(0,1));
		leftGroup = new ButtonGroup();
		rightGroup = new ButtonGroup();

		leftPanel.add(new JLabel("Select the delaying character"));
		rightPanel.add(new JLabel("Select character the delaying character will go after"));
		
		JCheckBox loopBox;
		

		ArrayList<String> nameList = controller.getNameList();

		//populate name lists
		for(String name: nameList){
			loopBox = new JCheckBox(name);
			loopBox.addActionListener(new CheckListener(loopBox, true));
			leftGroup.add(loopBox);
			leftPanel.add(loopBox);

			loopBox = new JCheckBox(name);
			loopBox.addActionListener(new CheckListener(loopBox, false));
			rightGroup.add(loopBox);
			rightPanel.add(loopBox);
		}

		frame.add(leftPanel, BorderLayout.WEST);
		frame.add(rightPanel, BorderLayout.EAST);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Delay GUI");
		frame.pack();
		frame.setVisible(true);

		JButton doneButton = new JButton("Done");
		DoneListener dListener = new DoneListener();
		doneButton.addActionListener(dListener);
		rightPanel.add(doneButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelListener());
		leftPanel.add(cancelButton);
		
	}

	private class CheckListener implements ActionListener{

		private JCheckBox attachedBox;
		private boolean isleftPanel; //true if this listener is attached to a checkbox in the left panel

		public CheckListener(JCheckBox box, boolean b){
			attachedBox = box;
			isleftPanel = b;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String name = attachedBox.getText();
			if(isleftPanel){
				delayer = name;
			}else{
				target = name;
			}
		}
	}
	
	private class DoneListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.moveCombatant(delayer, target);
			frame.dispose();
		}
	}
	
	private class CancelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.dispose();
			
		}
		
	}
	

}
