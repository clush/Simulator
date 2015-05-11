package application;



import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.RegisterClass;
import application.StackClass;


public class CodeReader {
	
	private int wRegister;
	private int pc;
	private int Code;
	private int stackPointer;
	private ObservableList<RegisterClass> dataRegister = FXCollections.observableArrayList();
	private ObservableList<StackClass> dataStack = FXCollections.observableArrayList();
	private int cycles=0;
	private int cyclesAlt=0;
	private boolean ra4Alt=false;
	private int prescaler=0;
	private int prescalCounter=0;
	private int ps123=0;
	private boolean rb0Alt=false;
	private int rb47Alt=0;
	private boolean writeTimer0=true;
	private AuxPort aux = null;
		
	public CodeReader() {
		this.pc=0;
		this.Code=0;
		this.stackPointer = 0;
		@SuppressWarnings("unused")
		int cycles = 0;
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
		else if((Code & 0x3FFF) == 0x0063)SLEEP(); //TODO: TO? PD? 
		else if((Code & 0x3E00) == 0x3C00)SUBLW();
		else if((Code & 0x3F00) == 0x3A00)XORLW();
	
	//Wenn Code nicht bekannt liefere 1 zurück;
		else{
		increasePc();
		return 1;
		}
		
		//Timer bearbeiten
			changeTimer0();
		//Interrupts prüfen
		interruptTest();
		
	return 0;
	}
	
	
	private void interruptTest() {
		if(bitTest(11,0,7)){
			//Timer0-Interrupt
			if(bitTest(11,0,2)&&bitTest(11,0,5)){
				interrupt();
			}
				
			//RB0/INT - Interrupt
			
			if(bitTest(1,8,6)){		//Interrupt bei steigender Flanke
				if(bitTest(6,0,0)&&!rb0Alt){
					setBit(11,0,1);
					interrupt();
				}
			}else{					//Interrupt bei fallender Flanke
				if(!bitTest(6,0,0)&&rb0Alt){
					setBit(11,0,1);
					interrupt();
				}
			}			
			
			
			//PortB-Interrupt
			
			if((getRegister(6,0)&0xF0)!=rb47Alt){
				setBit(11,0,0);
				interrupt();
			}
			
		}
		
		
		//Abspeichern des alten Wertes von RB0
		rb0Alt = bitTest(6,0,0);
		
		//Abspeichern der alten Wertes von RB4 - RB7
		rb47Alt = getRegister(6,0)&0xF0;
	}
		
	

	private void interrupt(){
		clearBit(11,0,7);
		//Stackpointer nach rechts rotieren
		if(stackPointer>=7)stackPointer=0;
		else stackPointer++;
		//Abspeichern der Programmstelle der nächsten Operation im Stack
		dataStack.get(stackPointer).setStack(pc);
		pc=4;
	}
	
	
	private void changeTimer0() {
		//Berechnung des Prescalers
		if(bitTest(1,8,0))ps123=1;
		else ps123=0;
		
		if(bitTest(1,8,1))ps123+=2;
		else ps123+=0;
		
		if(bitTest(1,8,2))ps123+=4;
		else ps123+=0;		
		
		if(!bitTest(1,8,5)){	//Timer0 im Timer mode
			
			if(!bitTest(1,8,3)){   	//Wenn Prescaler eingeschaltet			
				prescaler=(int)Math.pow(2, ps123+1);
			}else{					//ohne Prescaler
				prescaler=1; 
			}
			
			prescalCounter+=(cycles-cyclesAlt);		
			
			
			if(prescalCounter>=prescaler){ //Prescaler erreicht
				//Zurücksetzen des Counters
				prescalCounter-=prescaler;
				int wert;
				
				if(!bitTest(1,8,3)){   	//Wenn Prescaler eingeschaltet			
					wert=getRegister(1,0)+1;
					
				}else{					//ohne Prescaler
					wert=getRegister(1,0)+1+prescalCounter;
					prescalCounter=0;
				}				
						
				
				//TIMER0-Interrupt überprüfen
				if(wert>0xFF){
					setBit(11,0,2);
				}
				
				//Abspeichern
				writeTimer0=false;
				setRegister(1,0,wert);
				writeTimer0=true;
				
			}
			
		}else{					//Timer0 im Counter mode
			
			//Abfrage, ob Prescaler eingeschaltet ist;
			
			if(!bitTest(1,8,3)){   	//Prescaler eingeschaltet			
				prescaler=(int)Math.pow(2, ps123+1);
			}else{					//ohne Prescaler
				prescaler=1; 
			}			
			
			//Abfrage, ob bei steigender oder fallender Flanke erhöht werden soll
			
			if(bitTest(1,8,4)){ 	//Wenn TOSE-Bit gesetzt, zähle hoch bei fallender Flanke
				if(ra4Alt && !bitTest(5,0,4)){ 
					prescalCounter++;	
				}
			}else{					////Wenn TOSE-Bit nicht gesetzt, zähle hoch bei steigender Flanke
				if(!ra4Alt && bitTest(5,0,4)){ 
					prescalCounter++;	
				}		
			}
			
			//Abfrage, ob Counter erhöht werden kann
			
			if(prescalCounter>=prescaler){ //Prescaler erreicht
				//Zurücksetzen des Counters
				prescalCounter=0;
				int wert;				
											
				wert=getRegister(1,0)+1;
				
				//TIMER0-Interrupt überprüfen
				if(wert>0xFF){					
					setBit(11,0,2);
				}
				
				//Abspeichern
				writeTimer0=false;
				setRegister(1,0,wert);
				writeTimer0=true;
				
			}
		}
		
				
		
		//Abspeichern des alten Cycle-Wertes und Ra4-Wertes
		cyclesAlt=cycles;
		ra4Alt=bitTest(5,0,4);
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
			setCarryBit(result <= 0x00); 
		//Wenn die Addition der 4 Low-Bits größer als 15, setze das Carry-Digit-Bit
			setCarryDigitBit(((getLiteral()&0xF)-(wRegister&0xF))<=0x0);
			setZeroBit(result);
		//Abspeichern	
			setwRegister(result);			
		//Erhöhen des Programmzählers
			increasePc();
		
	}

