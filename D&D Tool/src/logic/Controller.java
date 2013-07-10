package logic;

import gui.BattlePanel;
import gui.BattleSquare;
import gui.ManagerFame;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;


public class Controller {

	private BattlePanel battlePanel;
	private Map<String,BattleSquare> nameLookup;
	private Map<JButton,BattleSquare> squareLookup;
	private ManagerFame managerFrame;
	
	public Controller(){
		battlePanel = new BattlePanel(this);
		managerFrame = new ManagerFame(battlePanel);
		nameLookup = new HashMap<String,BattleSquare>();
		squareLookup = new HashMap<JButton,BattleSquare>();
	}
	
	public void addCombatant(BattleSquare battleSquare)
	{
		//only monsters get added to the grid
		if(battleSquare.isMonster())
		{
			battlePanel.addToGrid(battleSquare);
		}

		String loopName;
		BattleSquare loopSquare;
		int size = battlePanel.getListSize();
		boolean Bigger = true;

		if(size == 0) // empty list so just add in new name
		{
			battlePanel.addToList(battleSquare.getName(),0);
		}
		else 
		{ //find where the new name should fall in the ordered list
			for(int i = 0; i < size; i++)
			{
				loopName = battlePanel.getFromList(i);
				loopSquare = nameLookup.get(loopName);
				
				if(loopSquare.getInitiative() < battleSquare.getInitiative()) //found something bigger, insert before and stop looping.
				{

					battlePanel.insertToListAt(battleSquare.getName(), i);
					Bigger = false;
					break;

				}
				if(loopSquare.getInitiative() == battleSquare.getInitiative()) //detected a tie
				{
					InitiativeTie(loopSquare, battleSquare,i);
					Bigger = false;
					break;
				}

			}

			if(Bigger) //Nothing bigger, add at the beginning
			{
				battlePanel.addToList(battleSquare.getName(), size);
			}
		}

		battlePanel.refresh();


	}

	public void InitiativeTie(BattleSquare loopSquare, BattleSquare battleSquare, int i)
	{
		String tieText = battleSquare.getName() + " and " + loopSquare.getName() + " have tied, should " + battleSquare.getName() + " go first";
		String yesOrNo = JOptionPane.showInputDialog(null, tieText);

		while(!yesOrNo.equalsIgnoreCase("Y") && !yesOrNo.equalsIgnoreCase("N") 
				&&!yesOrNo.equalsIgnoreCase("Yes") && yesOrNo.equalsIgnoreCase("No"))
		{
			yesOrNo = JOptionPane.showInputDialog(null, tieText);
		}

		if(yesOrNo.equalsIgnoreCase("Y"))
		{
			battlePanel.insertToListAt(battleSquare.getName(), i);
		}
		else //insert battleSquare after
		{
			if(i < battlePanel.getListSize() -1) //gonna have to check forward
			{
				BattleSquare nextSquare = nameLookup.get(battlePanel.getFromList(i+1));
				if(battleSquare.getInitiative() == nextSquare.getInitiative())
				{
					InitiativeTie(nextSquare,battleSquare,i+1);
				}
				else
				{
					battlePanel.insertToListAt(battleSquare.getName(), i+1);
				}
			}
			else
			{
				battlePanel.insertToListAt(battleSquare.getName(), i+1);
			}


		}

	}
	
	public void addToLookup(String name, BattleSquare bs){
		nameLookup.put(name, bs);
	}
	
	public void addToLookup(JButton button, BattleSquare bs){
		squareLookup.put(button, bs);
	}
	
	public BattleSquare getBattleSquare(String name){
		return nameLookup.get(name);
	}
	
	public BattleSquare getBattleSquare(JButton button){
		return squareLookup.get(button);
	}
	
	public ArrayList<String> getNameList(){
		ArrayList<String> nameList = new ArrayList<String>();
		for(String name: nameLookup.keySet()){
			nameList.add(name);
		}
		return nameList;
	}
	
	public void refreshBattlePanel(){
		battlePanel.refresh();
	}
	
	public void clearNameLookup(){
		nameLookup.clear();
	}
	
	public void clearBattlePanel(){
		battlePanel.clear();
	}
	
	public void clearList(){
		battlePanel.clearList();
	}
	
	public void moveCombatant(String delayer, String target){
		battlePanel.removeFromList(delayer);
		battlePanel.insertAfter(delayer, target);
	}
	
	public ArrayList<BattleSquare> getCombatants(){
		ArrayList<BattleSquare> combatants = new ArrayList<BattleSquare>();
		for(BattleSquare bs: nameLookup.values()){
			combatants.add(bs);
		}
		
		return combatants;
	}
	
	public void setList(ArrayList<BattleSquare> combatants){
		for(BattleSquare bs: combatants){
			addCombatant(bs);
		}
	}

	public void setNameLookup(ArrayList<BattleSquare> combatants) {
		for(BattleSquare bs: combatants){
			nameLookup.put(bs.getName(), bs);
		}
		
	}
	
	public void removeMonster(String monsterName){
		battlePanel.removeFromList(monsterName);
		ArrayList<BattleSquare> monsters = battlePanel.getMonsters();
		for(BattleSquare loopSquare: monsters){
			if(loopSquare.getName().equals(monsterName)){
				battlePanel.removeFromGrid(loopSquare);
				nameLookup.remove(loopSquare.getName());
				squareLookup.remove(loopSquare.getRemoveButton());
				squareLookup.remove(loopSquare.getDmgButton());
			}
		}
		
		
		battlePanel.refresh();
	}
}
