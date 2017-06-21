package com.OrhanAsan.MovieProfiler.MovieProfiler;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import info.movito.themoviedbapi.*;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.people.PersonPeople;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import javax.swing.event.ListSelectionEvent;


public class MainWindow {
	
	public List<MovieDb> movieList = new ArrayList<MovieDb>();
	public MovieDb result = new MovieDb();
	private JFrame frmMovieProfiler;
	private JTextField inputBoxSearch;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmMovieProfiler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	private void initialize(){
			frmMovieProfiler = new JFrame();
			frmMovieProfiler.addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent arg0) {
					
					File f = new File("database.db");
					String url = "jdbc:sqlite:database.db";
					if(f.exists() && !f.isDirectory()) { 					
				        try (Connection conn = DriverManager.getConnection(url)) {
				            if (conn != null) {
				            	JOptionPane.showMessageDialog(null, "The database is loaded.", "Success!", JOptionPane.INFORMATION_MESSAGE);
				            }
				 
				        } catch (SQLException e) {
				        	JOptionPane.showMessageDialog(null, "The database is not loaded properly. Error message is " + e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
				        }
					}
					else
					{					
				        try (Connection conn = DriverManager.getConnection(url)) {
				            if (conn != null) {
				            	JOptionPane.showMessageDialog(null, "A new database has been created and it is loaded.", "Success!", JOptionPane.INFORMATION_MESSAGE);
				            }
				 
				        } catch (SQLException e) {
				        	JOptionPane.showMessageDialog(null, "The database is not loaded properly. Error message is " + e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
				        }
					}				
				}});
			frmMovieProfiler.setTitle("Movie Profiler");
			frmMovieProfiler.setBounds(100, 100, 388, 296);
			frmMovieProfiler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmMovieProfiler.getContentPane().setLayout(null);
			
