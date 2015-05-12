//© Claus Burkhart und Thomas Coenen


package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import application.AuxPort;
import application.CodeReader;
import application.RegisterClass;
import application.StackClass;
import application.TextClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;



public class MyController implements Initializable{	

//Deklaration der TableView zum Anzeigen des Stacks
	
		@FXML
		public TableView<StackClass> tableViewStack;
		
		TableColumn<StackClass, String> colStack;
		TableColumn<StackClass, String> colBeschriftung;
//Deklaration der TableView zum Anzeigen des Registers
	
	@FXML
	public TableView<RegisterClass> tableViewRegister;
	
	TableColumn<RegisterClass, String> registerBeschriftung;
	TableColumn<RegisterClass, String> registerSpalte0;	 
	TableColumn<RegisterClass, String> registerSpalte1;
	TableColumn<RegisterClass, String> registerSpalte2;
	TableColumn<RegisterClass, String> registerSpalte3;
	TableColumn<RegisterClass, String> registerSpalte4;
	TableColumn<RegisterClass, String> registerSpalte5;
	TableColumn<RegisterClass, String> registerSpalte6;
	TableColumn<RegisterClass, String> registerSpalte7;
	TableColumn<RegisterClass, String> registerSpalte8;
	TableColumn<RegisterClass, String> registerSpalte9;
	TableColumn<RegisterClass, String> registerSpalteA;
	TableColumn<RegisterClass, String> registerSpalteB;
	TableColumn<RegisterClass, String> registerSpalteC;
	TableColumn<RegisterClass, String> registerSpalteD;
	TableColumn<RegisterClass, String> registerSpalteE;
	TableColumn<RegisterClass, String> registerSpalteF;
	
// Deklaration der TableView zum anzeigen der LST-Datei
	
	@FXML
	public TableView<TextClass> tableViewText; 
	
	TableColumn<TextClass, String> col0;
	TableColumn<TextClass, String> col1;
	TableColumn<TextClass, String> col2;
	TableColumn<TextClass, String> col3;
	CodeReader codeReader = new CodeReader();
	ObservableList<TextClass> data = FXCollections.observableArrayList();
			
//Radio-Buttons
	//Hardwareanbindung
	
	@FXML
	private RadioButton btnAux;
	
	//Geschwindigkeit	
	@FXML
	private RadioButton btnSchnell;
	@FXML
	private RadioButton btnMittel;
	@FXML
	private RadioButton btnLangsam;
	
	//Ports
	@FXML
	private RadioButton btnPortA0;
	@FXML
	private RadioButton btnPortA1;
	@FXML
	private RadioButton btnPortA2;
	@FXML
	private RadioButton btnPortA3;
	@FXML
	private RadioButton btnPortA4;
	@FXML
	private RadioButton btnPortB0;
	@FXML
	private RadioButton btnPortB1;
	@FXML
	private RadioButton btnPortB2;
	@FXML
	private RadioButton btnPortB3;
	@FXML
	private RadioButton btnPortB4;
	@FXML
	private RadioButton btnPortB5;
	@FXML
	private RadioButton btnPortB6;
	@FXML
	private RadioButton btnPortB7;
	@FXML
	private RadioButton btnExTakt;
	@FXML
	private Button btnInsert;
	
//Choice-Box
	@FXML
	private ChoiceBox<String> boxAux;
	@FXML
	private ChoiceBox<String> boxExTakt;
	
//Text-Felder
	
	@FXML
	private TextField txt_wRegister;
	
	@FXML
	private TextField txtQuarzfrequenz;
	
	@FXML
	private TextField txtExTakt;
	
	@FXML
	private Text txtLaufzeit;
	
//Variablen
	private Task<Integer> taskRun;
	private Task<Integer> taskRefreshPorts;
	private Thread th;
	private Thread thPorts;
	private Window stage;
	private int focusLine = 0;
	private int textLine=0;
	private boolean runable = true; //Nur wenn diese Variable true ist kann runProgram() gestartet werden
	private boolean auxStop = true;
	private int speed=100;
	private DecimalFormat df = new DecimalFormat("#.## µs");
	private double laufzeit=0;
	private int alterWertCycles=0;
	private int alterWertCyclesExtTakt=0;
	private double quarzfrequenz=0.5;
	private double cycleDauer=(1/quarzfrequenz)*4; //in µs
	private double extTakt = 10;
	private double extTaktDauer=(1/extTakt)*1000/2; //in µs
	private double extTaktTimer=0;					//in µs
	private double timeOnTakt=0;
	private boolean taktOn=false;
	private int taktPort=-1;
	
	
//Methode zur Initialisierung der Register- und Stackt-Tabelle	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
	//ChoiceBox für Hardwareansteuerung initialiseiren
		
		boxAux.getItems().addAll(AuxPort.getAllPorts());
		if(AuxPort.getAllPorts().isEmpty()){
			btnAux.setDisable(true);
			boxAux.setDisable(true);
		}else boxAux.setValue(AuxPort.getAllPorts().get(0));
		
		
	//ChoiceBox für Hardwareansteuerung initialiseiren
		
		boxExTakt.getItems().addAll(
				"RA0",
				"RA1",
				"RA2",
				"RA3",
				"RA4",
				"RB0",
				"RB1",
				"RB2",
				"RB3",
				"RB4",
				"RB5",
				"RB6",
				"RB7");
		boxExTakt.setValue("RA0");
	
	
	//Geschwindigkeit auf mittel stellen
		
