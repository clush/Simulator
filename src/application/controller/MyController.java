//© Claus Burkhart und Thomas Coenen


package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.CodeReader;
import application.RegisterClass;
import application.StackClass;
import application.TextClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	
//Text-Felder
	
	@FXML
	private Text txt_wRegister;
	
//Variablen
	private Task<Integer> taskRun;
	private Thread th;
	private Window stage;
	private int focusLine = 0;
	private int textLine=0;
	private int runable = 0; //Nur wenn diese Variable 1 ist kann runProgram() gestartet werden
	private int speed=100;

//Methode zum Testen einzelner Befehle
	public void Test(ActionEvent event){
		//codeReader.setwRegister(38);		
		//codeReader.setRegister(3, 0, 32); //Bank wechseln
		//codeReader.setRegister(1, 1, 38);
		//codeReader.setRegister(3, 4, 7);
		//codeReader.read("1683");
		
		codeReader.setBit(6, 0, 0);		
		//System.out.println(data.get(2).getColumn0().selectedProperty().get());
		
		/*switch (a){
		case 0: System.out.println("Alles in Butter"); break;
		case 1: System.out.println("Code nicht zulässig"); break;
		case 2: System.out.println("SLEEP");break;
		
		default: System.out.println("Unzulässiger Rückgabewert"); 
		
		}*/		
		
		//Refresh View
		refreshView();
        txt_wRegister.setText(codeReader.getwRegister());
		
	}
	
	
//Methode zur Initialisierung der Register- und Stackt-Tabelle	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	//Geschwindigkeit auf mittel stellen
		
		btnMittel.selectedProperty().set(true);		
		
	//Register initialisieren
		
		codeReader.initRegister();
	
	//Stackspalten definieren
		 	
		 	colBeschriftung = new TableColumn<StackClass,String>("");
		 	colBeschriftung.setCellValueFactory(new PropertyValueFactory<StackClass, String>("beschriftung"));
		 	colBeschriftung.setStyle("-fx-font-weight: bold; -fx-background-color: #DFDFDF;-fx-alignment: center;");
		 	colBeschriftung.setMaxWidth(31);
		 	colBeschriftung.setMinWidth(31);
		 	
		 	
		 	colStack = new TableColumn<StackClass,String>("Stack");
		 	colStack.setCellValueFactory(new PropertyValueFactory<StackClass, String>("stack"));
		 	colStack.setStyle("-fx-alignment: center;");
		 	colStack.setMaxWidth(64);
		 	colStack.setMinWidth(64);
		 			 	
		 	
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
	        registerSpalte0.setMaxWidth(24);
	        registerSpalte0.setMinWidth(24);
	        
	        registerSpalte1 = new TableColumn<RegisterClass, String>("01");        
	        registerSpalte1.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte1"));
	        registerSpalte1.setMaxWidth(24);
	        registerSpalte1.setMinWidth(24);
	        
	        registerSpalte2 = new TableColumn<RegisterClass, String>("02");        
	        registerSpalte2.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte2"));
	        registerSpalte2.setMaxWidth(24);
	        registerSpalte2.setMinWidth(24);
	        
	        registerSpalte3 = new TableColumn<RegisterClass, String>("03");        
	        registerSpalte3.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte3"));
	        registerSpalte3.setMaxWidth(24);
	        registerSpalte3.setMinWidth(24);
	        
	        registerSpalte4 = new TableColumn<RegisterClass, String>("04");        
	        registerSpalte4.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte4"));
	        registerSpalte4.setMaxWidth(24);
	        registerSpalte4.setMinWidth(24);
	        
	        registerSpalte5 = new TableColumn<RegisterClass, String>("05");        
	        registerSpalte5.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte5"));
	        registerSpalte5.setMaxWidth(24);
	        registerSpalte5.setMinWidth(24);
	        
	        registerSpalte6 = new TableColumn<RegisterClass, String>("06");        
	        registerSpalte6.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte6"));
	        registerSpalte6.setMaxWidth(24);
	        registerSpalte6.setMinWidth(24);
	        
	        registerSpalte7 = new TableColumn<RegisterClass, String>("07");        
	        registerSpalte7.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte7"));
	        registerSpalte7.setMaxWidth(24);
	        registerSpalte7.setMinWidth(24);
	        
	        registerSpalte8 = new TableColumn<RegisterClass, String>("08");        
	        registerSpalte8.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte8"));
	        registerSpalte8.setMaxWidth(24);
	        registerSpalte8.setMinWidth(24);
	        
	        registerSpalte9 = new TableColumn<RegisterClass, String>("09");        
	        registerSpalte9.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte9"));
	        registerSpalte9.setMaxWidth(24);
	        registerSpalte9.setMinWidth(24);
	        
	        registerSpalteA = new TableColumn<RegisterClass, String>("0A");        
	        registerSpalteA.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteA"));
	        registerSpalteA.setMaxWidth(24);
	        registerSpalteA.setMinWidth(24);
	        
	        registerSpalteB = new TableColumn<RegisterClass, String>("0B");        
	        registerSpalteB.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteB"));
	        registerSpalteB.setMaxWidth(24);
	        registerSpalteB.setMinWidth(24);
	        
	        registerSpalteC = new TableColumn<RegisterClass, String>("0C");        
	        registerSpalteC.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteC"));
	        registerSpalteC.setMaxWidth(24);
	        registerSpalteC.setMinWidth(24);
	        
	        registerSpalteD = new TableColumn<RegisterClass, String>("0D");        
	        registerSpalteD.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteD"));
	        registerSpalteD.setMaxWidth(24);
	        registerSpalteD.setMinWidth(24);
	        
	        registerSpalteE = new TableColumn<RegisterClass, String>("0E");        
	        registerSpalteE.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteE"));
	        registerSpalteE.setMaxWidth(24);
	        registerSpalteE.setMinWidth(24);
	        
	        registerSpalteF = new TableColumn<RegisterClass, String>("0F");        
	        registerSpalteF.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteF"));
	        registerSpalteF.setMaxWidth(24);
	        registerSpalteF.setMinWidth(24);
	        

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
		
		runable++;
		
		if(runable==1){
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

//Beim Klicken auf Steps wird genau eine Operation ausgeführt
	public void Steps(ActionEvent event)  {
        
		runable++;
		
		if(runable==1){
		taskRun = new Task<Integer>() {
		    @Override protected Integer call() throws Exception {
		    	RunProgram(true, 100);		   
		    	
		        return textLine;
		    }
		    
		};
		
		th = new Thread(taskRun);
		th.setDaemon(true);
		th.start();
		}
		
	}

//Beim Klicken auf Stop wird das laufende Programm angehalten
	
	public void StopProgramm(ActionEvent event){
		runable=0;
	}

//Beim Klicken kann eine Datei ausgewählt werden und der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){
//Kann nur ausgeführt werden, wenn Program nicht gestartet
		
	if(runable==0){
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
      //FocusLine berechnen 						
		if(!(textLine>=focusLine 
				&& textLine<= focusLine + 12)) focusLine = textLine-6;
			
        tableViewText.getSelectionModel().select(textLine);
		refreshView();
	}	
	}

