package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ManagerFame extends JFrame {
	
	private final static String BATTLE_CARD = "Battle Panel";
	private final static String MONSTER_CARD = "Monster Panel";
	private final static String SPELL_CARD = "Spell Panel";
	private CardLayout cardLayout;
	private BattlePanel battlePanel;
	private MonsterDatabasePanel monsterPanel;
	private SpellDatabasePanel spellPanel;
	
	public ManagerFame(BattlePanel b){
		JMenuBar menuBar = new JMenuBar();
		JMenu viewMenu = new JMenu("View");
		JMenuItem battleTool = new JMenuItem("Battle Tool");
		JMenuItem monsterTool = new JMenuItem("Monster Tool");
		JMenuItem spellTool = new JMenuItem("Spell Tool");
		
		battleTool.addActionListener(new BattleListener());
		monsterTool.addActionListener(new MonsterListener());
		spellTool.addActionListener(new SpellListener());
		
		viewMenu.add(battleTool);
		viewMenu.add(monsterTool);
		viewMenu.add(spellTool);
		
		menuBar.add(viewMenu);
		
		battlePanel = b;
		monsterPanel = new MonsterDatabasePanel();
		spellPanel = new SpellDatabasePanel();
		
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		add(battlePanel, BATTLE_CARD);
		add(monsterPanel, MONSTER_CARD);
		add(spellPanel, SPELL_CARD);
		
		setTitle("Pathfinder Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(menuBar);
		pack();
		setVisible(true);
		
		
	}
	
	public void showBattleTool() {
		cardLayout.show(this.getContentPane(), BATTLE_CARD);
	}

	public void showMonsterTool() {
		cardLayout.show(this.getContentPane(), MONSTER_CARD);
	}

	public void showSpellTool() {
		cardLayout.show(this.getContentPane(), SPELL_CARD);
	}
	
	private class BattleListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			showBattleTool();
			
		}
		
	}
	
	private class MonsterListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			showMonsterTool();
			
		}
		
	}
	
	private class SpellListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			showSpellTool();
			
		}
		
	}
	
}