		btnMittel.selectedProperty().set(true);		
		
	//Register initialisieren
		
		codeReader.initRegister();
		codeReader.resetRegister();
	
	//Stackspalten definieren
		 	
		 	colBeschriftung = new TableColumn<StackClass,String>("");
		 	colBeschriftung.setCellValueFactory(new PropertyValueFactory<StackClass, String>("beschriftung"));
		 	colBeschriftung.setStyle("-fx-font-weight: bold; -fx-background-color: #DFDFDF;-fx-alignment: center;");
		 	colBeschriftung.setMaxWidth(24);
		 	colBeschriftung.setMinWidth(24);
		 	
		 	
		 	colStack = new TableColumn<StackClass,String>("Stack");
		 	colStack.setCellValueFactory(new PropertyValueFactory<StackClass, String>("stack"));
		 	colStack.setStyle("-fx-alignment: center;");
		 	colStack.setMaxWidth(36);
		 	colStack.setMinWidth(36);
		 			 	
		 	
	//Stacktabelle erzeugen
		 	
		 	tableViewStack.setItems(codeReader.getDataStack());
		 	tableViewStack.getColumns().add(colBeschriftung);
		 	tableViewStack.getColumns().add(colStack);	
		 	
		 	tableViewStack.getSelectionModel().select(codeReader.getStackpointer());
		 			 	
	//Registerspalten definieren
		 	
