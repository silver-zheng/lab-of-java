package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DoctorController {
	private TabPane docTabPane;
	@FXML private TableView<PatientTable> PatientTable;
	@FXML private TableColumn<PatientTable,String> regID;
	@FXML private TableColumn<PatientTable,String> patientName;
	@FXML private TableColumn<PatientTable,String> regDateTime;
	@FXML private TableColumn<PatientTable,String> regType;
	@FXML private TableView<IncomeTable> IncomeTable;
	@FXML private TableColumn<IncomeTable,String> depName;
	@FXML private TableColumn<IncomeTable,String> docID;
	@FXML private TableColumn<IncomeTable,String> docName;
	@FXML private TableColumn<IncomeTable,String> InRegType;
	@FXML private TableColumn<IncomeTable,String> peopleNum;
	@FXML private TableColumn<IncomeTable,String> incomeTotal;
	@FXML private JFXButton exitBt;
	@FXML private JFXButton freshBt;
	@FXML private DatePicker beginDate;
	@FXML private DatePicker endDate;
	@FXML private JFXTimePicker beginTime;
	@FXML private JFXTimePicker endTime;
	@FXML private Tab incomeTab;
	@FXML private Tab patientTab;
	ObservableList<PatientTable> patientList;
	ObservableList<IncomeTable> incomeList;
	
	public void initialize() {
		
		regID.setCellValueFactory(new PropertyValueFactory<PatientTable,String>("regID"));
		patientName.setCellValueFactory(new PropertyValueFactory<PatientTable,String>("patientName"));
		regDateTime.setCellValueFactory(new PropertyValueFactory<PatientTable,String>("regDateTime"));
		regType.setCellValueFactory(new PropertyValueFactory<PatientTable,String>("regType"));
		patientList = FXCollections.observableArrayList();
		try {
			ResultSet patientRS = LoginController.stat.executeQuery("select regnumber,patient.name,regtime,registertype.regtype " + 
					" from doctor,reginfo,registertype,patient " + 
					" where doctor.DocID=reginfo.docID AND " + 
					" registertype.regID=reginfo.regtypeID AND " + 
					" reginfo.patientID = patient.PatientID AND " + 
					" (doctor.name = '"+LoginController.user+"' OR doctor.PingYin = '"+LoginController.user+ "');");
			while(patientRS.next()) {
				patientList.add(new PatientTable(patientRS.getString(1),patientRS.getString(2),patientRS.getString(3),patientRS.getInt(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		depName.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("depName"));
		docID.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("docID"));
		docName.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("docName"));
		InRegType.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("regType"));
		peopleNum.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("peopleNum"));
		incomeTotal.setCellValueFactory(new PropertyValueFactory<IncomeTable,String>("incomeTotal"));
		incomeList =  FXCollections.observableArrayList();
		fresh(0,"","");
		
		PatientTable.setItems(patientList);
		IncomeTable.setItems(incomeList);
		
		//设置初始的病人列表页面时间选择为灰色
		beginDate.setDisable(true);
		beginTime.setDisable(true);
		endDate.setDisable(true);
		endTime.setDisable(true);
		freshBt.setDisable(true);
		
		patientTab.setOnSelectionChanged(e->{
			beginDate.setDisable(true);
			beginTime.setDisable(true);
			endDate.setDisable(true);
			endTime.setDisable(true);
			freshBt.setDisable(true);
		});
		incomeTab.setOnSelectionChanged(e->{
			beginDate.setDisable(false);
			beginTime.setDisable(false);
			endDate.setDisable(false);
			endTime.setDisable(false);
			freshBt.setDisable(false);
		});
		
		//退出按钮
		exitBt.setOnAction(e->Main.appStage.setScene(Main.scene));
		freshBt.setOnAction(e->onFresh());
	}
	
	
	private void fresh(int mode,String begintime,String endtime) {//0->默认模式 1->用户指令时间模式
		ResultSet incomeRS;
		try {
			if(mode==0) incomeRS = LoginController.stat.executeQuery("select   doctor.department,doctor.docID,doctor.name,doctor.specialist,"+
					"COUNT(*) AS num,SUM(reginfo.regfee) AS income from" + 
					" doctor,reginfo where doctor.DocID= reginfo.docID AND" +
					" reginfo.regtime between CURDATE() AND NOW() " + 
					" group by reginfo.regtypeID,reginfo.docID ;");
			else incomeRS = LoginController.stat.executeQuery("select   doctor.department,doctor.docID,doctor.name,doctor.specialist,"+
					"COUNT(*) AS num,SUM(reginfo.regfee) AS income from" + 
					" doctor,reginfo where doctor.DocID= reginfo.docID AND" +
					" reginfo.regtime between '"+begintime+"' AND '" +endtime+ 
					"' group by reginfo.regtypeID,reginfo.docID ;");
			incomeList.clear();
			while(incomeRS.next()) {
				incomeList.add(new IncomeTable(incomeRS.getString(1),incomeRS.getString(2),incomeRS.getString(3),incomeRS.getString(4),
						incomeRS.getString(5),incomeRS.getString(6)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void onFresh() {
		LocalDate beginDay = beginDate.getValue();
		LocalDate endDay = endDate.getValue();
		LocalTime beginT = beginTime.getValue();
		LocalTime endT = endTime.getValue();
		
		if(beginDay==null&&endDay==null&&beginT==null&&endT==null) {//未输入做默认情况
			fresh(0,"","");
		}
		else if(beginDay!=null&&endDay!=null&&beginT!=null&&endT!=null) {//输入了全部信息
			if(beginDay.isBefore(endDay)||(beginDay.isEqual(endDay)&&!beginT.isAfter(endT))) {
				fresh(1,beginDay.toString()+" "+beginT.toString(),endDay.toString()+" "+endT.toString());
			}
			else { //输入时间矛盾
				Alert logFailAlert = new Alert(Alert.AlertType.INFORMATION,"开始时间不能比截止时间晚!\n",ButtonType.OK);
				logFailAlert.setTitle("刷新失败");
				logFailAlert.show();
			}
		}
		else {//输入部分信息
			Alert logFailAlert = new Alert(Alert.AlertType.INFORMATION,"输入时间不完全!\n",ButtonType.OK);
			logFailAlert.setTitle("刷新失败");
			logFailAlert.show();
		}
	}
}
