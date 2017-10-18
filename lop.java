/**
 * Created by donezio on 10/11/17.
 */
import com.lob.Lob;
import com.lob.model.Address;
import com.lob.model.Letter;
import com.lob.net.LobResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class lop {
   
    
    /*
     this part is removed to the config.txt
     */
     
     
    public static void main(String[] args) throws Exception {


        address senderAddress = new address();
        address  receiverAddress = new address();

        getInput(senderAddress);

        /*
            send request using google API
         */


        JSONObject res = null;
        try {
            res = getRequest(senderAddress);
        }
        catch (Exception e){
            System.out.println("error in GET request\n");
            System.out.println(e);
            return;
        }

        try{
            boolean flag = getDestin(res,receiverAddress);
            if(flag == false) return;
        }
        catch (Exception e){
            System.out.println("no official is found\n");
            System.out.println(e);
            return;
        }


        // sending message
        Letter letter = null;
        try {
            letter = sendLetter(senderAddress, receiverAddress);
        }
        catch (Exception e){
            System.out.println("Error in sending letter\n");
            System.out.println(e);
            return;
        }

        try{
            downloadPDF(letter);
        }
        catch (Exception e){
            System.out.println("Error in creating PDF\n");
            System.out.println(e);
            return;
        }

    }

    //read input from command Line
    public static void getInput(address tmp) {
        Scanner read  = new Scanner(System.in);

        System.out.println("Enter your name : \n");
        String str = read.nextLine();
        while(str.length() == 0) {
            System.out.println("length of name is zero, please enter again\n");
            str = read.nextLine();
        }
        tmp.setName(str);


        System.out.println("Enter your address line 1 : \n");
        str = read.nextLine();
        while(str.length() == 0) {
            System.out.println("length of address line 1 is zero, please enter again\n");
            str = read.nextLine();
        }
        tmp.setAddress1(str);


        System.out.println("\nEnter your address line 2 : \n");
        str = read.nextLine();
        tmp.setAddress2(str);


        System.out.println("\nEnter your city : \n");
        str = read.nextLine();
        while(str.length() == 0) {
            System.out.println("length of city is zero, please enter again\n");
            str = read.nextLine();
        }
        tmp.setCity(str);


        System.out.println("\nEnter your state : \n");
        str = read.nextLine();
        while(str.length() == 0) {
            System.out.println("length of state is zero, please enter again\n");
            str = read.nextLine();
        }
        tmp.setState(str);


        System.out.println("\nEnter your zip code : \n");
        str = read.nextLine();
        String regex = "[0-9]+";

        while(str.length() == 0 || !str.matches(regex)) {
            System.out.println("invalid zipcode, please enter again\n");
            str = read.nextLine();
        }
        tmp.setZipCode(str);


        System.out.println("\nEnter your message (200 words) hit enter twice to finish: \n");
        String msg = "";
        while( read.hasNextLine()){
            String nextLine = read.nextLine();
            //System.out.println(nextLine);
            if( nextLine.equals("")){
                break;
            }
            msg += nextLine + '\n';
        }
        tmp.setMessage(msg);
    }

    //send get request
    public static JSONObject getRequest(address fromAddress) throws Exception{
        System.out.println(urlString + "?key=" + apiKey +"&address="+fromAddress.getAddress() + "&roles=legislatorUpperBody");
        URL url = new URL(urlString + "?key=" + apiKey +"&address=" + fromAddress.getAddress() + "&roles=legislatorUpperBody");
        HttpURLConnection con =(HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("response code" + responseCode);
        BufferedReader rd  = null;
        StringBuilder sb = null;
        String line = null;
        if(responseCode == 200) rd  = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else rd = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line + "\n");
        }
        //System.out.println(sb);
        JSONObject jsonObj = new JSONObject(sb.toString());
        return jsonObj;
    }

    //get destination information
    public static boolean getDestin(JSONObject res, address receriverAddress) throws Exception{
        if(res !=null){
            if(res.getJSONArray("officials").length() != 0){
                JSONArray arr = res.getJSONArray("officials");
                JSONObject obj = (JSONObject) arr.get(0);
                //read name
                receriverAddress.setName((String) obj.get("name"));
                JSONArray objArr = obj.getJSONArray("address");
                JSONObject addObj = (JSONObject) objArr.get(0);
                // read address
                receriverAddress.setZipCode((String) addObj.get("zip"));
                receriverAddress.setCity((String) addObj.get("city"));
                receriverAddress.setState((String) addObj.get("state"));
                receriverAddress.setAddress1((String) addObj.get("line1"));
                if( addObj.has("line2")) receriverAddress.setAddress2((String) addObj.get("line2"));
                return true;
            }
            else{
                System.out.println("no officials, exit");
                return false;
            }
        }
        return false;
    }

    //send letter using API
    public static Letter sendLetter(address senderAddress, address receriverAddress) throws Exception{

        
        /*
             this part is removed to the config.txt
         
         */
        
        Map<String, String>  variables = new HashMap<String,String>();
        variables.put("content",senderAddress.getMessage());
        //when line2 is null
        String senderLine2 = (senderAddress.getAddressLine2() == null)? "" :senderAddress.getAddressLine2();
        String receiverLine2 = (receriverAddress.getAddressLine2() == null)? "" : receriverAddress.getAddressLine2();
        LobResponse<Letter> response = new Letter.RequestBuilder()
                    .setDescription("Demo Letter")
                    .setFile("tmpl_25485502333c7e2")
                    .setColor(true)
                    .setMergeVariables(variables)
                    .setTo(
                            new Address.RequestBuilder()
                                    .setName(receriverAddress.getName())
                                    .setLine1(receriverAddress.getAddressLine1())
                                    .setLine2(receiverLine2)
                                    .setCity(receriverAddress.getCity())
                                    .setState(receriverAddress.getState())
                                    .setZip(receriverAddress.getZipCode())
                                    .setCountry("US")
                    )
                    .setFrom(
                            new Address.RequestBuilder()
                                    .setName(senderAddress.getName())
                                    .setLine1(senderAddress.getAddressLine1())
                                    .setLine2(senderLine2)
                                    .setCity(senderAddress.getCity())
                                    .setState(senderAddress.getState())
                                    .setZip(senderAddress.getZipCode())
                                    .setCountry("US")
                    )
                    .create();

            Letter letter = response.getResponseBody();
            return letter;
    }

    //output PDF file
    public static void downloadPDF(Letter letter) throws Exception{
        /* letter.getUrl(); */
        //String str = "https://s3-us-west-2.amazonaws.com/assets.lob.com/ltr_73854539a1f0204b.pdf?AWSAccessKeyId=AKIAIILJUBJGGIBQDPQQ&Expires=1510473226&Signature=dHiIXNmiPP%2Fcr0DlULhGryPdDC8%3D";
       // HttpURLConnection connection = (HttpURLConnection) new URL(letter.getUrl()).openConnection();

        URL url = new URL(letter.getUrl());
        HttpURLConnection connection =(HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
        connection.setRequestProperty("Accept","*/*");
        System.out.println(connection.getResponseCode());
        //keep connecting the url until response code is 200
        while(connection.getResponseCode() != 200){
            System.out.println(connection.getResponseCode());
            url = new URL(letter.getUrl());
            connection =(HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept","*/*");
        }
        System.out.println(connection.getResponseCode());
        final FileOutputStream output = new FileOutputStream(new File("sample.pdf"), false);
        final byte[] buffer = new byte[2048];
        int read;
        final InputStream input = connection.getInputStream();
        while((read = input.read(buffer)) > -1)
            output.write(buffer, 0, read);
        output.flush();
        output.close();
        input.close();
    }
}