		 	registerBeschriftung = new TableColumn<RegisterClass, String>("");        
	        registerBeschriftung.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("beschriftung"));
	        registerBeschriftung.setStyle("-fx-font-weight: bold; -fx-background-color: #DFDFDF; -fx-alignment: center;");
	        registerBeschriftung.setMaxWidth(26);
	        registerBeschriftung.setMinWidth(26);
	        
	        registerSpalte0 = new TableColumn<RegisterClass, String>("00");     
	        registerSpalte0.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte0"));
	        registerSpalte0.setStyle("-fx-alignment: center;");
	        registerSpalte0.setMaxWidth(34);
	        registerSpalte0.setMinWidth(34);
	        registerSpalte0.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte0.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					//Wert einlesen
	       				  	int x = 0;
	       				  	int wert = Integer.valueOf(t.getNewValue(),16);
	       				  	int y = t.getTablePosition().getRow();
	       				  	codeReader.setRegister(x,y,wert);

	       				  	//View refreshen
	       				  	refreshView();
	        			}else{
	        				refreshView();
		       			}
	                }
	            }
	        );
	        
	        registerSpalte1 = new TableColumn<RegisterClass, String>("01");        
	        registerSpalte1.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte1"));
	        registerSpalte1.setStyle("-fx-alignment: center;");
	        registerSpalte1.setMaxWidth(34);
	        registerSpalte1.setMinWidth(34);
	        registerSpalte1.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte1.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        			//Wert einlesen
	        			int x = 1;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        
	        registerSpalte2 = new TableColumn<RegisterClass, String>("02");        
	        registerSpalte2.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte2"));
	        registerSpalte2.setStyle("-fx-alignment: center;");
	        registerSpalte2.setMaxWidth(34);
	        registerSpalte2.setMinWidth(34);
	        registerSpalte2.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte2.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 2;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        
	        
	        registerSpalte3 = new TableColumn<RegisterClass, String>("03");        
	        registerSpalte3.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte3"));
	        registerSpalte3.setStyle("-fx-alignment: center;");
	        registerSpalte3.setMaxWidth(34);
	        registerSpalte3.setMinWidth(34);
	        registerSpalte3.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte3.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 3;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}      		
	                }
	            }
	        );
	        
	        
	        registerSpalte4 = new TableColumn<RegisterClass, String>("04");        
	        registerSpalte4.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte4"));
	        registerSpalte4.setStyle("-fx-alignment: center;");
	        registerSpalte4.setMaxWidth(34);
	        registerSpalte4.setMinWidth(34);
	        registerSpalte4.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte4.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 4;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalte5 = new TableColumn<RegisterClass, String>("05");        
	        registerSpalte5.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte5"));
	        registerSpalte5.setStyle("-fx-alignment: center;");
	        registerSpalte5.setMaxWidth(34);
	        registerSpalte5.setMinWidth(34);
	        registerSpalte5.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte5.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	      
	        			//Wert einlesen
	        			int x = 5;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalte6 = new TableColumn<RegisterClass, String>("06");        
	        registerSpalte6.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte6"));
	        registerSpalte6.setStyle("-fx-alignment: center;");
	        registerSpalte6.setMaxWidth(34);
	        registerSpalte6.setMinWidth(34);
	        registerSpalte6.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte6.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        
	        			//Wert einlesen
	        			int x = 6;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalte7 = new TableColumn<RegisterClass, String>("07");        
	        registerSpalte7.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte7"));
	        registerSpalte7.setStyle("-fx-alignment: center;");
	        registerSpalte7.setMaxWidth(34);
	        registerSpalte7.setMinWidth(34);
	        registerSpalte7.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte7.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 7;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalte8 = new TableColumn<RegisterClass, String>("08");        
	        registerSpalte8.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte8"));
	        registerSpalte8.setStyle("-fx-alignment: center;");
	        registerSpalte8.setMaxWidth(34);
	        registerSpalte8.setMinWidth(34);
	        registerSpalte8.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte8.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 8;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        
	        registerSpalte9 = new TableColumn<RegisterClass, String>("09");        
	        registerSpalte9.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte9"));
	        registerSpalte9.setStyle("-fx-alignment: center;");
	        registerSpalte9.setMaxWidth(34);
	        registerSpalte9.setMinWidth(34);
	        registerSpalte9.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalte9.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 9;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}       		
	                }
	            }
	        );
	        
	        registerSpalteA = new TableColumn<RegisterClass, String>("0A");        
	        registerSpalteA.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteA"));
	        registerSpalteA.setStyle("-fx-alignment: center;");
	        registerSpalteA.setMaxWidth(34);
	        registerSpalteA.setMinWidth(34);
	        registerSpalteA.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteA.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 10;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalteB = new TableColumn<RegisterClass, String>("0B");        
	        registerSpalteB.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteB"));
	        registerSpalteB.setStyle("-fx-alignment: center;");
	        registerSpalteB.setMaxWidth(34);
	        registerSpalteB.setMinWidth(34);
	        registerSpalteB.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteB.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 11;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        
	        registerSpalteC = new TableColumn<RegisterClass, String>("0C");        
	        registerSpalteC.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteC"));
	        registerSpalteC.setStyle("-fx-alignment: center;");
	        registerSpalteC.setMaxWidth(34);
	        registerSpalteC.setMinWidth(34);
	        registerSpalteC.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteC.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 12;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalteD = new TableColumn<RegisterClass, String>("0D");        
	        registerSpalteD.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteD"));
	        registerSpalteD.setStyle("-fx-alignment: center;");
	        registerSpalteD.setMaxWidth(34);
	        registerSpalteD.setMinWidth(34);
	        registerSpalteD.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteD.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 13;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        
	        registerSpalteE = new TableColumn<RegisterClass, String>("0E");        
	        registerSpalteE.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteE"));
	        registerSpalteE.setStyle("-fx-alignment: center;");
	        registerSpalteE.setMaxWidth(34);
	        registerSpalteE.setMinWidth(34);
	        registerSpalteE.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteE.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        		
	        			//Wert einlesen
	        			int x = 14;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}        		
	                }
	            }
	        );
	        
	        registerSpalteF = new TableColumn<RegisterClass, String>("0F");        
	        registerSpalteF.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteF"));
	        registerSpalteF.setStyle("-fx-alignment: center;");
	        registerSpalteF.setMaxWidth(34);
	        registerSpalteF.setMinWidth(34);
	        registerSpalteF.setCellFactory(TextFieldTableCell.forTableColumn());
	        registerSpalteF.setOnEditCommit( 
	        	new EventHandler<CellEditEvent<RegisterClass, String>>() {
	        		@Override
	        		public void handle(CellEditEvent<RegisterClass, String> t) {
	        			if(t.getNewValue().matches("[0-9a-fA-F]{1,2}")){
	       					
	        	
	        			//Wert einlesen
	        			int x = 15;
	        			int wert = Integer.valueOf(t.getNewValue(),16);
	        			int y = t.getTablePosition().getRow();
	        			codeReader.setRegister(x,y,wert);
	        			System.out.println();
	        		  	refreshView();
	        			}else{
	        				refreshView();
		       			}	        		
	                }
	            }
	        );
	        

	    // Tabellenerzeugung  Register
	        tableViewRegister.setItems(codeReader.getDataRegister());
			tableViewRegister.getColumns().add(registerBeschriftung);
	        tableViewRegister.getColumns().add(registerSpalte0);
	        tableViewRegister.getColumns().add(registerSpalte1);
	        tableViewRegister.getColumns().add(registerSpalte2);
	        tableViewRegister.getColumns().add(registerSpalte3);
	        tableViewRegister.getColumns().add(registerSpalte4);
	        tableViewRegister.getColumns().add(registerSpalte5);
	        tableViewRegister.getColumns().add(registerSpalte6);
	        tableViewRegister.getColumns().add(registerSpalte7);
	        tableViewRegister.getColumns().add(registerSpalte8);
	        tableViewRegister.getColumns().add(registerSpalte9);
	        tableViewRegister.getColumns().add(registerSpalteA);
	        tableViewRegister.getColumns().add(registerSpalteB);
	        tableViewRegister.getColumns().add(registerSpalteC);
	        tableViewRegister.getColumns().add(registerSpalteD);
	        tableViewRegister.getColumns().add(registerSpalteE);
	        tableViewRegister.getColumns().add(registerSpalteF);
	        
	        refreshView();
	        		
	}

//Beim Klicken auf Start, startet programm
	public void StartProgramm(ActionEvent event){		
		
		if(!data.isEmpty()){//Nur wenn bereits ein Program geladen wurde 
		
		if(runable){
		runable=false;
		taskRun = new Task<Integer>() {
		    @Override protected Integer call() throws Exception {
		    	RunProgram(false, speed);		   
		    	
		        return textLine;
		    }
		    
		};
		
		th = new Thread(taskRun);
		th.setDaemon(true);
		th.start();			
		}
	}
	}

//Beim Klicken auf Steps wird genau eine Operation ausgeführt
	public void Steps(ActionEvent event)  {
        
		if(!data.isEmpty()){//Nur wenn bereits ein Program geladen wurde 
		
		if(runable){
		runable=false;
		taskRun = new Task<Integer>() {
		    @Override protected Integer call() throws Exception {
		    	RunProgram(true, 10);		   
		    	
		        return textLine;
		    }
		    
		};
		
		th = new Thread(taskRun);
		th.setDaemon(true);
		th.start();
		}
		}		
	}

