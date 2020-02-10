package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;

public class PatientController {
	@FXML private JFXButton ensure;
	@FXML private JFXButton clear;
	@FXML private JFXButton exit;
	@FXML private JFXComboBox<String> depname;
	@FXML private JFXComboBox<String> docname;
	@FXML private JFXComboBox<String> registertype;
	@FXML private JFXComboBox<String> registername;
	@FXML private JFXTextField shouldpay;
	@FXML private JFXTextField realpay;
	@FXML private Label restmoney;
	@FXML private Label changemoney;
	@FXML private Label registernumber;
	
	private boolean[] canshow = new boolean[4] ;//0->depcanshow,1->doccanshow
	private boolean depfinish = false; 
	private boolean docfinish = false; 
	private boolean regtypefinish = false;
	private boolean regnamefinish = false;
	private Integer money=0;

	private ArrayList<String> depRaw = new ArrayList<String>();
	private ArrayList<String> docRaw = new ArrayList<String>();
	private ArrayList<String> regnameRaw = new ArrayList<String>();
	private ArrayList<String> depFull = new ArrayList<String>();
	private ArrayList<String> docFull = new ArrayList<String>();
	private ArrayList<String> regtypeFull = new ArrayList<String>();
	private ArrayList<String> regnameFull = new ArrayList<String>();
	
