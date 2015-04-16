package application;


public class CodeReader {
	
	private int pc;
	private int line;
	private int Code;
	
	public CodeReader() {
		this.pc=0;
		this.line=0;
		this.Code=0;
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


	public void read (){
		System.out.println(this.Code);
		this.pc++;
		
		
	}

}