//Beim Klicken auf Stop wird das laufende Programm angehalten
	
	public void StopProgramm(ActionEvent event){
		runable=true;
	}

//Beim Klicken kann eine Datei ausgewählt werden und der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){
//Kann nur ausgeführt werden, wenn Program nicht gestartet
		
	if(runable){
//vorherige Tabelleninhalte löschen		
		tableViewText.getColumns().clear();	

//Data löschen
		data.clear();

//Reset der Register
		textLine = 0;
		codeReader.resetRegister();
		

//Öffnen eines Fenster zum auswählen einer Datei
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Öffne Datei");
		
		// Filtern nach LST-Dateien
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("LST-Files", "*.LST");
        fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(stage);
        		
		if (!file.canRead() || !file.isFile())
            System.exit(0);

//Zeilenweises einlesen der Datei und abspeichern in Liste
            BufferedReader in = null;
        try {        	
            
        	in = new BufferedReader(new FileReader(file));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                data.add(new TextClass(zeile.substring(0, 5).trim(),zeile.substring(5,9).trim(),zeile.substring(9).trim()));
            }
           
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }  
 // Spaltendefinition
        
        col0 = new TableColumn<TextClass, String>("BP");        
        col0.setCellValueFactory(new PropertyValueFactory<TextClass, String>("column0"));
        col0.setMinWidth(22);
        col0.setMaxWidth(22);
        
        col1 = new TableColumn<TextClass, String>("PC");        
        col1.setCellValueFactory(new PropertyValueFactory<TextClass, String>("column1"));
        col1.setEditable(false);
        col1.setSortable(false);
        col1.setMinWidth(40);
        col1.setMaxWidth(40);
        
        col2 = new TableColumn<TextClass, String>("Code");
        col2.setCellValueFactory(new PropertyValueFactory<TextClass, String>("column2"));
        col2.setEditable(false);
        col2.setSortable(false);
        col2.setMinWidth(40);
        col2.setMaxWidth(40);
        
        col3 = new TableColumn<TextClass, String>("Text");
        col3.setCellValueFactory(new PropertyValueFactory<TextClass, String>("column3"));
        col3.setEditable(false);
        col3.setSortable(false);
        col3.setMinWidth(40);

// Tabellenerzeugung
                
        tableViewText.setItems(data);
        tableViewText.getColumns().add(col0);
        tableViewText.getColumns().add(col1);
        tableViewText.getColumns().add(col2);
        tableViewText.getColumns().add(col3);    
        
// View refreshen
       //springe an Stelle pc=0;
        while(true){
        	if(!col1.getCellData(textLine).equals("")){
				if(Integer.parseInt(col1.getCellData(textLine),16)==0)break;
        	}
        	textLine++;
        }
      //FocusLine berechnen 						
		if(!(textLine>=focusLine 
				&& textLine<= focusLine + 12)) focusLine = textLine-6;
			
        tableViewText.getSelectionModel().select(textLine);
		refreshView();
	}	
	}

//Beim Klicken auf Reset werden alle Register zurückgesetzt
	public void Reset(ActionEvent event){
		
		if(runable){//Kann nur ausgeführt werden, wenn Program gestoppt ist		
		//Reset der Register
		textLine = 0;
		codeReader.resetRegister();
		// View refreshen
		//FocusLine berechnen 						
		if(!(textLine>=focusLine 
				&& textLine<= focusLine + 12)) focusLine = textLine-6;
			
        tableViewText.getSelectionModel().select(textLine);
		refreshView();
		}
	}

//Beim Klicken wird die Laufzeit zurückgesetzt
	public void actZurücksetzen(ActionEvent event){
		laufzeit=0;	
		refreshView();
	}