	private void RETURN() {
		//Zurückspringen an in Stack gespeicherte Addresse
			setPc(dataStack.get(stackPointer).getStackInd());
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
			setPc(dataStack.get(stackPointer).getStackInd());
		//StackPointer nach links rotieren
			if(stackPointer<=0)stackPointer=7;
			else stackPointer--;
		
	}

	private void RETFIE() {
		//Zurückspringen an in Stack gespeicherte Addresse
		setPc(dataStack.get(stackPointer).getStackInd());
		//StackPointer nach links rotieren
		if(stackPointer<=0)stackPointer=7;
		else stackPointer--;
		
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
			dataStack.get(stackPointer).setStack(pc + 1);
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
		 	else setwRegister(result);		
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
			setCarryBit(result <= 0x0); 
			//Wenn die Subtraktion der 4 Low-Bits kleiner als 0, setze das Carry-Digit-Bit
			setCarryDigitBit(((getFile()&0xF)-(wRegister&0xF))<=0x0);
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
	 		else setwRegister(result);		
	 	//Erhöhen des Programmzählers 
	 		increasePc();
		
	}

	private void RLF() {
		//Berechnung
			int result = getFile()*2;
			if(bitTest(3,0,0)) result += 1; 
		//Setzen/Löschen der Status-Bits
		 	setCarryBit(result>0xFF);
		 //Abspeichern
		 	//Wenn d =1, dann speichere in f
		 	if ((Code & mask(7))==mask(7))setFile(result);
		 	//Wenn d =0, dann speichere in w					
		 	else setwRegister(result);		
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
			else setwRegister(result);		
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
			else setwRegister(result);		
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
			else setwRegister(result);		
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
		if(Integer.valueOf(wRegister)<=0xF)return 0+Integer.toHexString(wRegister);
		else return Integer.toHexString(wRegister).toUpperCase();
	}
	
	
	public void setwRegister(int wert) {
		this.wRegister = (wert & 0xFF) ;
	}
	

	public ObservableList<RegisterClass> getDataRegister() {
		return dataRegister;
	}
	
	public ObservableList<StackClass> getDataStack() {
		return dataStack;
	}
	
	public int getStackpointer(){
		return stackPointer;
	}
	
	public int getCycles(){			
		return cycles;
	}
	

	public int getPc() {
		return pc;
	}

	private void setPc(int pc) {
		//in entsprechenden Registern speichern
		setRegister(2, 0, pc);
		cycles+=2;
		
	}
	
	private void increasePc(){
		//Erhöhe Programmzähler und speichern in entsprechenden Registerfeldern
		cycles++;
		setRegister(2, 0, pc+1);

	}

