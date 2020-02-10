package application;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient {
	public String ID=null;
	public String NAME=null;
	public String PWD=null;	
    public Double MONEY=null;
    public String PingYin=null;
    public String LogTime=null;  
    
    public Patient(String ID, String NAME, String PingYin, String PWD,String MONEY) {
    	this.ID=new String(ID);
    	this.NAME=new String(NAME);
    	this.PingYin=new String(PingYin);    	
    	this.PWD=new String(PWD);
    	this.MONEY=new Double(MONEY);
    	
    }
    public Patient(ResultSet rsbr) {
    	try {
    	    this.ID=new String(rsbr.getString("ID"));
    	    this.NAME=new String(rsbr.getString("NAME"));
    	    this.PingYin=new String(rsbr.getString("PingYin"));    	
    	    this.PWD=new String(rsbr.getString("PWD"));
    	    this.MONEY=rsbr.getDouble("MONEY");
    	    
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
    }
}