//Methode, die das eingelesene Programm ausführt
	public void RunProgram(boolean steps, int geschwindigkeit){
		 while(true){			 	
			 
			 	if(!col1.getCellData(textLine).equals("")){
					if(Integer.parseInt(col1.getCellData(textLine),16)==codeReader.getPc()){
						
						 //Highlight
						tableViewText.getSelectionModel().select(textLine);
						
						//Breakpoint abfragen 						
						if (data.get(textLine).getColumn0().
							selectedProperty().get()){
							runable = true;
						}
						
						//FocusLine berechnen 						
						if(!(textLine>=focusLine 
								&& textLine<= focusLine + 12)) focusLine = textLine-6;
						
						//View refresh
						Platform.runLater(new Runnable() {
		                     @Override public void run() {
		                         refreshView();  
		                        
		                     }
		                 });
												
						/* Aussprungpunkt:
						 * Abfrage ob Program gestopt wurde, 
						 * 1  durch drücken auf Stop-Button,
						 * 2. durch setzen eines Breakpoints,
						 * 3. wenn Step ausgeführt werden soll.
						 */
						
						if(runable){
							break;
						}
						
						//Sleep Funktion um Geschwindigkeit zu regeln
						
						try {
							Thread.sleep(geschwindigkeit);							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//Code an codeReader übergeben und Rückgabewert auswerten 
						
						int rueck = codeReader.read(col2.getCellData(textLine));
						
						if (rueck==0){			//Alles in Butter
							
						}			
						else if (rueck==1) {	//Code nicht bekannt
							System.out.println("Code nicht bekannt");
							break;
						} else if (rueck==2) {	//Sleep
							break;
						} else if (rueck==3) {	//Sprung
							
						}
												
						// Abfragen, ob nur ein Schritt ausgeführt werden soll.
						if(steps){
							textLine = 0;
							runable = true;
						}else{
						/*Text wieder ab Zeile 0 durchsuchen:  
						 * deshalb auf -1 setzen, da textLine anschließend noch um 1 erhöht wird.
						 */
						textLine = -1;
						}
					}	
				}
				textLine++;				
				if(col1.getCellData(textLine)==null)break;
				
			}
		 textLine=0;
		 
	}
	
//Methode, die nach jeder Änderung aufgerufen wird, um die View zu aktualisieren
 	public void refreshView(){
 	
 	//Hardwareansteuerung
 		if(!AuxPort.getAllPorts().isEmpty()){
 			btnAux.setDisable(!runable); 		
 			if(btnAux.selectedProperty().get())codeReader.refreshAuxPort();
 		}
 		
 	//Frequenzgenerator
 		if(taktOn){
 			timeOnTakt+=(codeReader.getCycles()-alterWertCyclesExtTakt)*cycleDauer;
 			
 			if(timeOnTakt>extTaktTimer){
 				extTaktTimer+=extTaktDauer;
 				refreshExTakt();
 			}
 		}
 		
 		alterWertCyclesExtTakt=codeReader.getCycles();
 		txtExTakt.setText(String.valueOf(extTakt));
 	
 	//Quarzfrequnenz 		
 		txtQuarzfrequenz.setDisable(!runable);
 		txtQuarzfrequenz.setText(String.valueOf(quarzfrequenz));
		
 		
 	//Laufzeit
 		laufzeit+=(codeReader.getCycles()-alterWertCycles)*cycleDauer;
		alterWertCycles=codeReader.getCycles();
 		txtLaufzeit.setText(df.format(laufzeit));
 	
 	//Portvisualisierung
 		refreshPortView(); 		
 		
 	//Register
 		tableViewRegister.setEditable(runable);
 		registerBeschriftung.setVisible(false);
 		registerBeschriftung.setVisible(true);
 		
    //W-Register
        txt_wRegister.setText(codeReader.getwRegister());
        txt_wRegister.setDisable(!runable);
    //Zu aktueller Zeile scrollen
		tableViewText.scrollTo(focusLine);
		
	//Stack
		colBeschriftung.setVisible(false);
		colBeschriftung.setVisible(true);
		tableViewStack.getSelectionModel().select(codeReader.getStackpointer());	
	}

 /*
 * Methode, die die Visualisirung der Ports aktualisiert
 */
 	
 	public void refreshPortView(){		
			/*
			 * Wenn Hardwareangebunden, dann sperre Eingabe über die Portvisualisierung
			 * und zeige die Werte aus Register an
			 */
		if(btnAux.isSelected()){
				btnPortA0.setDisable(true);
				if(codeReader.bitTest(5, 0, 0))btnPortA0.selectedProperty().set(true);
				else btnPortA0.selectedProperty().set(false);
				
				btnPortA1.setDisable(true);
				if(codeReader.bitTest(5, 0, 1))btnPortA1.selectedProperty().set(true);
				else btnPortA1.selectedProperty().set(false);
				
				btnPortA2.setDisable(true);
				if(codeReader.bitTest(5, 0, 2))btnPortA2.selectedProperty().set(true);
				else btnPortA2.selectedProperty().set(false);
								
				btnPortA3.setDisable(true);
				if(codeReader.bitTest(5, 0, 3))btnPortA3.selectedProperty().set(true);
				else btnPortA3.selectedProperty().set(false);
				
				btnPortA4.setDisable(true);
				if(codeReader.bitTest(5, 0, 4))btnPortA4.selectedProperty().set(true);
				else btnPortA4.selectedProperty().set(false);
				
				btnPortB0.setDisable(true);
				if(codeReader.bitTest(6, 0, 0))btnPortB0.selectedProperty().set(true);
				else btnPortB0.selectedProperty().set(false);
				
				btnPortB1.setDisable(true);
				if(codeReader.bitTest(6, 0, 1))btnPortB1.selectedProperty().set(true);
				else btnPortB1.selectedProperty().set(false);
				
				btnPortB2.setDisable(true);
				if(codeReader.bitTest(6, 0, 2))btnPortB2.selectedProperty().set(true);
				else btnPortB2.selectedProperty().set(false);
								
				btnPortB3.setDisable(true);
				if(codeReader.bitTest(6, 0, 3))btnPortB3.selectedProperty().set(true);
				else btnPortB3.selectedProperty().set(false);
				
				btnPortB4.setDisable(true);
				if(codeReader.bitTest(6, 0, 4))btnPortB4.selectedProperty().set(true);
				else btnPortB4.selectedProperty().set(false);
				
				btnPortB5.setDisable(true);
				if(codeReader.bitTest(6, 0, 5))btnPortB5.selectedProperty().set(true);
				else btnPortB5.selectedProperty().set(false);				
				
				btnPortB6.setDisable(true);
				if(codeReader.bitTest(6, 0, 6))btnPortB6.selectedProperty().set(true);
				else btnPortB6.selectedProperty().set(false);
								
				btnPortB7.setDisable(true);
				if(codeReader.bitTest(6, 0, 7))btnPortB7.selectedProperty().set(true);
				else btnPortB7.selectedProperty().set(false);
				
		}else{
				/*
				 * Wenn keine Hardwareanbindung, können Eingangs-Ports nur über die Port-Visualisierung 
				 * bearbeitet werden und Ausgangsports nur über das Programm. 
				 * 
				 */
		//PortA
			if(!codeReader.bitTest(5, 8, 0)||taktPort==0){//Wenn Pin ein Ausgang, übergib Register-Wert an Visualisieurung		
				if(codeReader.bitTest(5, 0, 0))btnPortA0.selectedProperty().set(true);
				else btnPortA0.selectedProperty().set(false);
				//...und sperre den Button
				btnPortA0.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortA0.selectedProperty().get())codeReader.setBit(5, 0, 0);
				else codeReader.clearBit(5, 0, 0);
				//...und gebe Button frei
				btnPortA0.setDisable(false);
			}
			
			if(!codeReader.bitTest(5, 8, 1)||taktPort==1){//Wenn Pin ein Ausgang, übergib Register-Wert an Visualisieurung
				if(codeReader.bitTest(5, 0, 1))btnPortA1.selectedProperty().set(true);
				else btnPortA1.selectedProperty().set(false);
				//...und sperre den Button
				btnPortA1.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortA1.selectedProperty().get())codeReader.setBit(5, 0, 1);
				else codeReader.clearBit(5, 0, 1);
				//...und gebe Button frei
				btnPortA1.setDisable(false);
			}
			
			if(!codeReader.bitTest(5, 8, 2)||taktPort==2){//Wenn Pin ein Ausgang, übergib Register-Wert an Visualisieurung
				if(codeReader.bitTest(5, 0, 2))btnPortA2.selectedProperty().set(true);
				else btnPortA2.selectedProperty().set(false);
				//...und sperre den Button
				btnPortA2.setDisable(true);		
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortA2.selectedProperty().get())codeReader.setBit(5, 0, 2);
				else codeReader.clearBit(5, 0, 2);
				//...und gebe Button frei
				btnPortA2.setDisable(false);
			}
			
			if(!codeReader.bitTest(5, 8, 3)||taktPort==3){//Wenn Pin ein Ausgang, übergib Register-Wert an Visualisieurung
				if(codeReader.bitTest(5, 0, 3))btnPortA3.selectedProperty().set(true);
				else btnPortA3.selectedProperty().set(false);
				//...und sperre den Button
				btnPortA3.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortA3.selectedProperty().get())codeReader.setBit(5, 0, 3);
				else codeReader.clearBit(5, 0, 3);
				//...und gebe Button frei
				btnPortA3.setDisable(false);
			}
			
			if(!codeReader.bitTest(5, 8, 4)||taktPort==4){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(5, 0, 4))btnPortA4.selectedProperty().set(true);
				else btnPortA4.selectedProperty().set(false);
				//...und sperre den Button
				btnPortA4.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortA4.selectedProperty().get())codeReader.setBit(5, 0, 4);
				else codeReader.clearBit(5, 0, 4);
				//...und gebe Button frei
				btnPortA4.setDisable(false);
			}
			
		//Port B
			if(!codeReader.bitTest(6, 8, 0)||taktPort==10){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 0))btnPortB0.selectedProperty().set(true);
				else btnPortB0.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB0.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB0.selectedProperty().get())codeReader.setBit(6, 0, 0);
				else codeReader.clearBit(6, 0, 0);
				//...und gebe Button frei
				btnPortB0.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 1)||taktPort==11){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 1))btnPortB1.selectedProperty().set(true);
				else btnPortB1.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB1.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB1.selectedProperty().get())codeReader.setBit(6, 0, 1);
				else codeReader.clearBit(6, 0, 1);
				//...und gebe Button frei
				btnPortB1.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 2)||taktPort==12){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 2))btnPortB2.selectedProperty().set(true);
				else btnPortB2.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB2.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB2.selectedProperty().get())codeReader.setBit(6, 0, 2);
				else codeReader.clearBit(6, 0, 2);
				//...und gebe Button frei
				btnPortB2.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 3)||taktPort==13){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 3))btnPortB3.selectedProperty().set(true);
				else btnPortB3.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB3.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB3.selectedProperty().get())codeReader.setBit(6, 0, 3);
				else codeReader.clearBit(6, 0, 3);
				//...und gebe Button frei
				btnPortB3.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 4)||taktPort==14){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 4))btnPortB4.selectedProperty().set(true);
				else btnPortB4.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB4.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB4.selectedProperty().get())codeReader.setBit(6, 0, 4);
				else codeReader.clearBit(6, 0, 4);
				//...und gebe Button frei
				btnPortB4.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 5)||taktPort==15){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 5))btnPortB5.selectedProperty().set(true);
				else btnPortB5.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB5.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB5.selectedProperty().get())codeReader.setBit(6, 0, 5);
				else codeReader.clearBit(6, 0, 5);
				//...und gebe Button frei
				btnPortB5.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 6)||taktPort==16){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 6))btnPortB6.selectedProperty().set(true);
				else btnPortB6.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB6.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB6.selectedProperty().get())codeReader.setBit(6, 0, 6);
				else codeReader.clearBit(6, 0, 6);
				//...und gebe Button frei
				btnPortB6.setDisable(false);
			}
			
			if(!codeReader.bitTest(6, 8, 7)||taktPort==17){//Wenn Pin ein Ausgang, übergib Wert an Visualisieurung
				if(codeReader.bitTest(6, 0, 7))btnPortB7.selectedProperty().set(true);
				else btnPortB7.selectedProperty().set(false);
				//...und sperre den Button
				btnPortB7.setDisable(true);
			}else{ //Wenn Pin ein Eingang, übergib Wert der Visualisierung an Register
				if(btnPortB7.selectedProperty().get())codeReader.setBit(6, 0, 7);
				else codeReader.clearBit(6, 0, 7);
				//...und gebe Button frei
				btnPortB7.setDisable(false);
			}
		}
 	}
 	
 	public void refreshExTakt(){
 		
			if ((taktPort==0) && codeReader.bitTest(5,8,0)){
				if(codeReader.bitTest(5, 0, 0))codeReader.clearBit(5, 0, 0);
				else codeReader.setBit(5, 0, 0);
			}else if ((taktPort==1) && codeReader.bitTest(5,8,1)){
				if(codeReader.bitTest(5, 0, 1))codeReader.clearBit(5, 0, 1);
				else codeReader.setBit(5, 0, 1);
			}else if ((taktPort==2) && codeReader.bitTest(5,8,2)){
				if(codeReader.bitTest(5, 0, 2))codeReader.clearBit(5, 0, 2);
				else codeReader.setBit(5, 0, 2);
			}else if ((taktPort==3) && codeReader.bitTest(5,8,3)){
				if(codeReader.bitTest(5, 0, 3))codeReader.clearBit(5, 0, 3);
				else codeReader.setBit(5, 0, 3);
			}else if ((taktPort==4) && codeReader.bitTest(5,8,4)){
				if(codeReader.bitTest(5, 0, 4))codeReader.clearBit(5, 0, 4);
				else codeReader.setBit(5, 0, 4);
			}
			 else if ((taktPort==10) && codeReader.bitTest(6,8,0)){
				if(codeReader.bitTest(6, 0, 0))codeReader.clearBit(6, 0, 0);
				else codeReader.setBit(6, 0, 0);
			}else if ((taktPort==11) && codeReader.bitTest(6,8,1)){
				if(codeReader.bitTest(6, 0, 1))codeReader.clearBit(6, 0, 1);
				else codeReader.setBit(6, 0, 1);
			}else if ((taktPort==12) && codeReader.bitTest(6,8,2)){
				if(codeReader.bitTest(6, 0, 2))codeReader.clearBit(6, 0, 2);
				else codeReader.setBit(6, 0, 2);
			}else if ((taktPort==13) && codeReader.bitTest(6,8,3)){
				if(codeReader.bitTest(6, 0, 3))codeReader.clearBit(6, 0, 3);
				else codeReader.setBit(6, 0, 3);
			}else if ((taktPort==14) && codeReader.bitTest(6,8,4)){
				if(codeReader.bitTest(6, 0, 4))codeReader.clearBit(6, 0, 4);
				else codeReader.setBit(6, 0, 4);
			}else if ((taktPort==15) && codeReader.bitTest(6,8,5)){
				if(codeReader.bitTest(6, 0, 5))codeReader.clearBit(6, 0, 5);
				else codeReader.setBit(6, 0, 5);
			}else if ((taktPort==16) && codeReader.bitTest(6,8,6)){
				if(codeReader.bitTest(6, 0, 6))codeReader.clearBit(6, 0, 6);
				else codeReader.setBit(6, 0, 6);
			}else if ((taktPort==17) && codeReader.bitTest(6,8,7)){
				if(codeReader.bitTest(6, 0, 7))codeReader.clearBit(6, 0, 7);
				else codeReader.setBit(6, 0, 7);
			} 		
 		
 	}
 	
 	
 /*
  * Methoden, die beim Klicken auf die Radio-Buttons aufgerufen werden
 */
 	
 	public void actAux(){
  			if(btnAux.isSelected()){
  			if(!(boxAux.getValue()==null)){
 				boxAux.setDisable(true);
 				btnExTakt.setDisable(true);
 				auxStop=false;
 				codeReader.connectAuxPort(boxAux.getValue()); 
  					taskRefreshPorts = new Task<Integer>() {
 					    @Override protected Integer call() throws Exception {
 					    	while(true){
 					    	if(!runable||auxStop)break;
 					    	try {
 								Thread.sleep(500);							
 							} catch (InterruptedException e) {
 								// TODO Auto-generated catch block
 								e.printStackTrace();
 							} 					    	
 					    	//View refresh
 							Platform.runLater(new Runnable() {
 			                     @Override public void run() {
 			                         refreshView();  
 			                        
 			                     }
 			                 });
 					    	}
 					        return 0; 					     
 					    } 					    
  					};
 					
 					thPorts = new Thread(taskRefreshPorts);
 					thPorts.setDaemon(true);
 					thPorts.start();	
  		}else btnAux.selectedProperty().set(false);
 					
 			}else {
 				auxStop=true;
 				boxAux.setDisable(false);
 				btnExTakt.setDisable(false);
 				codeReader.closeAuxPort();
 				refreshView();
 			}
 	}
 	
 	public void actSchnell(){
 		//Erneutes Anklicken ->Button bleibt aktiv
 		if(!btnSchnell.isSelected()){ 
 			btnSchnell.selectedProperty().set(true);
 		}else{//Wenn nicht bereits ausgewählt
 			if (runable){//Nur wenn Programm gestoppt
 				//Setze die anderen Buttons auf false
 				btnMittel.selectedProperty().set(false);
 				btnLangsam.selectedProperty().set(false);
 				//Setze neue Geschwindigkeit
 				speed=50;
 				
 			}else{//Wenn Program läuft, kann Button nicht aktiviert werden
 				btnSchnell.selectedProperty().set(false);
 			}
 			
 		}
 	}
 	
 	public void actMittel(){
 		//Erneutes Anklicken ->Button bleibt aktiv
 		if(!btnMittel.isSelected()){ 
 			btnMittel.selectedProperty().set(true);
 		}else{//Wenn nicht bereits ausgewählt
 			if (runable){//Nur wenn Programm gestoppt
 				//Setze die anderen Buttons auf false
 				btnSchnell.selectedProperty().set(false);
 				btnLangsam.selectedProperty().set(false);
 				//Setze neue Geschwindigkeit
 				speed=100;
 				
 			}else{//Wenn Program läuft, kann Button nicht aktiviert werden
 				btnMittel.selectedProperty().set(false);
 			}
 			
 		}
 	}
 	
 	public void actLangsam(){
 		//Erneutes Anklicken ->Button bleibt aktiv
 		if(!btnLangsam.isSelected()){ 
 			btnLangsam.selectedProperty().set(true);
 		}else{//Wenn nicht bereits ausgewählt
 			if (runable){//Nur wenn Programm gestoppt
 				//Setze die anderen Buttons auf false
 				btnSchnell.selectedProperty().set(false);
 				btnMittel.selectedProperty().set(false);
 				//Setze neue Geschwindigkeit
 				speed=200;
 				
 			}else{//Wenn Program läuft, kann Button nicht aktiviert werden
 				btnLangsam.selectedProperty().set(false);
 			}
 			
 		}
 	}
 	
