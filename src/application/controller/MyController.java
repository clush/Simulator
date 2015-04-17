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
		// TODO Auto-generated method stub
		
	}
	
	TableColumn<ValueClass, String> col1;
	TableColumn<ValueClass, String> col2;
	TableColumn<ValueClass, String> col3;
	CodeReader codeReader = new CodeReader();
	ObservableList<ValueClass> data = FXCollections.observableArrayList();
	
	ObservableList<RegisterClass> dataRegister = FXCollections.observableArrayList();
	
	TableColumn<RegisterClass, String> registerBeschriftung;
	TableColumn<RegisterClass, String> registerSpalte0;
	TableColumn<RegisterClass, String> registerSpalte1;
	TableColumn<RegisterClass, String> registerSpalte2;
	TableColumn<RegisterClass, String> registerSpalte3;
	TableColumn<RegisterClass, String> registerSpalte4;
	TableColumn<RegisterClass, String> registerSpalte5;
	TableColumn<RegisterClass, String> registerSpalte6;
	TableColumn<RegisterClass, String> registerSpalte7;
	
	Task<Integer> task;
	Thread th;	
	
	private Window stage;	

	@FXML
	private Button btnFoo;
	
	@FXML
	public TableView<ValueClass> tableView; 
	
	@FXML
	public TableView<RegisterClass> table; 
	
	@FXML
	private Label txtBar;
	//private TextFlow txtField;
	@FXML
	private TextArea txtCode;
	
	
//Beim Klicken auf Start, startet programm
	public void StartProgramm(ActionEvent event){
		
		dataRegister.get(0).setSpalten(4, "test");
		
// Refresh table		
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
		
		System.out.println(dataRegister.get(0).getSpalten(4));
		

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
						//break;
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

	public void StopProgramm(ActionEvent event){
		th.stop();	
	}
//Beim Klicken soll der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){

//vorherige Tabelleninhalte löschen
		
		tableView.getColumns().clear();
		table.getColumns().clear();
		
		
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
        for (int i=0;i<=32;i++){
        	dataRegister.add(new RegisterClass("00","00","00","00","00","00","00","00",Integer.toHexString(i*8)));
        }
        
        
        registerBeschriftung = new TableColumn<RegisterClass, String>("");        
        registerBeschriftung.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("beschriftung"));
        registerBeschriftung.setStyle("-fx-background-color: #DFDFDF");
        
        registerSpalte0 = new TableColumn<RegisterClass, String>("00");        
        registerSpalte0.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte0"));
        
        registerSpalte1 = new TableColumn<RegisterClass, String>("01");        
        registerSpalte1.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte1"));
        
        registerSpalte2 = new TableColumn<RegisterClass, String>("02");        
        registerSpalte2.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte2"));
        
        registerSpalte3 = new TableColumn<RegisterClass, String>("03");        
        registerSpalte3.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte3"));
        
        registerSpalte4 = new TableColumn<RegisterClass, String>("04");        
        registerSpalte4.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte4"));
        
        registerSpalte5 = new TableColumn<RegisterClass, String>("05");        
        registerSpalte5.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte5"));
        
        registerSpalte6 = new TableColumn<RegisterClass, String>("06");        
        registerSpalte6.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte6"));
        
        registerSpalte7 = new TableColumn<RegisterClass, String>("07");        
        registerSpalte7.setCellValueFactory(new PropertyValueFactory<RegisterClass, String>("spalte7"));
        
     // Tabellenerzeugung
        
        
        table.getColumns().add(registerBeschriftung);
        table.getColumns().add(registerSpalte0);
        table.getColumns().add(registerSpalte1);
        table.getColumns().add(registerSpalte2);
        table.getColumns().add(registerSpalte3);
        table.getColumns().add(registerSpalte4);
        table.getColumns().add(registerSpalte5);
        table.getColumns().add(registerSpalte6);
        table.getColumns().add(registerSpalte7);
        table.setItems(dataRegister);
        
     }
		
		 
	
	
	
}
