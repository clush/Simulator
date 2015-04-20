//© Claus Burkhart und Thomas Coenen


package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;


import java.util.Scanner;

import application.CodeReader;
import application.RegisterClass;
import application.ValueClass;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
//import javafx.scene.control.TextField;
//import javafx.scene.text.TextAlignment;
//import javafx.scene.text.TextFlow;
//import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;

public class MyController implements Initializable{

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		 	codeReader.initRegister();
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
	        
	        refreshRegister();
	        
	        /*table.getColumns().add(registerBeschriftung);
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
	        table.setItems(codeReader.getDataRegister());*/
		
	}
	
// Initialisierung der Variablen
	
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
	
	@FXML
	public TableView<RegisterClass> table;
	
	TableColumn<ValueClass, String> col1;
	TableColumn<ValueClass, String> col2;
	TableColumn<ValueClass, String> col3;
	CodeReader codeReader = new CodeReader();
	ObservableList<ValueClass> data = FXCollections.observableArrayList();
	
	Task<Integer> task;
	Thread th;	
	
	private Window stage;	

	@FXML
	private Button btnFoo;
	
	@FXML
	private Button btntest;
	
	public void Test(ActionEvent event){
		codeReader.setwRegister(2);		
		//codeReader.setRegister(3, 0, 255); //Bank wechseln
		codeReader.setRegister(1, 1, 7);
		codeReader.setCode("0A91");
		
		
		
		codeReader.read();
		refreshRegister();
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
	
	
//Beim Klicken auf Start, startet programm
	public void StartProgramm(ActionEvent event){		

		
		task = new Task<Integer>() {
		    @Override protected Integer call() throws Exception {
		    while(true){
				if(!col1.getCellData(codeReader.getLine()).equals("")){
					if(Integer.parseInt(col1.getCellData(codeReader.getLine()),16)==codeReader.getPc()){
						//Highlight
						tableView.getSelectionModel().select(codeReader.getLine());
												
						try {
							Thread.sleep(500);							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Code übergeben
						codeReader.setCode(col2.getCellData(codeReader.getLine()));
						codeReader.read();
						//System.out.println(registerSpalte4.getCellData(0));
						//break;
						Platform.runLater(new Runnable() {
		                     @Override public void run() {
		                         refreshRegister();
		                         txt_wRegister.setText(codeReader.getwRegister());
		                         
		                     }
		                 });
					}	
				}
				codeReader.increaseLine();
				if(codeReader.getLine()>=50)break;
				
			}
		    	
		        return codeReader.getLine();
		    }
		    
		};
		
		th = new Thread(task);

		th.setDaemon(true);

		th.start();			
		
	}
	
	public void Steps(ActionEvent event)  {
		while(true){
			if(!col1.getCellData(codeReader.getLine()).equals("")){
				if(Integer.parseInt(col1.getCellData(codeReader.getLine()),16)==codeReader.getPc()){
					//Highlight
					tableView.getSelectionModel().select(codeReader.getLine());
											
					//Code übergeben
					codeReader.setCode(col2.getCellData(codeReader.getLine()));
					codeReader.read();
					refreshRegister();
                    txt_wRegister.setText(codeReader.getwRegister());
					//System.out.println(registerSpalte4.getCellData(0));
					break;
					/*Platform.runLater(new Runnable() {
	                     @Override public void run() {
	                         refreshRegister();
	                         txt_wRegister.setText(codeReader.getwRegister());
	                         
	                     }
	                 });*/
				}	
			}
			codeReader.increaseLine();
		}
		
	}

	@SuppressWarnings("deprecation")
	public void StopProgramm(ActionEvent event){
		th.stop();	
		refreshRegister();
	}
//Beim Klicken soll der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){

//vorherige Tabelleninhalte löschen		
		tableView.getColumns().clear();				
		
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
        col1 = new TableColumn<ValueClass, String>("PC");        
        col1.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column1"));

        col2 = new TableColumn<ValueClass, String>("Code");
        col2.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column2"));

        col3 = new TableColumn<ValueClass, String>("Text");
        col3.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column3"));
        
        col1.setEditable(false);
        col1.setSortable(false);
        col1.setMinWidth(40);
        col1.setMaxWidth(40);
        
        col2.setEditable(false);
        col2.setSortable(false);
        col2.setMinWidth(40);
        col2.setMaxWidth(40);
        
        col3.setEditable(false);
        col3.setSortable(false);
        col3.setMinWidth(40);

// Tabellenerzeugung
                
        tableView.setItems(data);
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
        
//Register
       
	}
	
	void refreshRegister(){
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
       
		
	}
	
	
}