//Ports
 	
 	public void actPorts(){ 		
 		refreshView(); 		
 	}	

//Hilfefunktion
 	
  	
 	   public void actHilfe() throws Exception {
 	     Process p = 
 	         Runtime.getRuntime()
 	           .exec("rundll32 url.dll,FileProtocolHandler Datenblatt 16F84.pdf");
 	     p.waitFor(); 	    
 	     }
 	   
 	   
 	  public void actWregister(KeyEvent event){
 		  if(event.getCode()==KeyCode.ENTER){
 			  if(txt_wRegister.getText().matches("[0-9a-fA-F]{1,2}")){
 				  codeReader.setwRegister(Integer.valueOf(txt_wRegister.getText(),16));
 				  refreshView();
 			  }else{
 				  refreshView();
 			  }
 		  }
 	  }
 	  
 	 public void actQuarzfrequenz(KeyEvent event){
		  if(event.getCode()==KeyCode.ENTER){
			 
			  if(txtQuarzfrequenz.getText().matches("[0-9]{1,4}\\.{0,1}[0-9]{0,8}")){				 
				  quarzfrequenz=Double.valueOf(txtQuarzfrequenz.getText());
				  cycleDauer=(1/quarzfrequenz)*4;
				  refreshView();
			  }else{
				  refreshView();				  
			  }
		  }
	  }
 	 
 	 public void actTxtExTakt(KeyEvent event){
 		 if(event.getCode()==KeyCode.ENTER){
			 
			  if(txtExTakt.getText().matches("[0-9]{1,4}\\.{0,1}[0-9]{0,4}")){				 
				  extTakt=Double.valueOf(txtExTakt.getText());
				  extTaktDauer=(1/extTakt)*1000/2;
				  refreshView();
			  }else{
				  refreshView();				  
			  }
		  }
 	 }
 	 
 	 public void actBtnExTakt(){
 		if(btnExTakt.isSelected()){
				taktOn=true;
				extTaktTimer=0;
				timeOnTakt=0;
				
				if(boxExTakt.getValue().equals("RA0"))taktPort=0;
				else if(boxExTakt.getValue().equals("RA1"))taktPort=1;
				else if(boxExTakt.getValue().equals("RA2"))taktPort=2;
				else if(boxExTakt.getValue().equals("RA3"))taktPort=3;
				else if(boxExTakt.getValue().equals("RA4"))taktPort=4;
				
				else if(boxExTakt.getValue().equals("RB0"))taktPort=10;
				else if(boxExTakt.getValue().equals("RB1"))taktPort=11;
				else if(boxExTakt.getValue().equals("RB2"))taktPort=12;
				else if(boxExTakt.getValue().equals("RB3"))taktPort=13;
				else if(boxExTakt.getValue().equals("RB4"))taktPort=14;
				else if(boxExTakt.getValue().equals("RB5"))taktPort=15;
				else if(boxExTakt.getValue().equals("RB6"))taktPort=16;
				else if(boxExTakt.getValue().equals("RB7"))taktPort=17;
 			
 				boxExTakt.setDisable(true);
				txtExTakt.setDisable(true);
				btnAux.setDisable(true);
				auxStop=false;
				
				refreshView();					
			}else {
				taktOn=false;
				taktPort=-1;
				auxStop=true;
				boxExTakt.setDisable(false);
				txtExTakt.setDisable(false);
				btnAux.setDisable(false);
				refreshView();
			}
 	 }
 	 
 	 
 	 
 	   
}


