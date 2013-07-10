package gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;

import logic.Controller;


public class ClearListener implements ActionListener
{
	Controller controller;
	
	public ClearListener(Controller c){
		controller = c;
	}
	
	public void actionPerformed(ActionEvent e) 
	{ 
		controller.clearBattlePanel();
	}

}