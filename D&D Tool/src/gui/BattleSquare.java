package gui;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import logic.Controller;

public class BattleSquare extends JPanel
{
	private String name;
	private int health;
	private int initiative; 
	private JLabel HPLabel;
	private JLabel nameLabel;
	private boolean isMonster;
	private Controller controller;
	private JButton dmgButton;
	private JButton removeButton;

	public BattleSquare(String n, int h, int i, Controller c)
	{
		name = n;
		health = h;
		initiative = i;
		controller = c;

		nameLabel = new JLabel(name);
		HPLabel = new JLabel(Integer.toString(health));
		add(nameLabel);
		add(HPLabel);

		dmgButton = new JButton("Damage");
		DmgListener listener = new DmgListener(c);
		dmgButton.addActionListener(listener);
		add(dmgButton);
		
		removeButton = new JButton("Remove");
		removeButton.addActionListener(new RemoveListener());
		add(removeButton);

		controller.addToLookup(dmgButton,this);
		controller.addToLookup(name, this);
		controller.addToLookup(removeButton,this);

		isMonster = true;
	}

	public BattleSquare(String n, int i, Controller c)
	{
		name = n;
		initiative = i;
		health = 0; //set it to something in case it ever gets called, should never occur though.
		controller = c;

		controller.addToLookup(name, this);
		
		nameLabel = new JLabel(name);

		isMonster = false;
	}


	public void setHP(int hp)
	{
		health = hp;
		HPLabel.setText(Integer.toString(hp));
	}

	public int getHP()
	{
		return health;
	}

	public int getInitiative()
	{
		return initiative;
	}

	public String getName()
	{
		return name;
	}

	public boolean isMonster()
	{
		return isMonster;
	}

	public void setInitiative(int initiative) {
		this.initiative = initiative;
	}

	public void setName(String name) {
		this.name = name;
		nameLabel.setText(name);
	}
	
	private class DmgListener implements ActionListener {
		private Controller controller;
		
		public DmgListener(Controller c){
			controller = c;
		}
		
		public void actionPerformed(ActionEvent e) 
		{ 
			String damageInput = JOptionPane.showInputDialog(null, "Damage");
			int damage = 0;
			if(damageInput != null){
				Integer.parseInt(damageInput);
			}
			JButton monsterButton = (JButton)e.getSource();
			
			BattleSquare monster = controller.getBattleSquare(monsterButton);
			monster.setHP(monster.getHP() - damage);
			controller.refreshBattlePanel();
	        
		}
		
	}
	
	private class RemoveListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton monsterButton = (JButton)arg0.getSource();
			BattleSquare monsterSquare = controller.getBattleSquare(monsterButton);
			controller.removeMonster(monsterSquare.getName());
			
		}
		
	}

	public JButton getDmgButton() {
		return dmgButton;
	}

	public JButton getRemoveButton() {
		return removeButton;
	}
	
	

}
