package gui;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import logic.Controller;


public class MonsterAddListener implements ActionListener
{
	Controller controller;

	public MonsterAddListener(Controller c){
		controller = c;
	}

	public void actionPerformed(ActionEvent e) 
	{ 
		boolean nullValue = false;
		ArrayList<String> nameList = controller.getNameList();
		String name = JOptionPane.showInputDialog(null, "Name");
		for(int i = 0; i < nameList.size() &&!nullValue; i++)
		{
			if(name != null){
				if(name.equals(nameList.get(i))) //names must be unique
				{
					name = JOptionPane.showInputDialog(null, "Names must be unique,select a different name"); //ask for new name
					i = 0; //start comparing over again
				}
			}
			else{
				nullValue = true;
			}
			
			
		}
		
		int hp = 0;
		if(!nullValue){
			String hpInput = JOptionPane.showInputDialog(null, "HP");
			
			if(hpInput != null){
				hp = Integer.parseInt(hpInput);
				
			}else{
			   nullValue = true;
			}
		}
		
		int initiative = 0;
		if(!nullValue){
			String initiativeInput = JOptionPane.showInputDialog(null, "Initiative");
			if(initiativeInput != null){
				initiative = Integer.parseInt(initiativeInput);
			}else{
				nullValue = true;
			}
		}
		
		
		if(!nullValue){
			controller.addCombatant(new BattleSquare(name,hp,initiative, controller));
		}

	}


}
