package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.Controller;

public class EditListener implements ActionListener {

	private Controller controller;
	private JPanel combatantPanel;
	private JFrame frame;
	private ArrayList<String> oldNameList;
	


	public EditListener(Controller c){
		controller = c;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame = new JFrame();
		combatantPanel = new JPanel(new GridLayout(0,2));

		JLabel nameLabel = new JLabel("Name");
		JLabel initiativeLabel = new JLabel("Initiative");

		combatantPanel.add(nameLabel);
		combatantPanel.add(initiativeLabel);

		oldNameList = controller.getNameList();
		JTextField nameField;
		JTextField initiativeField;
		for(String name: oldNameList){
			nameField = new JTextField();
			nameField.setText(name);
			initiativeField = new JTextField();
			initiativeField.setText(Integer.toString(controller.getBattleSquare(name).getInitiative()));
			combatantPanel.add(nameField);
			combatantPanel.add(initiativeField);
		}

		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new DoneListener());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(cancelButton);
		buttonPanel.add(doneButton);

		frame.add(combatantPanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setTitle("Edit combatants");
		frame.setVisible(true);

	}

	private class DoneListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Component[] fields = combatantPanel.getComponents();
			ArrayList<BattleSquare> combatants = new ArrayList<BattleSquare>();
			BattleSquare battleSquare = null;
			String name = null;
			int initiative;
			int j = 0;
			for(int i = 2; i < fields.length; ++i){
				if(i%2 == 0){
					name = ((JTextField) fields[i]).getText();
					battleSquare = controller.getBattleSquare(oldNameList.get(j));
					battleSquare.setName(name);
				}else{
					initiative= Integer.parseInt(((JTextField) fields[i]).getText());
					battleSquare.setInitiative(initiative);
					combatants.add(battleSquare);
					j++;
				}
			}
			frame.dispose();
			controller.clearBattlePanel();
			controller.clearNameLookup();
			controller.setNameLookup(combatants);
			controller.setList(combatants);
			controller.refreshBattlePanel();
			
		}

	}
	
	private class CancelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.dispose();
			
		}
		
	}
}




