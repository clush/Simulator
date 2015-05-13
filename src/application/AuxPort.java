package application;

import gnu.io.CommPort;





import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class AuxPort {
	
    private int trisA = 255;
    private int trisB = 255;
    private int portA = 0;    
    private int portB = 0;
    
    SerialPort serialPort;
    CommPort commPort;

 
    public AuxPort(int aTrisA, int aPortA, int aTrisB, int aPortB) {
        trisA = aTrisA;
        portA = aPortA;
        trisB = aTrisB;
        portB = aPortB;
    }
    
    @SuppressWarnings("rawtypes")
    public static List<String> getAllPorts(){

       	
        List<String> list = new ArrayList<String>();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

       	
        while (portList.hasMoreElements()) {
        	CommPortIdentifier portID = (CommPortIdentifier) portList.nextElement();
        	if (portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        		list.add(portID.getName());
        	}
        }
        
   return list;
       
   }

    //Methode um den Com-Port zu vebinden.
    
    public void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            commPort = portIdentifier.open(this.getClass().getName(), 2000);
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            write();
            read();
        }
    }

    //Methode um den Com-Port zu trennen
    
    public void close() {
        serialPort.close();
    }

    // Methode um die obersten 4 Bits zu bekommen
     
    private int getHighBits(int value) {
        for (int i = 0; i < 4; i++) {
            value = value & ~(1 << i);
        }
        for (int i = 31; i > 7; i--) {
            value = value & ~(1 << i);
        }
        value = value >> 4;
        return value;
    }

    // Methode um die unteren 4 Bits zu bekommen
    
    private int getLowBits(int value) {
        for (int i = 31; i > 3; i--) {
            value = value & ~(1 << i);
        }
        return value;
    }

    // Methode Bits so zu verändern, dass es von der Hardware gelesen werden kann
    
    private int setBitsToSend(int bits) throws Exception {
        if (bits < 0 || bits > 15) {
            Exception exception = new Exception();
            throw exception;
        }
        int valueToSend = 0x30 + bits;
        return valueToSend;
    }

    
    public int getInputPortA() {
        return portA;
    }

   
    public int getInputPortB() {
        return portB;
    }

    
    public void setPortA(int portAnewValue) {
        portA = portAnewValue;
        write();
        read();
    }

   
    public void setPortB(int portBnewValue) {
        portB = portBnewValue;
        write();
        read();
    }

    
    public void setTrisA(int trisAnewValue) {
        trisA = trisAnewValue;
        write();
        read();
    }


    public void setTrisB(int trisBnewValue) {
        trisB = trisBnewValue;
        write();
        read();
    }

    // Methode um die Werte von PortA, TrisA, PortB und TrisB als Byte-Array zu bekommen.
   
    private byte[] getValuesAsByteArray() {
        byte data[] = new byte[9];
        try {
            data[0] = (byte) setBitsToSend(getHighBits(trisA));    
            data[1] = (byte) setBitsToSend(getLowBits(trisA));    
            data[2] = (byte) setBitsToSend(getHighBits(portA));    
            data[3] = (byte) setBitsToSend(getLowBits(portA));    
            data[4] = (byte) setBitsToSend(getHighBits(trisB));   
            data[5] = (byte) setBitsToSend(getLowBits(trisB));    
            data[6] = (byte) setBitsToSend(getHighBits(portB));   
            data[7] = (byte) setBitsToSend(getLowBits(portB));    
            data[8] = (byte) '\r'; // CR
        } catch (Exception ex) {
            System.out.println("Fehler: Tris oder PortWerte ausserhalb des Bereichs");
            ex.printStackTrace();
            System.exit(1);
        }
        return data;
    }

    // Methode zum Senden der Daten
     
    public void write() {
        try {
            OutputStream out = serialPort.getOutputStream();
            out.write(getValuesAsByteArray());
            out.close();
        } catch (IOException ex) {
            System.out.println("Fehler: Outstream konnte nicht geöffent werden!");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Methode zum Einlesen der Daten
     
    public void read() {
        try {
            InputStream in = serialPort.getInputStream();
            byte c = 0;
            int portAB[] = new int[100];
            int counter = 0;
            while (c > -1) {
                c = (byte) in.read();
                if (c < 0) {
                    break;
                }
                portAB[counter] = (int) c;
                counter++;
            }
            if (portAB[0] > 48) {
                decodeData(portAB);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    //  Methode Decodieren der Daten
     
    private void decodeData(int portAB[]) {
        portA = ((portAB[0] - 0x32) << 4) + portAB[1] - 0x30;
        portB = ((portAB[2] - 0x30) << 4) + portAB[3] - 0x30;
    }
}