//Beim Klicken auf Reset werden alle Register zurückgesetzt
	public void Reset(ActionEvent event){
		
		if(runable==0){//Kann nur ausgeführt werden, wenn Program gestoppt ist		
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

//Methode, die das eingelesene Programm ausführt
	public void RunProgram(boolean steps, int geschwindigkeit){
		 while(true){			 	
				
			 	if(runable==0)break;
			 
			 	if(!col1.getCellData(textLine).equals("")){
					if(Integer.parseInt(col1.getCellData(textLine),16)==codeReader.getPc()){
						
						 //Highlight
						tableViewText.getSelectionModel().select(textLine);
						
						//Breakpoint abfragen 						
						if (data.get(textLine).getColumn0().
								selectedProperty().get()){
							runable = 0;
							break;
						}
						
						//FocusLine berechnen 						
						if(!(textLine>=focusLine 
								&& textLine<= focusLine + 12)) focusLine = textLine-6;
						
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
						
						//View refresh
						Platform.runLater(new Runnable() {
		                     @Override public void run() {
		                         refreshView();  
		                        
		                     }
		                 });
						
						// Abfragen, ob nur ein Schritt ausgeführt werden soll.
						if(steps){
							textLine = 0;
							runable = 0;
							break;
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
	}
	
//Methode, die nach jeder Änderung aufgerufen wird, um die View zu aktualisieren
 	public void refreshView(){
 	//Register
 		registerBeschriftung.setVisible(false);
 		registerBeschriftung.setVisible(true);
 		
    //W-Register
        txt_wRegister.setText(codeReader.getwRegister());	
    //Zu aktueller Zeile scrollen
		tableViewText.scrollTo(focusLine);
		
	//Stack
		colBeschriftung.setVisible(false);
		colBeschriftung.setVisible(true);
		tableViewStack.getSelectionModel().select(codeReader.getStackpointer());
		
	//PortA
		if(codeReader.bitTest(5, 0, 0))btnPortA0.selectedProperty().set(true);
		else btnPortA0.selectedProperty().set(false);
		
		if(codeReader.bitTest(5, 0, 1))btnPortA1.selectedProperty().set(true);
		else btnPortA1.selectedProperty().set(false);
		
		if(codeReader.bitTest(5, 0, 2))btnPortA2.selectedProperty().set(true);
		else btnPortA2.selectedProperty().set(false);
		
		if(codeReader.bitTest(5, 0, 3))btnPortA3.selectedProperty().set(true);
		else btnPortA3.selectedProperty().set(false);
		
		if(codeReader.bitTest(5, 0, 4))btnPortA4.selectedProperty().set(true);
		else btnPortA4.selectedProperty().set(false);
		
	//Port B
		if(codeReader.bitTest(6, 0, 0))btnPortB0.selectedProperty().set(true);
		else btnPortB0.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 1))btnPortB1.selectedProperty().set(true);
		else btnPortB1.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 2))btnPortB2.selectedProperty().set(true);
		else btnPortB2.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 3))btnPortB3.selectedProperty().set(true);
		else btnPortB3.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 4))btnPortB4.selectedProperty().set(true);
		else btnPortB4.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 5))btnPortB5.selectedProperty().set(true);
		else btnPortB5.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 6))btnPortB6.selectedProperty().set(true);
		else btnPortB6.selectedProperty().set(false);
		
		if(codeReader.bitTest(6, 0, 7))btnPortB7.selectedProperty().set(true);
		else btnPortB7.selectedProperty().set(false);
	}

 /*
 * Methoden, die beim Klicken auf die Radio-Buttons aufgerufen werden
 */
 	public void portB_0(ActionEvent event){
 		/*if(PortB_0.isSelected()){
 		 		
 		codeReader.setBit(6, 0, 0);
 		refreshView();
 		}else{
 			codeReader.clearBit(6, 0, 0);
 			refreshView();
 		}
 		
 		if (true){
 			PortB_0.selectedProperty().set(false);
 		}
 		
 		*/
 	}
 	
 	public void actSchnell(){
 		//Erneutes Anklicken ->Button bleibt aktiv
 		if(!btnSchnell.isSelected()){ 
 			btnSchnell.selectedProperty().set(true);
 		}else{//Wenn nicht bereits ausgewählt
 			if (runable==0){//Nur wenn Programm gestoppt
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
 			if (runable==0){//Nur wenn Programm gestoppt
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
 			if (runable==0){//Nur wenn Programm gestoppt
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
 	
 	public void actPortA0(){
 		if(codeReader.bitTest(5, 8, 0)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortA0.isSelected()){//Setze Bit
 				codeReader.setBit(5, 0, 0);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(5, 0, 0);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortA0.selectedProperty().set(false);
 		}
 		
 	}
 	public void actPortA1(){
 		if(codeReader.bitTest(5, 8, 1)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortA1.isSelected()){//Setze Bit
 				codeReader.setBit(5, 0, 1);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(5, 0, 1);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortA1.selectedProperty().set(false);
 		}
 	}
 	public void actPortA2(){
 		if(codeReader.bitTest(5, 8, 2)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortA2.isSelected()){//Setze Bit
 				codeReader.setBit(5, 0, 2);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(5, 0, 2);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortA2.selectedProperty().set(false);
 		}
 	}
 	public void actPortA3(){
 		if(codeReader.bitTest(5, 8, 3)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortA3.isSelected()){//Setze Bit
 				codeReader.setBit(5, 0, 3);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(5, 0, 3);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortA3.selectedProperty().set(false);
 		}
 	}
 	public void actPortA4(){
 		if(codeReader.bitTest(5, 8, 4)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortA4.isSelected()){//Setze Bit
 				codeReader.setBit(5, 0, 4);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(5, 0, 4);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortA4.selectedProperty().set(false);
 		}
 	}
 	public void actPortB0(){
 		if(codeReader.bitTest(6, 8, 0)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB0.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 0);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 0);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB0.selectedProperty().set(false);
 		}
 	}
 	public void actPortB1(){
 		if(codeReader.bitTest(6, 8, 1)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB1.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 1);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 1);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB1.selectedProperty().set(false);
 		}
 	}
 	public void actPortB2(){
 		if(codeReader.bitTest(6, 8, 2)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB2.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 2);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 2);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB2.selectedProperty().set(false);
 		}
 	}
 	public void actPortB3(){
 		if(codeReader.bitTest(6, 8, 3)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB3.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 3);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 3);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB3.selectedProperty().set(false);
 		}
 	}
 	public void actPortB4(){
 		if(codeReader.bitTest(6, 8, 4)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB4.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 4);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 4);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB4.selectedProperty().set(false);
 		}
 	}
 	public void actPortB5(){
 		if(codeReader.bitTest(6, 8, 5)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB5.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 5);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 5);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB5.selectedProperty().set(false);
 		}
 	}
 	public void actPortB6(){
 		if(codeReader.bitTest(6, 8, 6)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB6.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 6);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 6);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB6.selectedProperty().set(false);
 		}
 	}
 	public void actPortB7(){
 		if(codeReader.bitTest(6, 8, 7)){//Wenn Bit im Tis-Register gesetz->Eingang
 			if(btnPortB7.isSelected()){//Setze Bit
 				codeReader.setBit(6, 0, 7);
 				refreshView();
 			}else{//Lösche Bit
 				codeReader.clearBit(6, 0, 7);
 				refreshView();
 			}
 		}else{//Kein Eingang->Button kann nicht aktiviert werden
 			btnPortB7.selectedProperty().set(false);
 		}
 	}
 	
 	



}


