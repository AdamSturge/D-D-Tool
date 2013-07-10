package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import database.DatabaseController;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class SpellDatabasePanel extends JPanel {
	private JPanel controlPanel;
	private JList spellList;
	private DefaultListModel listModel;
	private DatabaseController dbController;
	private String casterType; //class spell list currently being viewed
	private String activeLevel; //level of currently viewed spell list
	private String activeSchool; //school of currently viewed spell list

	private final String READ_CLERICSPELL_URL = "SELECT URL FROM ClericSpells WHERE Name = ?";
	private final String READ_WIZARDSPELL_URL = "SELECT URL FROM WizardSpells WHERE Name = ?";
	private final String READ_CLERICSPELLS= "SELECT Name FROM ClericSpells";
	private final String READ_WIZARDSPELLS = "SELECT Name FROM WizardSpells";
	private final String READ_CLERICSPELL_BYLEVEL = "SELECT Name FROM ClericSpells Where level = ?";
	private final String READ_WIZARDSPELL_BYLEVEL = "SELECT Name FROM WizardSpells Where level = ?";
	private final String READ_CLERICSPELL_BYSCHOOL = "SELECT Name FROM ClericSpells Where School = ?";
	private final String READ_WIZARDSPELL_BYSCHOOL = "SELECT Name FROM WizardSpells Where School = ?";
	private final String READ_CLERICSPELL_BYSCHOOLANDLEVEL = "SELECT Name FROM ClericSpells Where School = ? AND Level =?";
	private final String READ_WIZARDSPELL_BYSCHOOLANDLEVEL = "SELECT Name FROM WizardSpells Where School = ? AND Level =?";
	
	private final String dir = System.getProperty("user.dir");

	public SpellDatabasePanel(){
		casterType = " ";
		activeLevel = " ";
		activeSchool = " ";
		controlPanel = new JPanel();

		listModel = new DefaultListModel();
		spellList = new JList(listModel);
		spellList.getSelectionModel().addListSelectionListener(new spellListener(spellList));
		JScrollPane listScrollPane = new JScrollPane(spellList);
		listScrollPane.setPreferredSize(new Dimension(100,500));
		add(listScrollPane, BorderLayout.LINE_END);

		System.out.println(System.getProperty("user.dir"));
		dbController = new DatabaseController("jdbc:sqlite:" + dir + "\\"+ "Pathfinder.s3db");

		String[] classes = {"Wizard/Sorcerer","Cleric",};
		JComboBox classSelector = new JComboBox(classes);
		classSelector.addActionListener(new classListener());
		controlPanel.add(classSelector);

		String[] schools = {" ","abjuration","conjuration","evocation","enchantment","illusion","necromancy","transmutation"};
		JComboBox schoolSelector = new JComboBox(schools);
		schoolSelector.addActionListener(new SchoolListener());
		controlPanel.add(schoolSelector);

		String[] levels = {" ","0","1","2","3","4","5","6","7","8","9"};
		JComboBox levelSelector = new JComboBox(levels);
		levelSelector.addActionListener(new LevelListener());
		controlPanel.add(levelSelector);

		JButton displayButton = new JButton("Display spells");
		displayButton.addActionListener(new FetchListener());
		controlPanel.add(displayButton);

		add(controlPanel, BorderLayout.CENTER);

		setSize(1300,1300);
		setLocation(155,0);
		setVisible(true);
	}

	private class spellListener implements ListSelectionListener{

		private JList spellList;
		private BrowserLauncher launcher;

		public spellListener(JList m){
			spellList = m;
			try {
				launcher = new BrowserLauncher();
			} catch (BrowserLaunchingInitializingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedOperatingSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {

			ListSelectionModel lsm = (ListSelectionModel)e.getSource();

			int firstIndex = e.getFirstIndex();
			int lastIndex = e.getLastIndex();
			String spellURL = "";
			if(firstIndex == lastIndex){
				String name = (String)spellList.getSelectedValue();
				ArrayList<Object> sqlData = new ArrayList<Object>();
				sqlData.add(name);
				try {
					if(casterType.equals("Wizard/Sorcerer")){
						ResultSet rs = dbController.read(READ_WIZARDSPELL_URL, sqlData);
						rs.next();
						spellURL = rs.getString(1);
					} else if(casterType.equals("Cleric")){
						ResultSet rs = dbController.read(READ_CLERICSPELL_URL, sqlData);
						rs.next();
						spellURL = rs.getString(1);
					}


					launcher.openURLinBrowser(spellURL);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	private class classListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			casterType = (String) ((JComboBox) e.getSource()).getSelectedItem();

		}

	}

	private class LevelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			activeLevel = (String) ((JComboBox) e.getSource()).getSelectedItem();

		}

	}

	private class SchoolListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			activeSchool = (String) ((JComboBox) e.getSource()).getSelectedItem();

		}

	}

	private class FetchListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			listModel.clear();
			boolean specifiedSchool = false;
			boolean specifiedLevel = false;
			System.out.println(casterType);
			System.out.println(activeSchool);
			System.out.println(activeLevel);

			ArrayList<Object> sqlData = new ArrayList<Object>();
			try {
				if(!activeSchool.equals(" ")){
					sqlData.add(activeSchool);
					specifiedSchool = true;
				}
				if(!activeLevel.equals(" ")){
					sqlData.add(Integer.parseInt(activeLevel));
					specifiedLevel = true;
				}
				
				
				if(specifiedSchool && specifiedLevel && casterType.equals("Wizard/Sorcerer")){
					ResultSet rs = dbController.read(READ_WIZARDSPELL_BYSCHOOLANDLEVEL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(!specifiedSchool && specifiedLevel && casterType.equals("Wizard/Sorcerer")){
					ResultSet rs = dbController.read(READ_WIZARDSPELL_BYLEVEL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(specifiedSchool && !specifiedLevel && casterType.equals("Wizard/Sorcerer")){
					ResultSet rs = dbController.read(READ_WIZARDSPELL_BYSCHOOL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(!specifiedSchool && !specifiedLevel && casterType.equals("Wizard/Sorcerer")){
					ResultSet rs = dbController.read(READ_WIZARDSPELLS, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				
				if(specifiedSchool && specifiedLevel && casterType.equals("Cleric")){
					ResultSet rs = dbController.read(READ_CLERICSPELL_BYSCHOOLANDLEVEL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(!specifiedSchool && specifiedLevel && casterType.equals("Cleric")){
					ResultSet rs = dbController.read(READ_CLERICSPELL_BYLEVEL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(specifiedSchool && !specifiedLevel && casterType.equals("Cleric")){
					ResultSet rs = dbController.read(READ_CLERICSPELL_BYSCHOOL, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				if(!specifiedSchool && !specifiedLevel && casterType.equals("Cleric")){
					ResultSet rs = dbController.read(READ_CLERICSPELLS, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				
				

			}catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}



	}
}
