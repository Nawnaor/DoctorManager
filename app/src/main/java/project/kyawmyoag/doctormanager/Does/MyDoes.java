package project.kyawmyoag.doctormanager.Does;

public class MyDoes {

    String CustomerName;
    String PhoneNo;
    String Customerpass;
    String About;
    String IMEI;
    String Date;
    String Time;
    String Service;
    String Fee;
    String Condition;
    String keydoes;



    public MyDoes() {

    }

    public MyDoes(String CustomerName,String PhoneNo,String Customerpass,String About,String IMEI,String Date,
                  String Time,String Service,String Fee,String Condition,String keydoes) {
        this.CustomerName = CustomerName;
        this.PhoneNo = PhoneNo;
        this.Customerpass = Customerpass;
        this.About = About;
        this.IMEI = IMEI;
        this.Date = Date;
        this.Time = Time;
        this.Service = Service;
        this.Fee = Fee;
        this.Condition = Condition;
        this.keydoes = keydoes;
    }


    public String getKeydoes() {
        return keydoes;
    }
    public void setKeydoes(String keydoes) {
        this.keydoes=keydoes;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String addCustomerName) {
        this.CustomerName=addCustomerName;
    }

    public String getPhoneNo(){
        return PhoneNo;
    }
    public void setPhoneNo(String addPhoneNo) {
        this.PhoneNo=addPhoneNo;
    }

    public String getCustomerpass(){
        return Customerpass;
    }
    public void setCustomerpass(String addCustomerpass) {
        this.Customerpass=addCustomerpass;
    }

    public String getAbout(){
        return About;
    }
    public void setAbout(String addAbout) {
        this.About=addAbout;
    }

    public String getIMEI() {
        return IMEI;
    }
    public void setIMEI(String addIMEI) {
        this.IMEI = addIMEI;
    }

    public String getDate(){
        return Date;
    }
    public void setDate(String addDate) {
        this.Date=addDate;
    }

    public String getTime(){
        return Time;
    }
    public void setTime(String addTime) {
        this.Time=addTime;
    }

    public String getService(){
        return Service;
    }
    public void setService(String addService) {
        this.Service=addService;
    }

    public String getFee(){
        return Fee;
    }
    public void setFee(String addFee) {
        this.Fee=addFee;
    }

    public String getCondition() {
        return Condition;
    }
    public void setCondition(String addCondition) {
        this.Condition=addCondition;
    }
}
