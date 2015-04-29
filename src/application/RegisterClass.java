package application;

public class RegisterClass {
	private String[] spalten = new String[17];
	

	public RegisterClass(String x0,String x1,String x2,String x3,String x4,String x5,String x6,String x7,String x8,String x9,String xA,String xB,String xC,String xD,String xE,String xF,String beschriftung) {
		
		this.spalten[0] = x0;
		this.spalten[1] = x1;
		this.spalten[2] = x2;
		this.spalten[3] = x3;
		this.spalten[4] = x4;
		this.spalten[5] = x5;
		this.spalten[6] = x6;
		this.spalten[7] = x7;	
		this.spalten[8] = x8;
		this.spalten[9] = x9;
		this.spalten[10] = xA;
		this.spalten[11] = xB;
		this.spalten[12] = xC;
		this.spalten[13] = xD;
		this.spalten[14] = xE;
		this.spalten[15] = xF;
		this.spalten[16] = beschriftung;
		
	}
	
	public void setSpalten(int x,int wert) {
		this.spalten[x] = Integer.toHexString(wert&0xFF);
	}
	
	public int getSpalten(int x) {
		return Integer.parseInt(spalten[x],16);
	}

	public String getBeschriftung() {
		return spalten[16];
	}
	
	public String getSpalte0() {
		if (Integer.valueOf(spalten[0],16)<=0xF)return 0+spalten[0].toUpperCase();
		else return spalten[0].toUpperCase();
	}
	
	public String getSpalte1() {
		if (Integer.valueOf(spalten[1],16)<=0xF)return 0+spalten[1].toUpperCase();
		else return spalten[1].toUpperCase();
	}
	
	public String getSpalte2() {
		if (Integer.valueOf(spalten[2],16)<=0xF)return 0+spalten[2].toUpperCase();
		else return spalten[2].toUpperCase();
	}
	
	public String getSpalte3() {
		if (Integer.valueOf(spalten[3],16)<=0xF)return 0+spalten[3].toUpperCase();
		else return spalten[3].toUpperCase();
	}
	
	public String getSpalte4() {
		if (Integer.valueOf(spalten[4],16)<=0xF)return 0+spalten[4].toUpperCase();
		else return spalten[4].toUpperCase();
	}
	
	public String getSpalte5() {
		if (Integer.valueOf(spalten[5],16)<=0xF)return 0+spalten[5].toUpperCase();
		else return spalten[5].toUpperCase();
	}
	
	public String getSpalte6() {
		if (Integer.valueOf(spalten[6],16)<=0xF)return 0+spalten[6].toUpperCase();
		else return spalten[6].toUpperCase();
	}
	
	public String getSpalte7() {
		if (Integer.valueOf(spalten[7],16)<=0xF)return 0+spalten[7].toUpperCase();
		else return spalten[7].toUpperCase();
	}
	
	public String getSpalte8() {
		if (Integer.valueOf(spalten[8],16)<=0xF)return 0+spalten[8].toUpperCase();
		else return spalten[8].toUpperCase();
	}
	
	public String getSpalte9() {
		if (Integer.valueOf(spalten[9],16)<=0xF)return 0+spalten[9].toUpperCase();
		else return spalten[9].toUpperCase();
	}
	
	public String getSpalteA() {
		if (Integer.valueOf(spalten[10],16)<=0xF)return 0+spalten[10].toUpperCase();
		else return spalten[10].toUpperCase();
	}
	
	public String getSpalteB() {
		if (Integer.valueOf(spalten[11],16)<=0xF)return 0+spalten[11].toUpperCase();
		else return spalten[11].toUpperCase();
	}
	
	public String getSpalteC() {
		if (Integer.valueOf(spalten[12],16)<=0xF)return 0+spalten[12].toUpperCase();
		else return spalten[12].toUpperCase();
	}
	
	public String getSpalteD() {
		if (Integer.valueOf(spalten[13],16)<=0xF)return 0+spalten[13].toUpperCase();
		else return spalten[13].toUpperCase();
	}
	
	public String getSpalteE() {
		if (Integer.valueOf(spalten[14],16)<=0xF)return 0+spalten[14].toUpperCase();
		else return spalten[14].toUpperCase();
	}
	
	public String getSpalteF() {
		if (Integer.valueOf(spalten[15],16)<=0xF)return 0+spalten[15].toUpperCase();
		else return spalten[15].toUpperCase();
	}	
	
}
