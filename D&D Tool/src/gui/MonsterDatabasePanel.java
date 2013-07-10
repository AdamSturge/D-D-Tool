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

public class MonsterDatabasePanel extends JPanel {
	private JPanel controlPanel;
	private JList<String> monsterList;
	private DefaultListModel<String> listModel;
	private DatabaseController dbController;
	private String activeType; //monster type currently being viewed
	private String activeLetter; //letter currently viewed monster names starts with. " " indicates don't fliter by name

	private final String READ_MONSTER_URL = "SELECT URL FROM Monsters WHERE NAME = ?";
	private final String READ_MONSTER_NAME = "SELECT Name FROM Monsters";
	private final String READ_MONSTER_NAME_BYTYPEANDLETTER = "SELECT Name FROM Monsters WHERE Type = ? AND Name LIKE ?";
	private final String READ_MONSTER_NAME_BYTYPE = "SELECT Name FROM Monsters WHERE Type = ?";
	private final String READ_MONSTER_NAME_BYLETTER = "SELECT Name FROM Monsters WHERE Name LIKE ?";
	
	private final String dir = System.getProperty("user.dir");

	public MonsterDatabasePanel(){
		activeType = " ";
		activeLetter = " ";
		controlPanel = new JPanel();

		listModel = new DefaultListModel<String>();
		monsterList = new JList<String>(listModel);
		monsterList.getSelectionModel().addListSelectionListener(new MonsterListener(monsterList));
		JScrollPane listScrollPane = new JScrollPane(monsterList);
		listScrollPane.setPreferredSize(new Dimension(250,500));
		add(listScrollPane, BorderLayout.LINE_END);

		dbController = new DatabaseController("jdbc:sqlite:" + dir + "\\"+ "Pathfinder.s3db");

		String[] types = {" ", "Aberration","Animal","Construct","Dragon","Fey","Humanoid","Magical Beast","Monsterous Humanoid","Ooze","Outsider","Plant","Undead","Vermin","Template"};
		JComboBox<String> monsterTypeSelector = new JComboBox<String>(types);
		monsterTypeSelector.addActionListener(new MonsterTypeListener());
		controlPanel.add(monsterTypeSelector);

		String[] letters = {" ", "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		JComboBox<String> letterSelector = new JComboBox<String>(letters);
		letterSelector.addActionListener(new LetterListener());
		controlPanel.add(letterSelector);

		JButton displayButton = new JButton("Display Monsters");
		displayButton.addActionListener(new FetchListener());
		controlPanel.add(displayButton);

		add(controlPanel, BorderLayout.CENTER);

		setSize(1300,1300);
		setLocation(155,0);
		setVisible(true);
	}

	private class MonsterListener implements ListSelectionListener{

		private JList<String> monsterList;
		private BrowserLauncher launcher;

		public MonsterListener(JList<String> m){
			monsterList = m;
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

				String name = monsterList.getSelectedValue();
				ArrayList<Object> sqlData = new ArrayList<Object>();
				sqlData.add(name);
				try {
					ResultSet rs = dbController.read(READ_MONSTER_URL, sqlData);
					rs.next();
					String monsterURL = rs.getString(1);
					launcher.openURLinBrowser(monsterURL);
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

	private class MonsterTypeListener implements ActionListener{

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			activeType = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();

		}

	}

	private class LetterListener implements ActionListener{

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			activeLetter = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();

		}

	}

	private class FetchListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("active type: " + activeType);
			System.out.println("active letter: " + activeLetter);

			ArrayList<Object> sqlData = new ArrayList<Object>();
			listModel.clear();
			try {
				if(activeLetter != " " && !activeType.equals(" ")){
					sqlData.add(activeType);
					sqlData.add(activeLetter.toLowerCase() + "%");
					ResultSet rs = dbController.read(READ_MONSTER_NAME_BYTYPEANDLETTER, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}
				else if(!activeType.equals(" ") && activeLetter == " "){
					sqlData.add(activeType);
					ResultSet rs = dbController.read(READ_MONSTER_NAME_BYTYPE, sqlData);
					while(rs.next()){
						listModel.addElement(rs.getString(1));
					}
				}else{
					sqlData.add(activeLetter.toLowerCase() + "%");
					ResultSet rs = dbController.read(READ_MONSTER_NAME_BYLETTER, sqlData);
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