	private void filter(String input,ArrayList<String> list,JFXComboBox<String> combox) {
		StringFilter strf = new StringFilter(input,list);
		combox.getItems().removeAll();
		combox.getItems().setAll(strf.filter());
	}
	
	
	public void initialize() {
		canshow[0]=canshow[1]=canshow[2]=canshow[3]=false;
		//��ȡ���ݿ�
    	ResultSet depRes;
    	ResultSet docRes;
    	ResultSet regnameRes;
		try {
			//������Ϣ��ȡ
			depRes = LoginController.stat.executeQuery("SELECT * FROM department");
	        while(depRes.next()){ //get data from a row
	        	depRaw.add(depRes.getString("depID")+" "+depRes.getString("department")+" "
	    	    +depRes.getString("depPingYin"));
	        	depFull.add(depRes.getString("depID")+" "+depRes.getString("department")+" "
	        	+depRes.getString("depPingYin"));
	        };
	        //ҽ����Ϣ��ȡ
	        docRes = LoginController.stat.executeQuery("SELECT department,name,PingYin FROM doctor");
	        while(docRes.next()) {
	        	docRaw.add(docRes.getString("department")+" "+docRes.getString("name")+" "
	    	    +docRes.getString("PingYin"));
	        	docFull.add(docRes.getString("department")+" "+docRes.getString("name")+" "
	        	+docRes.getString("PingYin"));
	        }
	        //�������ƶ�ȡ
	        regnameRes = LoginController.stat.executeQuery("SELECT regname FROM registertype");
	        while(regnameRes.next()) {
	        	regnameRaw.add(regnameRes.getString("regname"));
	        	regnameFull.add(regnameRes.getString("regname"));
	        }
	        //������������ȡֱ�ӳ�ʼ��
	        regtypeFull.add("0 ��ͨ�� PTH");//����ѡ��ҽ����ſ�ѡȡר�Һ�
	        //��ȡ���
	        readRestmoney();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		//����ѡ�񲿷�
		depname.getItems().addAll(depFull);
        depname.setOnKeyReleased(e->{ //��Ӧenter��
        	if(e.getCode()==KeyCode.ENTER)
        		depname.getSelectionModel().select(0);
        });
        depname.getEditor().textProperty().addListener((obs,oldval,newval)->{ //�����ı��仯����
        	Platform.runLater(()->{onCboxTextChanged(depname,depFull,0);});
        });
        depname.getEditor().setOnKeyReleased(e->{ //��ʾ
        	if(depfinish) { //��ǰ�޶������ϣ��ָ�
        		depfinish=false;
        		if(docfinish==false) {
        			depFull.clear();
        			depFull.addAll(depRaw);   			
        			docFull.clear();
        			docFull.addAll(docRaw);
        			docname.getItems().removeAll();
        			docname.getItems().setAll(docFull);
        		}
        	}
        	int itemsnum= depname.getItems().size();
        	if(itemsnum==0||!canshow[0])
        		depname.hide();
        	else {
        		depname.hide();
        		depname.show();
        	}
        });
        depname.getSelectionModel().selectedIndexProperty().addListener(e->onDepSelect());
        
        //ҽ��ѡ�񲿷�
		docname.getItems().addAll(docFull);
        docname.setOnKeyReleased(e->{ //��ӦENTER��
        	if(e.getCode()==KeyCode.ENTER)
        		docname.getSelectionModel().select(0);
        });
        docname.getEditor().textProperty().addListener((obs,oldval,newval)->{ //�����ı��仯����
        	Platform.runLater(()->onCboxTextChanged(docname,docFull,1));
        });
        docname.getEditor().setOnKeyReleased(e->{ //��ʾ
        	if(docfinish) { //ҽ���Կ����Լ����޶���Ҫ���
        		docfinish=false;
        		if(depfinish==false) {
        			docFull.clear();
        			docFull.addAll(docRaw);
        			depFull.clear();
        			depFull.addAll(depRaw);
        			depname.getItems().removeAll();
        			depname.getItems().setAll(depFull);
        		}
        		//���ҽ���Ժ��������޶�
        		regtypeFull.clear();
        		regtypeFull.add("0 ��ͨ�� PTH");
        		registertype.getItems().removeAll();
        		registertype.getItems().setAll(regtypeFull);
        	}
        	int itemsnum= docname.getItems().size();
        	if(itemsnum==0||!canshow[1])
        		docname.hide();
        	else {
        		docname.hide();
        		docname.show();
        	}
        });
        docname.getSelectionModel().selectedIndexProperty().addListener(e->onDocSelect());
       
        //�������ѡ�񲿷�
        registertype.getItems().addAll(regtypeFull);
        registertype.setOnKeyReleased(e->{ 
        	if(e.getCode()==KeyCode.ENTER)
        		registertype.getSelectionModel().select(0);
        });
        registertype.getEditor().textProperty().addListener((obs,oldval,newval)->{ //�����ı��仯����
        	Platform.runLater(()->onCboxTextChanged(registertype,regtypeFull,2));
        });
        registertype.getEditor().setOnKeyReleased(e->{
        		int itemsnum= registertype.getItems().size();
        		if(regtypefinish) {
        			regtypefinish=false;
        			if(regnamefinish==false) {
        				if(regtypeFull.size()==2) regtypeFull.remove(1);
        				regnameFull.clear();
        				regnameFull.addAll(regnameRaw);
        				registername.getItems().removeAll();
        				registername.getItems().setAll(regnameFull);
        			}
        		}
            	if(itemsnum==0||!canshow[2])
            		registertype.hide();
            	else {
            		registertype.show();
            	}
        });
        registertype.getSelectionModel().selectedIndexProperty().addListener(e->onRegtypeSelect());
        
        //��������ѡ�񲿷�
        registername.getItems().addAll(regnameFull);
        registername.setOnKeyReleased(e->{
        	if(e.getCode()==KeyCode.ENTER)
        		registername.getSelectionModel().select(0);
        });
        registername.getEditor().textProperty().addListener((obs,oldval,newval)->{ //�����ı��仯����
        	Platform.runLater(()->onCboxTextChanged(registername,regnameFull,3));
        });
        registername.getEditor().setOnKeyReleased(e->{
        		if(regnamefinish) regnamefinish=false;
        		int itemsnum= registername.getItems().size();
            	if(itemsnum==0||!canshow[3])
            		registername.hide();
            	else {
            		registername.show();
            	}
        });
        registername.getSelectionModel().selectedIndexProperty().addListener(e->onRegnameSelect());
        
        //�Һŷѱ仯������
        shouldpay.textProperty().addListener(lst->{
        	String regfee=shouldpay.getText();
        	if(!regfee.isEmpty()) {
        		if(money>Integer.parseInt(regfee)) {
        			realpay.setDisable(true);
        			restmoney.setStyle("-fx-text-fill: black;");
        		}
        		else {
        			realpay.setDisable(false);
        			restmoney.setStyle("-fx-text-fill: red;");
        		}
        	}
        	else {
        		realpay.setDisable(false);
        		restmoney.setStyle("-fx-text-fill: black");
        	}
        });
	}
	
	
	private void onCboxTextChanged(JFXComboBox<String> combox,ArrayList<String> fullList,int showidx) { 
		String input = combox.getEditor().getText();
		System.out.println("INPUT "+input);//for test
		filter(input,fullList,combox);
		int num=combox.getItems().size(); //��ǰ�б�����
		if(num>0&&!input.isEmpty()) //����ǿ�����ƥ��
			canshow[showidx]=true;
		else {
			combox.getItems().removeAll();
			combox.getItems().setAll(fullList);
			canshow[showidx]=false;
		}	
	}
	private void onDepSelect() { //ѡ�����ң���ҽ�����й���
		if(depname.getSelectionModel().getSelectedIndex()>=0) {
			depfinish=true;
			String deptext = getDepartment(depname.getEditor().getText());
			docFull = (new StringFilter(deptext,docRaw)).filter();
			docname.getItems().removeAll();
			docname.getItems().setAll(docFull);
		}
	}
	
	private void onDocSelect() { //ѡ����ҽ�����Կ����Լ�����������
		if(docname.getSelectionModel().getSelectedIndex()>=0) {
			docfinish=true;
			String docinfo = docname.getEditor().getText();
			String doctext =docinfo.substring(0,4);
			depFull = (new StringFilter(doctext,depRaw)).filter();
			depname.getItems().removeAll();
			depname.getItems().setAll(depFull);
			//���˺������
			regtypeRefresh(docinfo);
		}
	}
	
	private void regtypeRefresh(String docinfo) {//���˺������
		int isspecialist = 0;
		try {
			ResultSet speRes;
			speRes = LoginController.stat.executeQuery("SELECT specialist FROM doctor where name='"+getDoctor(docinfo)+"'");
			speRes.next();
			isspecialist=speRes.getInt("specialist");
		} catch (SQLException e) {e.printStackTrace();}
		if(isspecialist==1) {
			if(regtypeFull.size()==1) 
				regtypeFull.add("1 ר�Һ� ZJH");
		}
		else {
			if(regtypeFull.size()==2) 
				regtypeFull.remove(1);
		}
		registertype.getItems().removeAll();
		registertype.getItems().setAll(regtypeFull);
	}
	
	private void onRegtypeSelect() { //ѡ���˺������,����ѡ���������
		if(registertype.getSelectionModel().getSelectedIndex()>=0) {
			regtypefinish = true;
			String reginfo =getDepartment(depname.getEditor().getText())+getRegtype(registertype.getEditor().getText());
			regnameFull = new StringFilter(reginfo,regnameRaw).filter();
			registername.getItems().removeAll();
			registername.getItems().setAll(regnameFull);
			if(regnameFull.size()==1)
				registername.getSelectionModel().clearAndSelect(0);
		}
	}
	
	private void onRegnameSelect() { //ѡ���˺������ƣ������Һŷ�
		if(registername.getSelectionModel().getSelectedIndex()>=0) {
			regnamefinish=true;
			String reginfo=registername.getEditor().getText();
			ResultSet regres;
			try {
				regres = LoginController.stat.executeQuery("SELECT fee FROM registertype where regname='"+reginfo+"'");
				regres.next();
				Integer fee=regres.getInt("fee");
				shouldpay.setText(fee.toString());
				if(money<fee) { //���С�ڹҺŷ�
					restmoney.setStyle("-fx-text-fill: red;");
					realpay.setDisable(false);
				}
				else {
					restmoney.setStyle("-fx-text-fill: black;");
					realpay.setDisable(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//��ť����
	public void onClear(ActionEvent e) {
		canshow[0]=canshow[1]=canshow[2]=canshow[3]=false;
		depfinish = false;
		docfinish = false; 
		regtypefinish = false;
		regnamefinish = false;
		readRestmoney();
		depFull.clear();
		docFull.clear();
		regtypeFull.clear();
		regnameFull.clear();
		depFull.addAll(depRaw);
		docFull.addAll(docRaw);
		regtypeFull.add("0 ��ͨ�� PTH");
		regnameFull.addAll(regnameRaw);
		docname.getItems().removeAll();
		docname.getItems().setAll(docFull);
		docname.getEditor().clear();
		depname.getItems().removeAll();
		depname.getItems().setAll(depFull);
		depname.getEditor().clear();
		registertype.getItems().removeAll();
		registertype.getItems().setAll(regtypeFull);
		registertype.getEditor().clear();
		registername.getItems().removeAll();
		registername.getItems().setAll(regnameFull);
		registername.getEditor().clear();
		shouldpay.clear();
		realpay.clear();
		restmoney.setStyle("-fx-text-fill: black;");
		changemoney.setText("");
		registernumber.setText("");
	}
	
	public void onExit() {
		Main.appStage.setScene(Main.scene);
	}
	
	public void onRegister() {
		if(depfinish==false||docfinish==false||regtypefinish==false||regnamefinish==false) {
			Alert inputFailAlert = new Alert(Alert.AlertType.INFORMATION,"�Һ���Ϣ��д����!!!\n",ButtonType.OK);
			inputFailAlert.show();
			return;
		}
		Integer regfee = Integer.parseInt(shouldpay.getText());//Ӧ�����
		Integer realfee = 0;
		Integer changefee = 0; 
		Integer regnumber = 0;
		boolean shouldchange =  false;
		if(money<regfee) { //����Һŷ�
			try{
				realfee = Integer.parseInt(realpay.getText());//ʵ�����
			}catch(Exception exc) {
				Alert payFailAlert = new Alert(Alert.AlertType.INFORMATION,"δ����ʵ����������Ƿ�!!!\n",ButtonType.OK);
				payFailAlert.show();
				return;
			}
			if(realfee<regfee) { //ʵ������
				Alert payFailAlert = new Alert(Alert.AlertType.INFORMATION,"ʵ������!!!\n",ButtonType.OK);
				payFailAlert.show();
				return;
			}
		}
		
		String docinput = getDoctor(docname.getEditor().getText());
		String regnameinput = registername.getEditor().getText();
		//���ø��뼶ΪSERAILIZABLE
		try {
			LoginController.stat.getConnection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		try {
			LoginController.stat.getConnection().setAutoCommit(false);
			String regtypeID = getRegtypeID(regnameinput);
			String docID = getDocID(docinput);
			String patientID = getPatientID(LoginController.user);
			Integer peoplenum = getPeoplenum(regtypeID);
			Integer quit = 0;
			regnumber = getRegnumber();
			//debug
			System.out.println("regnumber "+regnumber);
			Pair<Integer,Integer> p = getRegfeeAndLimits(regtypeID);
			regfee = p.getKey();
			Integer limits = p.getValue();
			System.out.println("!!!the peoplenum is "+peoplenum+" and limits is "+limits);
			if(peoplenum>limits) {
				Alert regFailAlert = new Alert(Alert.AlertType.INFORMATION,"�ú����ѹ���!!!\n",ButtonType.OK);
				regFailAlert.show();
				return;
			}
			if(money<regfee&&realfee<regfee) {
				Alert regFailAlert = new Alert(Alert.AlertType.INFORMATION,"�Һŷ��ѱ���������¹Һ�!!!\n",ButtonType.OK);
				regFailAlert.show();
				return;
			}
			if(money>=regfee) {
				LoginController.stat.execute("UPDATE patient SET money = money - "+regfee+" where PatientID='"+
				patientID+"'");
				restmoney.setText(Integer.toString(money-=regfee));
				if(money<regfee) {
					restmoney.setStyle("-fx-text-fill: red;");
					realpay.setDisable(false);
				}
				else restmoney.setStyle("-fx-text-fill: black;");
			}
			else {
				changefee = realfee-regfee;
				shouldchange = true;
			}
			String sql="INSERT INTO reginfo VALUES(?,?,?,?,?,?,?,NOW(),NULL)";
			PreparedStatement pstmt = LoginController.stat.getConnection().prepareStatement(sql);
			pstmt.setString(1,String.format("%06d",regnumber));
			pstmt.setString(2,regtypeID);
			pstmt.setString(3,docID);
			pstmt.setString(4, patientID);
			pstmt.setInt(5, peoplenum);
			pstmt.setInt(6, quit);
			pstmt.setDouble(7, regfee);
			pstmt.execute();
			LoginController.stat.getConnection().commit();
			if(shouldchange) changemoney.setText(Integer.toString(changefee));
			registernumber.setText(String.format("%06d", regnumber));
		} catch (SQLException e) {
			try {
				LoginController.stat.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	    finally {
	    	try {
				LoginController.stat.getConnection().setAutoCommit(true);
				LoginController.stat.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	}
	
	
	//��������
	private String getDepartment(String fullinfo) {
		if(fullinfo==null||fullinfo.isEmpty()) return "";
		int i,j;
		for(i=0;i<fullinfo.length()&&fullinfo.charAt(i)!=' ';i++) ;
		if(i>=fullinfo.length()) return "";
		for(j=i+1;j<fullinfo.length()&&fullinfo.charAt(j)!=' ';j++);
		if(j>=fullinfo.length()) return "";
		return fullinfo.substring(i+1,j);
	}
	private String getDoctor(String fullinfo) {
		return getDepartment(fullinfo);
	}
	private String getRegtype(String fullinfo) {
		return getDepartment(fullinfo);
	}
	private String getDocID(String doctor) {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select DocID from doctor where name='"+doctor+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	private String getPatientID(String user) {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select PatientID from patient where name='"+user+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	private String getRegtypeID(String regname) {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select regID from registertype where regname='"+regname+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	private int getPeoplenum(String regnameid) {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select COUNT(*) from reginfo where regtypeID='"+regnameid+"'");
			rs.next();
			return rs.getInt(1)+1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	private Pair<Integer,Integer> getRegfeeAndLimits(String regtypeid) {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select fee,limits from registertype where regID='"+regtypeid+"'");
			rs.next();
			return new Pair<Integer,Integer>(rs.getInt(1),rs.getInt(2));
		} catch (SQLException e) {
			e.printStackTrace();
			return new Pair<Integer,Integer>(-1,-1);
		}
	}
	private int getRegnumber() {
		try {
			ResultSet rs = LoginController.stat.executeQuery("select COUNT(*) from reginfo");
			rs.next();
			return rs.getInt(1)+1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	private void readRestmoney() {
        ResultSet moneyRes;
		try {
			moneyRes = LoginController.stat.executeQuery("SELECT money FROM patient where name='"
			+LoginController.user+"' OR PingYin='"+LoginController.user+"'");
			moneyRes.next();
			money=moneyRes.getInt("money");
			restmoney.setText(money.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
