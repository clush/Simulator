package application;

import javafx.scene.control.CheckBox;

public class TextClass {
	  private CheckBox column0;
	  private String column1;
	  private String column2;
	  private String column3;
	    


	  @Override
	public String toString() {
		return "ValueClass [column1=" + column1 + ", column2=" + column2
				+ ", column3=" + column3 + "]";
	}

	public TextClass(String column1, String column2, String column3) {
	    this.column0 = new CheckBox();
		this.column1 = column1;
	    this.column2 = column2;
	    this.column3 = column3;
	  }

	
	public void setColumn0(CheckBox column0) {
		this.column0 = column0;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public void setColumn3(String column3) {
		this.column3 = column3;
	}

	public CheckBox getColumn0() {
			return column0;
		}
	
	
	public String getColumn1() {
	    return column1;
	  }

	  public String getColumn2() {
	    return column2;
	  }

	  public String getColumn3() {
	    return column3;
	  }

}

	