	public void resetRegister(){
		
		//Register
		for (int i = 0;i<16;i++){
			for (int j = 0; j<16;j++){
				setRegister(j,i,0);
			}
		}
		//Register, die nicht null sind
		setRegister(3,0,24);//STATUS
		setRegister(1,8,255);//Option-Reg
		setRegister(5,8,255);//TrisA
		setRegister(6,8,255);//TrisB
		setRegister(4,0,32);//FSR
		
		//Stack
		for(int i=0;i<8;i++){
			dataStack.get(i).setStack(0);
		}
		
		stackPointer=0;
		pc=0;
		wRegister=0;
	}
	
	
	public void initRegister(){	
		
		for (int i=0;i<16;i++){
        	dataRegister.add(new RegisterClass("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",Integer.toHexString(i).toUpperCase()+"0"));
        }
		
		for (int i=0;i<8;i++){
			dataStack.add(new StackClass("0",Integer.toString(i)));
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
		/*
		 * Abfragen, ob Spezial-Register gesetzt werden.
		 */
		if(y==0 || y==8){
			//Indirektes-Register
			if(x==0){
				x = getRegister(4,0)&0x0F;
				y = (getRegister(4,0)&0xF0)/16;
				dataRegister.get(y).setSpalten(x, wert);
			}//TMR0
			else if(x==1&&y==0){
				if(writeTimer0){
					prescalCounter=0;
					if(!bitTest(1,8,5))prescalCounter-=2;					
				}				
				dataRegister.get(y).setSpalten(x, wert);
			}//PC			
			else if(x==2){
				pc = wert;
				dataRegister.get(0).setSpalten(x, wert);
				dataRegister.get(8).setSpalten(x, wert);
			} //Status
			else if(x==3){
				dataRegister.get(0).setSpalten(x,(wert));
				dataRegister.get(8).setSpalten(x,(wert));
			} //FSR
			else if(x==4){
				if(wert==0)wert=32;
				dataRegister.get(0).setSpalten(x,(wert));
				dataRegister.get(8).setSpalten(x,(wert));
			} //PCLATH
			else if(x==10){
				dataRegister.get(0).setSpalten(x,(wert));
				dataRegister.get(8).setSpalten(x,(wert));
			} //INTCON
			else if(x==11){
				dataRegister.get(0).setSpalten(x,(wert));
				dataRegister.get(8).setSpalten(x,(wert));
			}
			else dataRegister.get(y).setSpalten(x, wert);
			
		}else dataRegister.get(y).setSpalten(x, wert);
			
	}
	
	public int getRegister(int x, int y){
		/* Wenn das Indirekt-Register angesprochen wird setze 
		 * für x und y die Werte aus FSR-Register ein
		 */
		if(x==0 && (y==0 || y==8)){
			x = getRegister(4,0)&0x0F;
			y = (getRegister(4,0)&0xF0)/16;
		}
		return this.dataRegister.get(y).getSpalten(x);
	}
	
	private int mask(int bit){
		return (int)(Math.pow(2, bit));
	}
	
	public void connectAuxPort(String auxPort) {
        int trisA, trisB, portA, portB;
        trisA = getRegister(5,8);
        trisB = getRegister(6,8);
        portA = getRegister(5,0);
        portB = getRegister(6,0);
        aux = new AuxPort(trisA, portA, trisB, portB);
        try {
            aux.connect(auxPort);
        } catch (Exception ex) {
            Logger.getLogger(CodeReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Schließt die aktuelle Verbindung und setzt ComPort-Objekt auf null.
     */
    public void closeAuxPort() {
        aux.close();
        aux = null;
    }

    /**
     * Kommuniziert mit COM-Port. Gibt TRIS und Port Werte an COM aus
     * und liest neue Port Werte ein.
     */
    public void refreshAuxPort() {
        int trisA, trisB, portA, portB;
        trisA = getRegister(5,8);
        trisB = getRegister(6,8);
        portA = getRegister(5,0);
        portB = getRegister(6,0);
        aux.updatePortA(portA);
        aux.updatePortB(portB);
        aux.updateTrisA(trisA);
        aux.updateTrisB(trisB);

        portA = aux.getInputPortA();
        portB = aux.getInputPortB();

        setRegister(5,0,portA);
        setRegister(6,0,portB);
    }

    /**
     * @return the aux
     */
    public AuxPort getAux() {
        return aux;
    }
}
