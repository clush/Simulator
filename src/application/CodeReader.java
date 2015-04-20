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
		
		
		if((Code & 0x3F00) == 0x0700) ADDWF();	
		else if((Code & 0x3F00) == 0x0500)ANDWF();		
		else if((Code & 0x3F80) == 0x0180)CLRF();		
		else if((Code & 0x3F80) == 0x0100)CLRW();		
		else if((Code & 0x3F00) == 0x0900)COMF();			
		else if((Code & 0x3F00) == 0x0300)DECF();
		else if((Code & 0x3F00) == 0x0B00)DECFSC();				
		else if((Code & 0x3F00) == 0x0A00)INCF();
			
		
		//GOTO
		else if ((Code & 0x3800)==0x2800)

		{
			this.pc= (this.Code & 0x07FF);
			//Beginne mit dem Durchsuchen der Textdatei wieder bei der ersten Zeile
			line=0; 
		}
		//
		
		else{
		System.out.println("Code nicht zulässig");
		
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern	
		increasePc();
		}
		
		
	}
	
	private void INCF() {
	//INCF	
		int result = getFile()+1;
		//Wenn d =1, dann speichere in f
		if (bitSetinCode(7)) setFile(result);
		//Wenn d =0, dann speichere in w					
		else setwRegister(result);
		
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();
		
	}

	private void DECFSC() {
	//DECFSC	
		DECF();
		if (getFile()==0)increasePc();
		
	}

	private void DECF() {
	//DECF		
		int result = getFile()-1;
		//Wenn d =1, dann speichere in f
		if (bitSetinCode(7)) setFile(result);
		//Wenn d =0, dann speichere in w					
		else setwRegister(result);
		
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();
		
	}

	private void COMF() {
	//COMF	 
		int result = getFile() ^ 0xFF;
		//Wenn d =1, dann speichere in f
		if (bitSetinCode(7)) setFile(result);
		//Wenn d =0, dann speichere in w					
		else setwRegister(result);
		
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();			
	}

	private void CLRW() {
		//CLRW
		//Setzen der Z-Bits
		setBit(3,0,2); 
		setBit(3,8,2);
		//wRegister auf 0 setzen
		setwRegister(0);
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();
		
	}

	private void CLRF() {
		//CLRF	
		//Setzen der Z-Bits
		setBit(3,0,2); 
		setBit(3,8,2);
		//Entsprechendes Registerfeld auf 0 setzen
		setFile(0);
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();
		
	}

	private void ANDWF() {
		//ANDWF			
		int result = wRegister & getFile();
		
		//Wenn d =1, dann speichere in f
		if (bitSetinCode(7))setFile(result);
		//Wenn d =0, dann speichere in w					
		else setwRegister(wRegister & getFile());
		
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		increasePc();
		
	}

	private void ADDWF() {
		//ADDWF	
			int result = wRegister + getFile();
			
			//Wenn d = 1 speichere Ergebnis in f
			if (bitSetinCode(7)) setFile(result);
			//Wenn d = 0, dann speichere in w					
			else setwRegister(result);
					
			//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
			increasePc();
					
		
	}

	public String getwRegister() {
		return Integer.toHexString(wRegister);
	}
	
	
	public void setwRegister(int wert) {
		this.wRegister = (wert & 0xFF) ;
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
	
	public void increasePc(){
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		pc++;
		setRegister(2, 0, pc);
		setRegister(2, 8, pc);
	}

	
	public void initRegister(){	
		
		for (int i=0;i<16;i++){
        	dataRegister.add(new RegisterClass("00","00","00","00","00","00","00","00","00","00","00","00","00","00","00","00",Integer.toHexString(i)));
        }
        
    }
	
	public void setRegister(int x, int y, int wert){
		this.dataRegister.get(y).setSpalten(x, wert);
	}
		
	public int getFile(){
		//Ermittlung der Registerspalte
		int x = Code & 0x000F; 
		//Ermittlung der Registerzeile			
		int y = (Code & 0x0070) / 16; 
		// Wenn Z-Bit gesetzt, wechsle auf Bank1	
		if (bitSet(3,0,5)) y = y + 8; 
		
		return dataRegister.get(y).getSpalten(x);
	}
		
	public void setFile(int wert){
		//Ermittlung der Registerspalte
		int x = Code & 0x000F; 
		//Ermittlung der Registerzeile			
		int y = (Code & 0x0070) / 16; 
		// Wenn Z-Bit gesetzt, wechsle auf Bank1	
		if (bitSet(3,0,5)) y = y + 8; 		
		
		dataRegister.get(y).setSpalten(x,(wert & 0xFF)); 
		
	}
	
	public boolean bitSetinCode (int bit){
		int Maske;
		switch(bit){
			case 0: Maske= 0x1; break;
			case 1: Maske= 0x2; break;
			case 2: Maske= 0x4; break;
			case 3: Maske= 0x8; break;
			case 4: Maske= 0x10; break;
			case 5: Maske= 0x20; break;
			case 6: Maske= 0x40; break;
			case 7: Maske= 0x80; break;
			default: Maske = 0x00;
		}
			
		if((Code & Maske) == Maske)
			return true;
		else return false;
	}
	public boolean bitSet(int x, int y, int bit){
		int Maske;
		switch(bit){
			case 0: Maske= 0x1; break;
			case 1: Maske= 0x2; break;
			case 2: Maske= 0x4; break;
			case 3: Maske= 0x8; break;
			case 4: Maske= 0x10; break;
			case 5: Maske= 0x20; break;
			case 6: Maske= 0x40; break;
			case 7: Maske= 0x80; break;
			default: Maske = 0x00;
		}
		
		if((dataRegister.get(y).getSpalten(x) & Maske) == Maske)
			return true;
		else return false;
	}
	
	public void setBit(int x,int y, int bit){
				
		int Maske;
		switch(bit){
			case 0: Maske= 0x1; break;
			case 1: Maske= 0x2; break;
			case 2: Maske= 0x4; break;
			case 3: Maske= 0x8; break;
			case 4: Maske= 0x10; break;
			case 5: Maske= 0x20; break;
			case 6: Maske= 0x40; break;
			case 7: Maske= 0x80; break;
			default: Maske = 0x00;
			
		}			
		setRegister(x,y,dataRegister.get(y).getSpalten(x) | Maske);
	}
}
