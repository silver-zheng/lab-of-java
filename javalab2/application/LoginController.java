package application;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController {
	@FXML private JFXComboBox<String> username;
	@FXML private JFXPasswordField password;
	@FXML private RadioButton docRadioBt;
	@FXML private RadioButton patientRadioBt;
	@FXML private JFXButton login_bt;
	@FXML private JFXButton exit_bt;
	private ArrayList<String> PatientNames = new ArrayList<String>();
	private ArrayList<String> PatientPingYin = new ArrayList<String>();
	private ArrayList<String> DoctorNames = new ArrayList<String>();
	private ArrayList<String> DoctorPingYin = new ArrayList<String>();
//	private Statement stat=null;
	private boolean docSel = true;
	private boolean canshow = false;

	public static Statement stat = null;
	public static String user = null;
	private void filter(String input) {
		char chr = input.charAt(0);
		boolean isPingYin = chr>='a'&&chr<='z'||chr>='A'&&chr<='Z';
		if(docSel) { //身份是医生
			if(isPingYin) { //输入拼音
				StringFilter strf = new StringFilter(input,DoctorPingYin);
				username.getItems().removeAll();
				username.getItems().setAll(strf.filter());
			}
			else { //输入姓名
				StringFilter strf = new StringFilter(input,DoctorNames);
				username.getItems().removeAll();
				username.getItems().setAll(strf.filter());
			}
		}
		else { //身份是患者
			if(isPingYin) { //患者拼音
				StringFilter strf = new StringFilter(input,PatientPingYin);
				username.getItems().removeAll();
				username.getItems().setAll(strf.filter());
			}
			else {//患者姓名
				StringFilter strf = new StringFilter(input,PatientNames);
				username.getItems().removeAll();
				username.getItems().setAll(strf.filter());
			}
		}
	}
	public void initialize() {
		//read data from DB first
		Connect connector =  new Connect();
		try{stat = connector.GetConnect(Connect.URL, Connect.Admin,Connect.AdminPwd);}
		catch(Exception e) {
			e.printStackTrace();
		}
        try {
        	ResultSet PatientRes = stat.executeQuery("SELECT name,PingYin FROM patient");
	        while(PatientRes.next()){ //get data from a row
	        	PatientNames.add(PatientRes.getString("name"));
	        	PatientPingYin.add(PatientRes.getString("PingYin"));
	        };
	        ResultSet DoctorRes = stat.executeQuery("SELECT name,PingYin FROM doctor");
	        while(DoctorRes.next()) {
	        	DoctorNames.add(DoctorRes.getString("name"));
	        	DoctorPingYin.add(DoctorRes.getString("PingYin"));
	        }
        }catch(SQLException e) {
        	e.printStackTrace();
        }

        username.getItems().addAll(DoctorNames);
        username.setOnKeyReleased(e->{
        	if(e.getCode()==KeyCode.ENTER)
        		username.getSelectionModel().select(0);
        });
        username.getEditor().textProperty().addListener(listener->{ //监听文本变化
        	Platform.runLater(()->onCboxTextChanged());
        });
        username.getEditor().setOnKeyReleased(e->{
        	System.out.println("haha combox detect key Released"); //设置显示
        	int itemsnum= username.getItems().size();
        	if(itemsnum==0||!canshow)
        		username.hide();
        	else {
        		username.show();
        	}
        });
	}
	public void onLog(ActionEvent e) {
		user=username.getEditor().getText();//input username
		String pwd =password.getText();//input password
		String realPwd = null;
		String Sql= null;
		ResultSet rs = null;
		if(docSel) Sql="SELECT password FROM doctor where name = '"+user+"' OR PingYin = '"+user+"'";
		else Sql="SELECT password FROM patient where name = '"+user+"' OR PingYin = '"+user+"'";
		try {
			rs = stat.executeQuery(Sql);
			while(rs.next()) {
				realPwd = rs.getString("password");
			}
		}catch(SQLException doce) {
			doce.printStackTrace();
		}
		if(pwd.equals(realPwd)) { //验证成功
			if(docSel) {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("Doctor.fxml"));
					Scene scene = new Scene(root,600,450);
					Main.appStage.setScene(scene);
					Main.appStage.setResizable(false);
					Main.appStage.setTitle("医生登陆");
					Main.appStage.show();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			else {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("Patient.fxml"));
					Scene scene = new Scene(root,600,350);
					Main.appStage.setScene(scene);
					Main.appStage.setResizable(false);
					Main.appStage.setTitle("病人挂号");
					Main.appStage.show();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			
		}
		else {
			//inform user of selecting their identity;
			Alert logFailAlert = new Alert(Alert.AlertType.INFORMATION,"密码错误，请重新输入!\n",ButtonType.OK);
			logFailAlert.setTitle("登陆失败");
			logFailAlert.show();	
		}
	}
	
	public void onCboxTextChanged() { 
		int num=username.getItems().size(); //当前列表项数
		String input = username.getEditor().getText();
		System.out.println(num);
		System.out.println(input);
		if(num>0&&!input.isEmpty()) {
			filter(input);
			canshow=true;
			System.out.println("filter finished");//for test
		}
		else {
			if(docSel)
				onDoc();
			else onPat();
			canshow=false;
		}	
	}
	public void onExit() {
		Stage stage = (Stage)exit_bt.getScene().getWindow();
		stage.close();
		System.exit(0);
	}
	
	public void onDoc() {
		docSel=true;
		username.getItems().removeAll(username.getItems());
		username.getItems().addAll(DoctorNames);
	}
	
	public void onPat() {
		docSel=false;
		username.getItems().removeAll(username.getItems());
		username.getItems().addAll(PatientNames);
	}
	 
}

