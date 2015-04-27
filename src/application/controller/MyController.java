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
import application.ValueClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;


public class MyController implements Initializable{

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		 	codeReader.initRegister();
	        
	//Registerspalten definieren
		 	
		 	registerBeschriftung = new TableColumn<RegisterClass, String>("");        
	        registerBeschriftung.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("beschriftung"));
	        registerBeschriftung.setStyle("-fx-background-color: #DFDFDF");
	        
	        registerSpalte0 = new TableColumn<RegisterClass, String>("0");        
	        registerSpalte0.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte0"));
	        
	        registerSpalte1 = new TableColumn<RegisterClass, String>("1");        
	        registerSpalte1.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte1"));
	        
	        registerSpalte2 = new TableColumn<RegisterClass, String>("2");        
	        registerSpalte2.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte2"));
	        
	        registerSpalte3 = new TableColumn<RegisterClass, String>("3");        
	        registerSpalte3.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte3"));
	        
	        registerSpalte4 = new TableColumn<RegisterClass, String>("4");        
	        registerSpalte4.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte4"));
	        
	        registerSpalte5 = new TableColumn<RegisterClass, String>("5");        
	        registerSpalte5.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte5"));
	        
	        registerSpalte6 = new TableColumn<RegisterClass, String>("6");        
	        registerSpalte6.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte6"));
	        
	        registerSpalte7 = new TableColumn<RegisterClass, String>("7");        
	        registerSpalte7.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte7"));
	        
	        registerSpalte8 = new TableColumn<RegisterClass, String>("8");        
	        registerSpalte8.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte8"));
	        
	        registerSpalte9 = new TableColumn<RegisterClass, String>("9");        
	        registerSpalte9.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte9"));
	        
	        registerSpalteA = new TableColumn<RegisterClass, String>("A");        
	        registerSpalteA.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteA"));
	        
	        registerSpalteB = new TableColumn<RegisterClass, String>("B");        
	        registerSpalteB.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteB"));
	        
	        registerSpalteC = new TableColumn<RegisterClass, String>("C");        
	        registerSpalteC.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteC"));
	        
	        registerSpalteD = new TableColumn<RegisterClass, String>("D");        
	        registerSpalteD.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteD"));
	        
	        registerSpalteE = new TableColumn<RegisterClass, String>("E");        
	        registerSpalteE.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteE"));
	        
	        registerSpalteF = new TableColumn<RegisterClass, String>("F");        
	        registerSpalteF.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalteF"));
	        
	     // Tabellenerzeugung	        
	        
	        refreshView();
	        		
	}
	
// Initialisierung der Registerspalten
	
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
	
// Initialisierung der Registerspalten der TableView zum anzeigen der Datei
	@FXML
	public TableView<RegisterClass> table;
	
	TableColumn<ValueClass, String> col0;
	TableColumn<ValueClass, String> col1;
	TableColumn<ValueClass, String> col2;
	TableColumn<ValueClass, String> col3;
	CodeReader codeReader = new CodeReader();
	ObservableList<ValueClass> data = FXCollections.observableArrayList();
	
	Task<Integer> taskRun;
	Thread th;	
	
	private Window stage;	

