//© Claus Burkhart und Thomas Coenen


package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


import java.util.Scanner;

import application.ValueClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TextField;
//import javafx.scene.text.TextAlignment;
//import javafx.scene.text.TextFlow;
//import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;



public class MyController implements Initializable{

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

	@FXML
	private Button btnFoo;
	
	@FXML
	private TableView<ValueClass> tableView; 
	
	@FXML
	private Label txtBar;
	//private TextFlow txtField;
	@FXML
	private TextArea txtCode;


	private Window stage;
	

	
	//Beim Klicken auf btnFoo, ChangeText Methode ändern den Text in txtBar
	public void StartProgramm(ActionEvent event){
		String zeile = txtCode.getText(0, 10);
		System.out.println(zeile);
		
	}
	
	//Beim Klicken soll der Inhalt der Textdatei eingefügt werden
	public void InsertText(ActionEvent event){
		
		ObservableList<ValueClass> data = FXCollections.observableArrayList();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Öffne Datei");
		File file = fileChooser.showOpenDialog(stage);
        
		if (!file.canRead() || !file.isFile())
            System.exit(0);

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
        TableColumn<ValueClass, String> col1 = new TableColumn<ValueClass, String>("PC");        
        col1.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column1"));

        TableColumn<ValueClass, String> col2 = new TableColumn<ValueClass, String>("Code");
        col2.setCellValueFactory(new PropertyValueFactory<ValueClass, String>("column2"));

        TableColumn<ValueClass, String> col3 = new TableColumn<ValueClass, String>("Text");
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
        
       

        
     }
		
		 
	
	
	
}
