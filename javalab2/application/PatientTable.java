package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PatientTable {
	private final StringProperty regID;
	private final StringProperty patientName;
	private final StringProperty regDateTime;
	private final StringProperty regType;
	
	public PatientTable(String regID,String patientName,String regDateTime,int regType) {
        this.regID = new SimpleStringProperty(this, "regID", regID);
        this.patientName = new SimpleStringProperty(this,"patientName", patientName);
        this.regDateTime = new SimpleStringProperty(this,"regDateTime",regDateTime);
        this.regType = new SimpleStringProperty(this,"regType",regType==1?"×¨¼ÒºÅ":"ÆÕÍ¨ºÅ");
    }

    public final String getRegID() {
        return this.regID.get();
    }
    public final void setRegID(String value) {
        this.regID.set(value);
    }
    public final StringProperty regIDProperty() {
        return this.regID;
    }

    public final String getPatientName() {
        return this.patientName.get();
    }
    public final void setPatientName(String value) {
        this.patientName.set(value);
    }
    public final StringProperty patientNameProperty() {
        return this.patientName;
    }
    
    public final String getRegDateTime() {
        return this.regDateTime.get();
    }
    public final void setRegDateTime(String value) {
        this.regDateTime.set(value);
    }
    public final StringProperty regDateTimeProperty() {
        return this.regDateTime;
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
}
