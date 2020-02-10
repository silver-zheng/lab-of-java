package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IncomeTable {
	private final StringProperty depName;
	private final StringProperty docID;
	private final StringProperty docName;
	private final StringProperty regType;
	private final StringProperty peopleNum;
	private final StringProperty incomeTotal;
	
	public IncomeTable(String depName,String docID,String docName,String regType,Integer num,Integer income) {
		this.depName = new SimpleStringProperty(this,"depName",depName);
		this.docID = new SimpleStringProperty(this,"docID",docID);
		this.docName = new SimpleStringProperty(this,"docName",docName);
        this.regType = new SimpleStringProperty(this, "regType", regType);
        this.peopleNum = new SimpleStringProperty(this,"peopleNum",num.toString());
        this.incomeTotal = new SimpleStringProperty(this,"inicomeTotal",income.toString());
		
	}

    public final String getDepName() {
        return this.depName.get();
    }
    public final void setDepName(String value) {
        this.depName.set(value);
    }
    public final StringProperty depNameProperty() {
        return this.depName;
    }

    public final String getDocID() {
        return this.docID.get();
    }
    public final void setDocID(String value) {
        this.docID.set(value);
    }
    public final StringProperty docIDProperty() {
        return this.docID;
    }
    
    public final String getDocName() {
        return this.docName.get();
    }
    public final void setDocName(String value) {
        this.docName.set(value);
    }
    public final StringProperty docNameProperty() {
        return this.docName;
    }
    
    public final String getRegType() {
        return this.regType.get();
    }
    public final void setRegType(String value) {
        this.regType.set(value);
    }
    public final StringProperty regTypeProperty() {
        return this.regType;
    }
    
    public final String getPeopleNum() {
        return this.peopleNum.get();
    }
    public final void setPeopleNum(String value) {
        this.peopleNum.set(value);
    }
    public final StringProperty peopleNumProperty() {
        return this.peopleNum;
    }
    
    public final String getIncomeTotal() {
        return this.incomeTotal.get();
    }
    public final void setIncomeTotal(String value) {
        this.incomeTotal.set(value);
    }
    public final StringProperty incomeTotalProperty() {
        return this.incomeTotal;
    }
    
    
}