			final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
					switch (sourceTabbedPane.getSelectedIndex())
					{
					case 0: frmMovieProfiler.setBounds(100, 100, 388, 296); break;
					case 1: frmMovieProfiler.setBounds(100, 100, 709, 365); break;
					case 2: frmMovieProfiler.setBounds(100, 100, 698, 368); break;
					}				
				}
			});
			tabbedPane.setBounds(0, 0, 704, 394);
			frmMovieProfiler.getContentPane().add(tabbedPane);
			
			JPanel searchPanel = new JPanel();
			tabbedPane.addTab("Search", null, searchPanel, null);
			searchPanel.setLayout(null);
			
			JLabel lblMovieToSearch = new JLabel("Movie to Search");
			lblMovieToSearch.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblMovieToSearch.setBounds(10, 11, 91, 23);
			searchPanel.add(lblMovieToSearch);
			
			inputBoxSearch = new JTextField();
			inputBoxSearch.setFont(new Font("Calibri", Font.PLAIN, 11));
			inputBoxSearch.setBounds(111, 12, 147, 20);
			searchPanel.add(inputBoxSearch);
			inputBoxSearch.setColumns(10);
			
			final JList listMovie = new JList();
			JScrollPane scrollPane = new JScrollPane(listMovie);
			
			listMovie.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane.setBounds(10, 41, 347, 182);
			searchPanel.add(scrollPane);
			
			JButton btnSearch = new JButton("Search");
			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try
					{
						movieList.clear();
						DefaultListModel<String> listModel = new DefaultListModel<>();
						TmdbApi tmdb = new TmdbApi("5cd1b3b309a8eb884ee5577aeaf112ee");
						TmdbSearch search = tmdb.getSearch();
						movieList = search.searchMovie(inputBoxSearch.getText(), 0, "", true, 0).getResults();
						for (MovieDb element: movieList)
						{
							listModel.addElement(element.toString());
						}
						listMovie.setModel(listModel);
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(null, "Something went wrong. Error message is " + ex.getMessage() + ". Application is going to be closed.", "Error!", JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					}
				}
			});
			btnSearch.setBounds(268, 11, 89, 23);
			searchPanel.add(btnSearch);
			
			JPanel infoPanelMovie = new JPanel();
			tabbedPane.addTab("Profile of the Movie", null, infoPanelMovie, null);
			infoPanelMovie.setLayout(null);
			
			final JLabel poster = new JLabel("");
			poster.setBounds(10, 11, 185, 277);
			infoPanelMovie.add(poster);
			
			final JLabel lblTitle = new JLabel("Title:");
			lblTitle.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblTitle.setBounds(205, 11, 233, 14);
			infoPanelMovie.add(lblTitle);
			
			final JLabel lblYearOf = new JLabel("Release Date (USA):");
			lblYearOf.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblYearOf.setBounds(205, 36, 233, 14);
			infoPanelMovie.add(lblYearOf);
			
			final JLabel lblDuration = new JLabel("Duration:");
			lblDuration.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblDuration.setBounds(205, 61, 233, 14);
			infoPanelMovie.add(lblDuration);
			
			final JLabel lblBudget = new JLabel("Budget:");
			lblBudget.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblBudget.setBounds(205, 86, 233, 14);
			infoPanelMovie.add(lblBudget);
			
			final JLabel lblGross = new JLabel("Gross:");
			lblGross.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblGross.setBounds(205, 111, 233, 14);
			infoPanelMovie.add(lblGross);
			
			final JLabel lblDirector = new JLabel("Director:");
			lblDirector.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblDirector.setBounds(205, 136, 233, 14);
			infoPanelMovie.add(lblDirector);
			
			final JList listCastMembers = new JList();
			listCastMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
			JScrollPane scrollPane2 = new JScrollPane(listCastMembers);
			scrollPane2.setBounds(492, 36, 185, 190);
			infoPanelMovie.add(scrollPane2);
			
			final JLabel lblCastMember = new JLabel("Notable Cast Members");
			lblCastMember.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblCastMember.setBounds(492, 11, 136, 14);
			infoPanelMovie.add(lblCastMember);
			
			JButton btnAddToDatabase = new JButton("Add this movie to database");
			btnAddToDatabase.setFont(new Font("Calibri", Font.PLAIN, 13));
			btnAddToDatabase.setBounds(492, 237, 185, 51);
			infoPanelMovie.add(btnAddToDatabase);
			
			final JLabel lblGenres = new JLabel("Genres:");
			lblGenres.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblGenres.setBounds(205, 161, 233, 14);
			infoPanelMovie.add(lblGenres);
			
			final JLabel lblImdbPoint = new JLabel("IMDB Point: ");
			lblImdbPoint.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblImdbPoint.setBounds(205, 183, 233, 14);
			infoPanelMovie.add(lblImdbPoint);
			
			final JPanel infoPanelPerson = new JPanel();
			tabbedPane.addTab("Profile of the Person", null, infoPanelPerson, null);
			infoPanelPerson.setLayout(null);
			
			final JLabel profile = new JLabel("");
			profile.setBounds(10, 11, 185, 277);
			infoPanelPerson.add(profile);
			
			final JLabel lblName = new JLabel("Name: ");
			lblName.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblName.setBounds(205, 11, 233, 14);
			infoPanelPerson.add(lblName);
			
			final JLabel lblBirthdate = new JLabel("Birth date: ");
			lblBirthdate.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblBirthdate.setBounds(205, 36, 233, 14);
			infoPanelPerson.add(lblBirthdate);
			
			final JLabel lblBirthplace = new JLabel("Birth place: ");
			lblBirthplace.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblBirthplace.setBounds(205, 60, 233, 14);
			infoPanelPerson.add(lblBirthplace);
			
			final JLabel lblDeathdate = new JLabel("Death date: ");
			lblDeathdate.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblDeathdate.setBounds(205, 85, 233, 14);
			infoPanelPerson.add(lblDeathdate);
			
			final JLabel lblPopularity = new JLabel("Popularity: ");
			lblPopularity.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblPopularity.setBounds(205, 110, 233, 14);
			infoPanelPerson.add(lblPopularity);
			
			final JLabel lblBiography = new JLabel("Biography:");
			lblBiography.setFont(new Font("Calibri", Font.PLAIN, 14));
			lblBiography.setBounds(448, 11, 101, 14);
			infoPanelPerson.add(lblBiography);
			
			final JTextArea txtBiography = new JTextArea();
			txtBiography.setLineWrap(true);
			txtBiography.setWrapStyleWord(true);
			txtBiography.setEditable(false);
			JScrollPane scrollPane3 = new JScrollPane(txtBiography);
			scrollPane3.setBounds(448, 36, 222, 252);
			infoPanelPerson.add(scrollPane3);
			
			listMovie.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					if (listMovie.getSelectedIndex() != -1)
					{
						int indexOfMovie = listMovie.getSelectedIndex();
						TmdbApi tmdb = new TmdbApi("5cd1b3b309a8eb884ee5577aeaf112ee");
						TmdbMovies movies = tmdb.getMovies();
						result = movies.getMovie(movieList.get(indexOfMovie).getId(), "en", MovieMethod.credits);
						
						lblTitle.setText("Title: " + result.getTitle());
						
						lblYearOf.setText("Release Date (USA): " + result.getReleaseDate());
						
						lblDuration.setText("Duration: " + Integer.toString(result.getRuntime()) + " minutes");
						
						lblBudget.setText("Budget: " + NumberFormat.getCurrencyInstance(new Locale("en","US")).format(result.getBudget()));
						
						lblGross.setText("Gross: " + NumberFormat.getCurrencyInstance(new Locale("en","US")).format(result.getRevenue()));
						
						int directorId = 0;
						for (PersonCrew a : result.getCrew())
						{
							if (a.getJob() == "Director")
							{
								directorId = a.getCastId();
								break;
							}
						}					
						lblDirector.setText("Director: " + result.getCrew().get(directorId).getName());	
						
						String genres = "";
						int counter = 1;
						for (Genre a : result.getGenres())
						{
							if (result.getGenres().size() == counter)
								genres += a.getName();
							else
								genres += a.getName() + ", ";
							counter++;
						}					
						lblGenres.setText("Genres: " + genres);
						
						DefaultListModel<String> listModel = new DefaultListModel<>();
						for (PersonCast a : result.getCast())
						{
							listModel.addElement(a.getName() + " as " + a.getCharacter());	
							
						}
						listCastMembers.setModel(listModel);
						
						Document doc;
						try {
							doc = Jsoup.connect("http://www.imdb.com/title/" + result.getImdbID()).get();
							Element ratingVal = doc.select("div.ratingValue").first().select("span[itemprop]").first();
							lblImdbPoint.setText("IMDB Point: " + ratingVal.text());	
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}										
						
						URL img;
						try {
							img = new URL("http://image.tmdb.org/t/p/w185" + result.getPosterPath());
							ImageIcon image = new ImageIcon(img);
							poster.setIcon(image);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						tabbedPane.setSelectedIndex(1);
					}				
				}
			});
			listCastMembers.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					if (listCastMembers.getSelectedIndex() != -1)
					{				
						TmdbApi tmdb = new TmdbApi("5cd1b3b309a8eb884ee5577aeaf112ee");
						TmdbPeople person = tmdb.getPeople();
						PersonPeople resultPerson = person.getPersonInfo(result.getCast().get(listCastMembers.getSelectedIndex()).getId(), "language=en-US");
						
						lblName.setText("Name: " + resultPerson.getName());
						
						lblBirthdate.setText("Birth date: " + resultPerson.getBirthday());

						if(resultPerson.getDeathday() == "")
							lblDeathdate.setText("Death date: Still Alive");
						else
							lblDeathdate.setText("Death date:" + resultPerson.getDeathday());					

						lblBirthplace.setText("Birth place: " + resultPerson.getBirthplace());
						
						lblPopularity.setText("Popularity: " + String.format("%.2f", resultPerson.getPopularity() * 10) + "%");
						
						txtBiography.setText(resultPerson.getBiography());
						
						System.out.println(resultPerson.getBiography());
						
						URL img = null;
						try {
							img = new URL("http://image.tmdb.org/t/p/w185" + resultPerson.getProfilePath());
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ImageIcon image = new ImageIcon(img);
						profile.setIcon(image);
						tabbedPane.setSelectedIndex(2);
					}
				}
			});
			btnAddToDatabase.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!result.equals(new MovieDb()))
					{
						String url = "jdbc:sqlite:database.db";
						Connection conn = null;
				        try {
				            conn = DriverManager.getConnection(url);
				            
				            String sql = "CREATE TABLE IF NOT EXISTS movies (\n"
					                + "	id integer PRIMARY KEY,\n"
					                + " title text NOT NULL,\n"
					                + "	release_date text,\n"
					                + "	duration int,\n"
					                + "	budget text,\n"
					                + "	gross text,\n"
					                + "	director text,\n"
					                + "	genres text,\n"
					                + "	notable_cast_members text,\n"
					                + "	imdb_point real\n"
					                + ");";
							try (Statement stmt = conn.createStatement()) {
					            // create a new table
					            stmt.execute(sql);
					            
					            sql = "INSERT INTO movies(title,release_date,duration,budget,gross,director,genres,notable_cast_members,imdb_point) VALUES(?,?,?,?,?,?,?,?,?)";
								 
						        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
						            pstmt.setString(1, result.getTitle());
						            pstmt.setString(2, result.getReleaseDate());
						            pstmt.setInt(3, result.getRuntime());
						            pstmt.setString(4, NumberFormat.getCurrencyInstance(new Locale("en","US")).format(result.getBudget()));
						            pstmt.setString(5, NumberFormat.getCurrencyInstance(new Locale("en","US")).format(result.getRevenue()));
						            
						            int directorId = 0;
									for (PersonCrew a : result.getCrew())
									{
										if (a.getJob() == "Director")
										{
											directorId = a.getCastId();
											break;
										}
									}
						            pstmt.setString(6, result.getCrew().get(directorId).getName());
						            
						            String genres = "";
									int counter = 1;
									for (Genre a : result.getGenres())
									{
										if (result.getGenres().size() == counter)
											genres += a.getName();
										else
											genres += a.getName() + ", ";
										counter++;
									}					
						            pstmt.setString(7, genres);
						            
						            pstmt.setString(8, result.getCast().get(0).getName() + ", " + result.getCast().get(1).getName() + ", " + result.getCast().get(2).getName());
						            pstmt.setDouble(9, Double.parseDouble(lblImdbPoint.getText().replaceAll("IMDB Point: ", "")));
						            pstmt.executeUpdate();
						        } catch (SQLException e) {
						        	JOptionPane.showMessageDialog(null, "The datas cannot be inserted to table. Error message is " + e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
						        }
					        } catch (SQLException e) {
					        	JOptionPane.showMessageDialog(null, "The table is not created. Error message is " + e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
					        }
				        } catch (SQLException e) {
				        	JOptionPane.showMessageDialog(null, "The database is not loaded properly. Error message is " + e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
					        }		
					}
				}
			});		
	}
}
