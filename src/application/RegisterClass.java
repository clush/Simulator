package application;

public class RegisterClass {
	private String[] spalten = new String[9];
	

	public RegisterClass(String x0,String x1,String x2,String x3,String x4,String x5,String x6,String x7,String x8) {
		
		this.spalten[0] = x0;
		this.spalten[1] = x1;
		this.spalten[2] = x2;
		this.spalten[3] = x3;
		this.spalten[4] = x4;
		this.spalten[5] = x5;
		this.spalten[6] = x6;
		this.spalten[7] = x7;	
		this.spalten[8] = x8;
	}


	public String getBeschriftung() {
		return spalten[8];
	}
	
	public String getSpalte0() {
		return spalten[0];
	}
	
	public String getSpalte1() {
		return spalten[1];
	}
	
	public String getSpalte2() {
		return spalten[2];
	}
	
	public String getSpalte3() {
		return spalten[3];
	}
	
	public String getSpalte4() {
		return spalten[4];
	}
	
	public String getSpalte5() {
		return spalten[5];
	}
	
	public String getSpalte6() {
		return spalten[6];
	}
	
	public String getSpalte7() {
		return spalten[7];
	}
	

	public String getSpalten(int x) {
		return spalten[x];
	}

	public void setSpalten(int x,String wert) {
		this.spalten[x] = wert;
	}
	
	
}
