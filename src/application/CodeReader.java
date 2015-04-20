package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.RegisterClass;


public class CodeReader {
	
	private int wRegister;
	private int pc;
	private int line;
	private int Code;
	private ObservableList<RegisterClass> dataRegister = FXCollections.observableArrayList();
		
	public CodeReader() {
		this.pc=0;
		this.line=0;
		this.Code=0;		
	}	

	public void read (){
		System.out.println(this.Code);
		this.wRegister=Code;
		setRegister(4,0,Integer.toHexString(this.Code));
		this.pc++;		
	}
	
	public String getwRegister() {
		return Integer.toHexString(wRegister);
	}

	public ObservableList<RegisterClass> getDataRegister() {
		return dataRegister;
	}

	public int getLine() {
		return line;
	}

	public void increaseLine() {
		this.line++;
	}

	public int getCode() {
		return Code;
	}

	public void setCode(String code) {
		this.Code = Integer.parseInt(code,16) ;
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	
	public void initRegister(){	
		
		for (int i=0;i<16;i++){
        	dataRegister.add(new RegisterClass("00","00","00","00","00","00","00","00","00","00","00","00","00","00","00","00",Integer.toHexString(i)));
        }
        
     }
	
	public void setRegister(int x, int y, String wert){
		this.dataRegister.get(y).setSpalten(x, wert);
	}
}
