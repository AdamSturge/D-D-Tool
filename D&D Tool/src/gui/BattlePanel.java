package gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import logic.Controller;


public class BattlePanel extends JPanel
{
	private JPanel mainPanel = new JPanel(new GridLayout(4,4));
	private JList intList;
	private DefaultListModel listModel;
	private Controller controller;

	public BattlePanel(Controller c)
	{
		controller = c;

		listModel = new DefaultListModel();
		intList = new JList(listModel);
		JScrollPane listScrollPane = new JScrollPane(intList);
		add(listScrollPane, BorderLayout.EAST);


		JButton addMonsterButton = new JButton("Add Monster");
		MonsterAddListener addMonsterListener = new MonsterAddListener(controller);
		addMonsterButton.addActionListener(addMonsterListener);


		JButton addPCButton = new JButton("Add PC");
		PCAddListener addPCListener = new PCAddListener(controller);
		addPCButton.addActionListener(addPCListener);


		JButton clearButton = new JButton("Clear");
		ClearListener cListener = new ClearListener(controller);
		clearButton.addActionListener(cListener);


		JButton delayButton = new JButton("Delay");
		DelayListener dListener = new DelayListener(controller);
		delayButton.addActionListener(dListener);
		
		JButton editButton = new JButton("Edit");
		EditListener eListener = new EditListener(controller);
		editButton.addActionListener(eListener);


		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
		buttonPanel.add(addMonsterButton);
		buttonPanel.add(addPCButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(delayButton);
		buttonPanel.add(editButton);
		add(buttonPanel, BorderLayout.WEST);

		listScrollPane.setPreferredSize(new Dimension(100,500));
		add(mainPanel,BorderLayout.CENTER );
		setPreferredSize(new Dimension(1000,1000));
		setVisible(true);
	}

	public void addToGrid(BattleSquare battleSquare){
		mainPanel.add(battleSquare);
		validate();
		repaint();
	}

	public void addToList(String name, int position){
		listModel.addElement(name);
		intList.ensureIndexIsVisible(position);
	}

	public void insertToListAt(String name, int i){
		listModel.insertElementAt(name,i);
		intList.ensureIndexIsVisible(i);
	}

	public int getListSize(){
		return listModel.getSize();
	}

	public int searchList(String name){
		String loopName;
		for(int i = 0; i < listModel.size(); ++i){
			loopName = (String)listModel.get(i);
			if(loopName.equals(name)){
				return i;
			}
		}

		return -1; //indicates that name was not found in the last
	}

	//returns true if the name was removed from the list, false otherwise
	public boolean removeFromList(String name){
		int i = searchList(name);
		if(i > -1){
			listModel.remove(i);
			return true;
		}
		return false;

	}

	//inserts name1 after name2 in the list
	public boolean insertAfter(String name1, String name2){
		int i = searchList(name2);
		if(i > -1){
			listModel.insertElementAt(name1, i+1);
			return true;
		}
		return false;
	}

	public String getFromList(int i){
		return (String) listModel.get(i);
	}

	public void refresh(){
		validate();
		repaint();
	}

	public void clear(){
		remove(mainPanel);
		mainPanel = new JPanel(new GridLayout(4,4));
		add(mainPanel);

		listModel.clear();
		controller.clearNameLookup();

		validate();
		repaint();
	}
	
	public void clearList(){
		listModel.clear();
		
		validate();
		repaint();
	}

	public ArrayList<BattleSquare> getMonsters(){
		ArrayList<BattleSquare> monsters = new ArrayList<BattleSquare>();
		Component[] components = mainPanel.getComponents();
		for(Component c: components){
			monsters.add((BattleSquare)c);
		}
		
		return monsters;
	}
	
	//removes the monster square from the center grid
	public void removeFromGrid(BattleSquare monsterSquare){
		mainPanel.remove(monsterSquare);
	}
	
}