//Initialisierung der Buttons
	
	@FXML
	private Button btnStart;

	//Button um einzelne Operationen zu testen	
	@FXML
	private Button btntest;
	
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
	
	@FXML
	private Button btn_Step;
	
	@FXML
	private Text txt_wRegister;
	
	@FXML
	public TableView<ValueClass> tableView; 
	
	@FXML
	private Label txtBar;
	//private TextFlow txtField;
	@FXML
	private TextArea txtCode;
	
	private int focusLine = 0;
	private int textLine=0;
	
	public void RunProgram(boolean steps, int geschwindigkeit){
		 while(true){
				if(!col1.getCellData(textLine).equals("")){
					if(Integer.parseInt(col1.getCellData(textLine),16)==codeReader.getPc()){
						
						 //Highlight
						tableView.getSelectionModel().select(textLine);
						//FocusLine berechnen 						
						if(!(textLine>=focusLine 
								&& textLine<= focusLine + 12)) focusLine = textLine-6;
						
						//Breakpoint abfragen 						
						if (data.get(textLine).getColumn0().
								selectedProperty().get())break;
						
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
	

//Beim Klicken auf Start, startet programm
	public void StartProgramm(ActionEvent event){		
		
		taskRun = new Task<Integer>() {
		    @Override protected Integer call() throws Exception {
		    	RunProgram(false, 80);		   
		    	
		        return textLine;
		    }
		    
		};
		
		th = new Thread(taskRun);
		th.setDaemon(true);
		th.start();			
		
	}

//Beim Klicken auf Steps wird genau eine Operation ausgeführt
	public void Steps(ActionEvent event)  {
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

//Beim Klicken auf Stop wird das laufende Programm angehalten
	@SuppressWarnings("deprecation")
	public void StopProgramm(ActionEvent event){
		th.stop();	
		refreshView();
	}

//Beim Klicken kann eine Datei ausgewählt werden und der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){

//vorherige Tabelleninhalte löschen		
		tableView.getColumns().clear();	

//Reset der Register
		textLine = 0;
		codeReader.resetRegister();
		

//Öffnen eines Fenster zum auswählen einer Datei
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Öffne Datei");
		File file = fileChooser.showOpenDialog(stage);
        
		if (!file.canRead() || !file.isFile())
            System.exit(0);

//Zeilenweises einlesen der Datei und abspeichern in Liste
            BufferedReader in = null;
        try {        	
            
        	in = new BufferedReader(new FileReader(file));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                data.add(new ValueClass(zeile.substring(0, 5).trim(),zeile.substring(5,9).trim(),zeile.substring(9).trim()));
                
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
        
        col0 = new TableColumn<ValueClass, String>("BP");        
        col0.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column0"));
        col0.setMinWidth(22);
        col0.setMaxWidth(22);
        
        col1 = new TableColumn<ValueClass, String>("PC");        
        col1.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column1"));
        col1.setEditable(false);
        col1.setSortable(false);
        col1.setMinWidth(40);
        col1.setMaxWidth(40);
        
        col2 = new TableColumn<ValueClass, String>("Code");
        col2.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column2"));
        col2.setEditable(false);
        col2.setSortable(false);
        col2.setMinWidth(40);
        col2.setMaxWidth(40);
        
        col3 = new TableColumn<ValueClass, String>("Text");
        col3.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column3"));
        col3.setEditable(false);
        col3.setSortable(false);
        col3.setMinWidth(40);

// Tabellenerzeugung
                
        tableView.setItems(data);
        tableView.getColumns().add(col0);
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);    
        
// View refreshen
      //FocusLine berechnen 						
		if(!(textLine>=focusLine 
				&& textLine<= focusLine + 12)) focusLine = textLine-6;
			
        tableView.getSelectionModel().select(textLine);
		refreshView();
	}

//Beim Klicken auf Reset werden alle Register zurückgesetzt
	public void Reset(ActionEvent event){
		//Reset der Register
		textLine = 0;
		codeReader.resetRegister();
		// View refreshen
		//FocusLine berechnen 						
		if(!(textLine>=focusLine 
				&& textLine<= focusLine + 12)) focusLine = textLine-6;
			
        tableView.getSelectionModel().select(textLine);
		refreshView();
	}
	
	
//Methode, die nach jeder Änderung aufgerufen wird, um die View zu aktualisieren
 	public void refreshView(){
	//Register			
		table.setItems(codeReader.getDataRegister());
		table.getColumns().clear();
		table.getColumns().add(registerBeschriftung);
        table.getColumns().add(registerSpalte0);
        table.getColumns().add(registerSpalte1);
        table.getColumns().add(registerSpalte2);
        table.getColumns().add(registerSpalte3);
        table.getColumns().add(registerSpalte4);
        table.getColumns().add(registerSpalte5);
        table.getColumns().add(registerSpalte6);
        table.getColumns().add(registerSpalte7);
        table.getColumns().add(registerSpalte8);
        table.getColumns().add(registerSpalte9);
        table.getColumns().add(registerSpalteA);
        table.getColumns().add(registerSpalteB);
        table.getColumns().add(registerSpalteC);
        table.getColumns().add(registerSpalteD);
        table.getColumns().add(registerSpalteE);
        table.getColumns().add(registerSpalteF);
    //W-Register
        txt_wRegister.setText(codeReader.getwRegister());	
    //Zu aktueller Zeile scrollen
		tableView.scrollTo(focusLine);
       
		
	}
	
	
}
