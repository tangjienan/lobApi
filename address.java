/**
 * Created by donezio on 10/12/17.
 */
public class address {
    String name = "";
    String addressLine1 = null;
    String addressLine2 = null;
    String city = null;
    String state = null;
    String zipCode = null;
    String message = null;


    public void setName(String str){
        this.name = str;
    }
    public void setAddress1(String str){
        this.addressLine1 = str;
    }
    public void setAddress2(String str){
        this.addressLine2 = str;
    }
    public void setCity(String str){
        this.city = str;
    }
    public void setState(String str){
        this.state = str;
    }
    public void setZipCode(String str){
        this.zipCode = str;
    }
    public void setMessage(String str){
        this.message = str;
    }

    public String getAddress(){
        String fullAddress = this.addressLine1.replace(" ","");
        if(this.addressLine2 != null){
            fullAddress += this.addressLine2.replace(" ","");;
        }
        return fullAddress + "," + this.city.replace(" ","") + "," + this.state.replace(" ","") + "," + this.zipCode;
    }
    public String getName(){
        return this.name;
    }
    public String getState(){
        return this.state;
    }
    public String getZipCode(){
        return this.zipCode;
    }
    public String getCity(){
        return this.city;
    }
    public String getAddressLine1(){
        return this.addressLine1;
    }
    public String getAddressLine2(){
        return this.addressLine2;
    }
    public String getMessage() {
        return this.message;
    }
}

