//© Claus Burkhart und Thomas Coenen


package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;


import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Öffne Datei");
		File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
        	if (txtCode.getText() != null)txtCode.setText(null);
        	try {
                Scanner s = new Scanner(file).useDelimiter("//s+");
                
                while (s.hasNext()) {
                    if (s.hasNextInt()) { // check if next token is an int
                        txtCode.appendText(s.nextInt() + " "); // display the found integer
                    } else {
                       txtCode.appendText(s.next() + " "); // else read the next token
                    }
                }
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
            }
                        
        }
		
		 
	}
	
	
	
}
