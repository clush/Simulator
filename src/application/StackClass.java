package application;

public class StackClass {
	private String beschriftung;
	private String stack;

	
	
	public StackClass(String stack, String beschriftung) {
		this.stack = stack;
		this.beschriftung = beschriftung;
	}
	
	public String getBeschriftung() {
		return beschriftung;
	}
	


	public String getStack() {
		if (Integer.valueOf(stack,16)<=0xF)return 0+stack.toUpperCase();
		else return stack.toUpperCase();
	}
	
	public int getStackInd(){
		return Integer.parseInt(stack,16);
	}

	public void setStack(int stackInt) {
		stack = Integer.toHexString(stackInt&0xFF);
	}
	
	
}
