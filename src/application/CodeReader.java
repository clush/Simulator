package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.RegisterClass;


public class CodeReader {
	
	private int wRegister;
	private int pc;
	private int line;
	private int Code;
	private int[] stack = new int[8];
	private int stackPointer;
	private ObservableList<RegisterClass> dataRegister = FXCollections.observableArrayList();
		
	public CodeReader() {
		this.pc=0;
		this.line=0;
		this.Code=0;
		this.stackPointer = 0;
	}	

	public int read (String code){
	//Übergabe des neuen Codes
		this.Code = Integer.parseInt(code,16);
	/*
	 * Im folgenden wird geprüft um welchen Befehl es sich handelt 
	 * und die entsprchende Methode aufgerufen
	 */
		
	//Byte-oriented file register operations:
		
		if     ((Code & 0x3F00) == 0x0700)ADDWF();	
		else if((Code & 0x3F00) == 0x0500)ANDWF();		
		else if((Code & 0x3F80) == 0x0180)CLRF();		
		else if((Code & 0x3F80) == 0x0100)CLRW();		
		else if((Code & 0x3F00) == 0x0900)COMF();			
		else if((Code & 0x3F00) == 0x0300)DECF();
		else if((Code & 0x3F00) == 0x0B00)DECFSC();				
		else if((Code & 0x3F00) == 0x0A00)INCF();
		else if((Code & 0x3F00) == 0x0F00)INCFSZ();
		else if((Code & 0x3F00) == 0x0400)IORWF();
		else if((Code & 0x3F00) == 0x0800)MOVF();
		else if((Code & 0x3F80) == 0x0080)MOVWF();
		else if((Code & 0x3F9F) == 0x0000)NOP();
		else if((Code & 0x3F00) == 0x0D00)RLF();
		else if((Code & 0x3F00) == 0x0C00)RRF();
		else if((Code & 0x3F00) == 0x0200)SUBWF();
		else if((Code & 0x3F00) == 0x0E00)SWAPF();
		else if((Code & 0x3F00) == 0x0600)XORWF();
		
	//Bit oriented file register operations
		
		else if((Code & 0x3C00) == 0x1000)BCF();
		else if((Code & 0x3C00) == 0x1400)BSF();
		else if((Code & 0x3C00) == 0x1800)BTFSC();
		else if((Code & 0x3C00) == 0x1C00)BTFSS();
		
	//Literal and Controll Operations
		
		else if((Code & 0x3E00) == 0x3E00)ADDLW();
		else if((Code & 0x3F00) == 0x3900)ANDLW();
		else if((Code & 0x3800) == 0x2000)CALL();	//TODO PCLATH?
		else if((Code & 0x3FFF) == 0x0064)CLRWDT(); //TODO
		else if((Code & 0x3800) == 0x2800)GOTO();   //TODO PCLATH?
		else if((Code & 0x3F00) == 0x3800)IORLW();
		else if((Code & 0x3C00) == 0x3000)MOVLW();
		else if((Code & 0x3FFF) == 0x0009)RETFIE(); //TODO
		else if((Code & 0x3C00) == 0x3400)RETLW();
		else if((Code & 0x3FFF) == 0x0008)RETURN();
		else if((Code & 0x3FFF) == 0x0063){SLEEP(); return 2;} //TODO: TO? PD? Rückgabewert in Whileschleife abfangen
		else if((Code & 0x3E00) == 0x3C00)SUBLW();
		else if((Code & 0x3F00) == 0x3A00)XORLW();
	
	//Wenn Code nicht bekannt liefere 1 zurück;
		else{
		increasePc();
		return 1;
		}
		
	//Gleichsetzen der Register, die auf beiden Bänken gleich sind
		if (bitTest(3,0,5)){			
			setRegister(0,0,getRegister(0,8)); //Indirekt address
			setRegister(2,0,getRegister(2,8)); //PCL
			setRegister(4,0,getRegister(4,8)); //FSR
			setRegister(10,0,getRegister(10,8)); //PCLATH
			setRegister(11,0,getRegister(11,8)); //INTCON			
		}else{
			setRegister(0,8,getRegister(0,0)); //Indirekt address
			setRegister(2,8,getRegister(2,0)); //PCL
			setRegister(4,8,getRegister(4,0)); //FSR
			setRegister(10,8,getRegister(10,0)); //PCLATH
			setRegister(11,8,getRegister(11,0)); //INTCON	
		
		}
	
				
	return 0;
	}
	
	
	private void SLEEP() {
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void XORLW() {
		//Berechnung
			int result = wRegister ^ getLiteral();
		//Setzen/Löschen der Status-Bits	
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void SUBLW() {
		//Berechnung
			int result = getLiteral() - wRegister;
		//Setzen/Löschen der Status-Bits	
			setCarryBit(result < 0x00); 
		//Wenn die Addition der 4 Low-Bits größer als 15, setze das Carry-Digit-Bit
			setCarryDigitBit(((getLiteral()&0xF)-(wRegister&0xF))<0x0);
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void RETURN() {
		//Zurückspringen an in Stack gespeicherte Addresse
			setPc(stack[stackPointer]);
		//StackPointer nach links rotieren
			if(stackPointer<=0)stackPointer=7;
			else stackPointer--;
		
	}

	private void RETLW() {
		//Berechnung
			int result = getLiteral();
		//Abspeichern	
			setwRegister(result);			
		//Zurückspringen an in Stack gespeicherte Addresse
			setPc(stack[stackPointer]);
		//StackPointer nach links rotieren
			if(stackPointer<=0)stackPointer=7;
			else stackPointer--;
		
	}

	private void RETFIE() {
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void MOVLW() {
		//Berechnung
			int result = getLiteral();
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void IORLW() {
		//Berechnung
			int result = wRegister | getLiteral();
		//Setzen/Löschen der Status-Bits	
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void GOTO() {
		
		setPc(this.Code & 0x07FF);	
		
	}

	//Literal and Controll Operations
	
	private void CLRWDT() {
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void CALL() {
		//Stackpointer nach rechts rotieren
			if(stackPointer>=7)stackPointer=0;
			else stackPointer++;
		//Abspeichern der Programmstelle der nächsten Operation im Stack
			stack[stackPointer] = pc + 1;
		//Sprung zu angegebenem Programmstelle 
			setPc(this.Code & 0x07FF);		
	}

	private void ANDLW() {
		//Berechnung
			int result = wRegister & getLiteral();
		//Setzen/Löschen der Status-Bits	
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void ADDLW() {
	
		//Berechnung
			int result = wRegister + getLiteral();
		//Setzen/Löschen der Status-Bits	
			setCarryBit(result > 0xFF); 
			//Wenn die Addition der 4 Low-Bits größer als 15, setze das Carry-Digit-Bit
			setCarryDigitBit(((getLiteral()&0xF)+(wRegister&0xF))>0xF);
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();			
	}
	
	//Bit oriented file register operations
	
	private void BTFSS() {
		//Koordinaten des Files aus Code auslesen
			//Ermittlung der Registerspalte
			int x = Code & 0x000F; 
			//Ermittlung der Registerzeile			
			int y = (Code & 0x0070) / 16; 
			// Wenn Z-Bit gesetzt, wechsle auf Bank1	
			if (bitTest(3,0,5)) y = y + 8; 
		//Test ob Bit gesetzt ist
			//Wenn ja, überspringe nächste Operation
			if(bitTest(x,y,getBit())) increasePc();
		//Erhöhen des Programmzählers 
			increasePc();	
		
	}

	private void BTFSC() {
		//Koordinaten des Files aus Code auslesen
			//Ermittlung der Registerspalte
			int x = Code & 0x000F; 
			//Ermittlung der Registerzeile			
			int y = (Code & 0x0070) / 16; 
			// Wenn Z-Bit gesetzt, wechsle auf Bank1	
			if (bitTest(3,0,5)) y = y + 8; 
		//Test ob Bit gesetzt ist
			//Wenn nein, überspringe nächste Operation
			if(!bitTest(x,y,getBit())) increasePc();
		//Erhöhen des Programmzählers 
			increasePc();					
	}

	private void BSF() {
		//Koordinaten des Files aus Code auslesen
			//Ermittlung der Registerspalte
			int x = Code & 0x000F; 
			//Ermittlung der Registerzeile			
			int y = (Code & 0x0070) / 16; 
			// Wenn Z-Bit gesetzt, wechsle auf Bank1	
			if (bitTest(3,0,5)) y = y + 8; 
		//Operation ausfühern
			setBit(x,y,getBit());
		//Erhöhen des Programmzählers 
			increasePc();		
	}

	private void BCF() {
		//Koordinaten des Files aus Code auslesen
			//Ermittlung der Registerspalte
			int x = Code & 0x000F; 
			//Ermittlung der Registerzeile			
			int y = (Code & 0x0070) / 16; 
			// Wenn Z-Bit gesetzt, wechsle auf Bank1	
			if (bitTest(3,0,5)) y = y + 8; 
		//Operation ausfühern
			clearBit(x,y,getBit());
		//Erhöhen des Programmzählers 
		 	increasePc();		 	
	}

	//Byte-oriented file register operations:
	
	private void XORWF() {
		//Berechnung
		 	int result = wRegister ^ getFile();
		//Setzen/Löschen der Status-Bits
		 	setZeroBit(result);
		//Abspeichern
		 	//Wenn d =1, dann speichere in f
		 	if ((Code & mask(7))==mask(7))setFile(result);
		 	//Wenn d =0, dann speichere in w					
		 	else setwRegister(wRegister & getFile());		
		//Erhöhen des Programmzählers 
		 	increasePc();
		
	}

	private void SWAPF() {
		//Berechnung
			int result = ((getFile()&0x0F)*16)+((getFile()&0xF0)/16);	
		//Abspeichern	
			//Wenn d = 1 speichere Ergebnis in f
			if ((Code & mask(7))==mask(7)) setFile(result);
			//Wenn d = 0, dann speichere in w					
			else setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();		
	}

	private void SUBWF() {
		//Berechnung
			int result = getFile() - wRegister;
		//Setzen/Löschen der Status-Bits	
			setCarryBit(result < 0x0); 
			//Wenn die Subtraktion der 4 Low-Bits kleiner als 0, setze das Carry-Digit-Bit
			setCarryDigitBit(((getFile()&0xF)-(wRegister&0xF))<0x0);
			setZeroBit(result);
		//Abspeichern	
			//Wenn d = 1 speichere Ergebnis in f
			if ((Code & mask(7))==mask(7)) setFile(result);
			//Wenn d = 0, dann speichere in w					
			else setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();			
	}

	private void RRF() {
		//Berechnung
	 		int result = getFile()/2;
	 		if(bitTest(3,0,0)) result += 0x80; 
	 	//Setzen/Löschen der Status-Bits
	 		setCarryBit((getFile()&0x01)==1);
	 	//Abspeichern
	 		//Wenn d =1, dann speichere in f
	 		if ((Code & mask(7))==mask(7))setFile(result);
	 		//Wenn d =0, dann speichere in w					
	 		else setwRegister(wRegister & getFile());		
	 	//Erhöhen des Programmzählers 
	 		increasePc();
		
	}

	private void RLF() {
		//Berechnung
			int result = getFile()*2;		 	
		//Setzen/Löschen der Status-Bits
		 	setCarryBit(result>0xFF);
		 //Abspeichern
		 	//Wenn d =1, dann speichere in f
		 	if ((Code & mask(7))==mask(7))setFile(result);
		 	//Wenn d =0, dann speichere in w					
		 	else setwRegister(wRegister & getFile());		
		 //Erhöhen des Programmzählers 
		 	increasePc();
		
	}

	private void NOP() {
		//Erhöhen des Programmzählers 
			increasePc();
		
	}

	private void MOVWF() {
		//Berechnung
			int result = wRegister;
	
		//Abspeichern
			setFile(result);
				
		//Erhöhen des Programmzählers 
			increasePc();
		
	}

	private void MOVF() {
		//Berechnung
			int result = getFile();
		//Setzen/Löschen der Status-Bits
			setZeroBit(result);
		//Abspeichern
			//Wenn d =1, dann speichere in f
			if ((Code & mask(7))==mask(7))setFile(result);
			//Wenn d =0, dann speichere in w					
			else setwRegister(wRegister & getFile());		
		//Erhöhen des Programmzählers 
			increasePc();		
	}

	private void IORWF() {
		//Berechnung
			int result = wRegister | getFile();
		//Setzen/Löschen der Status-Bits
			setZeroBit(result);
		//Abspeichern
			//Wenn d =1, dann speichere in f
			if ((Code & mask(7))==mask(7))setFile(result);
			//Wenn d =0, dann speichere in w					
			else setwRegister(wRegister & getFile());		
		//Erhöhen des Programmzählers 
			increasePc();		
	}

	private void INCFSZ() {
	//Berechnung	
		INCF();
	//Wenn Zero-Bit gestzt überspringen nächste Zeile
		if (bitTest(3,0,2))increasePc();
		
	}

	private void INCF() {
	//Berechnung	
		int result = getFile()+1;
	//Setzen/Löschen der Status-Bits
		setZeroBit(result);
	//Abspeichern
		//Wenn d =1, dann speichere in f
		if ((Code & mask(7))==mask(7))setFile(result);
		//Wenn d =0, dann speichere in w					
		else setwRegister(result);
		
	//Erhöhen des Programmzählers 
		increasePc();
		
	}

	private void DECFSC() {
	//Berechnung	
		DECF();
	//Wenn Zero-Bit gestzt überspringen nächste Zeile
		if (bitTest(3,0,2))increasePc();
		
	}

	private void DECF() {
		//Berechnung
			int result = getFile()-1;
		//Setzen/Löschen der Status-Bits
			setZeroBit(result);
		//Abspeichern
			//Wenn d =1, dann speichere in f
			if ((Code & mask(7))==mask(7))setFile(result);
			//Wenn d =0, dann speichere in w					
			else setwRegister(result);
		
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void COMF() {
		//Berechnung
			int result = getFile() ^ 0xFF;
		//Setzen/Löschen der Status-Bits
			setZeroBit(result);
		//Abspeichern
			//Wenn d =1, dann speichere in f
			if ((Code & mask(7))==mask(7)) setFile(result);
			//Wenn d =0, dann speichere in w					
			else setwRegister(result);		
		//Erhöhen des Programmzählers 
			increasePc();			
	}

	private void CLRW() {
		//Setzen/Löschen der Status-Bits
			setZeroBit(0);
		//wRegister auf 0 setzen
			setwRegister(0);
		//Erhöhen des Programmzählers
			increasePc();		
	}

	private void CLRF() {
		//Setzen/Löschen der Status-Bits
			setZeroBit(0);
		//Entsprechendes Registerfeld auf 0 setzen
			setFile(0);
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void ANDWF() {
		//Berechnung
			int result = wRegister & getFile();
		//Setzen/Löschen der Status-Bits
			setZeroBit(result);
		//Abspeichern
			//Wenn d =1, dann speichere in f
			if ((Code & mask(7))==mask(7))setFile(result);
			//Wenn d =0, dann speichere in w					
			else setwRegister(wRegister & getFile());		
		//Erhöhen des Programmzählers 
			increasePc();
		
	}

	private void ADDWF() {
		//Berechnung
			int result = wRegister + getFile();
		//Setzen/Löschen der Status-Bits	
			setCarryBit(result > 0xFF); 
			//Wenn die Addition der 4 Low-Bits größer als 15, setze das Carry-Digit-Bit
			setCarryDigitBit(((getFile()&0xF)+(wRegister&0xF))>0xF);
			setZeroBit(result);
		//Abspeichern	
			//Wenn d = 1 speichere Ergebnis in f
			if ((Code & mask(7))==mask(7)) setFile(result);
			//Wenn d = 0, dann speichere in w					
			else setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();		
	}
	
	
/* Tools:	 * 
 * Hilfsmethoden für immer wiederkehrende Aktionen	
 */
	
	public int getLiteral(){
		return Code & 0xFF;
	}
	
	
	private int getBit(){
	/*
	 * Methode, die aus dem Code für bit-oreintierte Operationen
	 * die Bitnummer ausließt 
	 */	
	return (Code & 0x0380) / mask(7);			
		
	}
	
	
	private void setCarryBit(boolean bool){
		//Wenn das Ergbnis größer als 255, setze das Carry-Bit
		if(bool){
			setBit(3,0,0); 
			setBit(3,8,0);
		//ansonten lösche das Carry-Bit
		}else{
			clearBit(3,0,0);
			clearBit(3,8,0);
		}
	}
	
	private void setCarryDigitBit(boolean bool){
		/*Diese Methode muss vor dem abspeichern des 
		 *Ergebnisses angewand werden! 
		 */
		
		//Wenn übergebene Bedingung wahr, setze Carry-Digti-Bit
		if(bool){
			setBit(3,0,1); 
			setBit(3,8,1);
		//ansonten lösche das Carry-Digit-Bit
		}else{
			clearBit(3,0,1);
			clearBit(3,8,1);
		}
	}
	
	private void setZeroBit(int result){
		//Wenn das Ergebnis der Operation 0 ist, setze das Zero-Bit
		if((result&0xFF)==0){
			setBit(3,0,2); 
			setBit(3,8,2);
		//ansonten lösche das Zero-Bit
		}else{
			clearBit(3,0,2);
			clearBit(3,8,2);
		}
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

	public int getTextLine() {
		return line;
	}

	public void increaseLine() {
		this.line++;
	}
	
	public int getPc() {
		return pc;
	}

	private void setPc(int pc) {
		this.pc = pc;
		//in entsprechenden Registern speichern
		setRegister(2, 0, pc);
		setRegister(2, 8, pc);
		
		//Beginne mit dem Durchsuchen der Textdatei wieder bei der ersten Zeile
		line=0; 
	}
	
	private void increasePc(){
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		pc=(pc+1)&0xFF;
		setRegister(2, 0, pc);
		setRegister(2, 8, pc);
	}

	
	public void initRegister(){	
		
		for (int i=0;i<16;i++){
        	dataRegister.add(new RegisterClass("00","00","00","00","00","00","00","00","00","00","00","00","00","00","00","00",Integer.toHexString(i)));
        }
        
    }
	
		
	private int getFile(){
		//Ermittlung der Registerspalte
		int x = Code & 0x000F; 
		//Ermittlung der Registerzeile			
		int y = (Code & 0x0070) / 16; 
		// Wenn Z-Bit gesetzt, wechsle auf Bank1	
		if (bitTest(3,0,5)) y = y + 8; 
		
		return getRegister(x,y);
	}
		
	private void setFile(int wert){
		//Ermittlung der Registerspalte
		int x = Code & 0x000F; 
		//Ermittlung der Registerzeile			
		int y = (Code & 0x0070) / 16; 
		// Wenn Z-Bit gesetzt, wechsle auf Bank1	
		if (bitTest(3,0,5)) y = y + 8; 		
				
		setRegister(x,y,wert); 
		
	}
	
	public boolean bitTest(int x, int y, int bit){
		/*
		 * Diese Methode liefert true zurück wenn das übergebene Bit 
		 * gesetzt ist und false wenn nicht.		
		 */
		
		if((getRegister(x,y) & mask(bit)) == mask(bit))
			return true;
		else return false;
	}	
	
	
	public void setBit(int x,int y, int bit){
		setRegister(x,y,getRegister(x,y) | mask(bit));
	}
	
	public void clearBit(int x,int y, int bit){
		setRegister(x,y,getRegister(x,y) & (mask(bit)^0xFF));
	}
	
	public void setRegister(int x, int y, int wert){
		
		if((x==0&&y==0)||(x==0&&y==8)){
			x = getRegister(4,0)&0x0F;
			y = (getRegister(4,0)&0xF0)/16;
		}
		
		if((x==3&&y==0)||(x==3&&y==8)){
			dataRegister.get(0).setSpalten(3,(wert));
			dataRegister.get(8).setSpalten(3,(wert));
		}else dataRegister.get(y).setSpalten(x, wert);
			
	}
	
	public int getRegister(int x, int y){
		if((x==0&&y==0)||(x==0&&y==8)){
			x = getRegister(4,0)&0x0F;
			y = (getRegister(4,0)&0xF0)/16;
		}
		return this.dataRegister.get(y).getSpalten(x);
	}
	
	private int mask(int bit){
		return (int)(Math.pow(2, bit));
	}
}
