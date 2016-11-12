import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Prueba {
	public static void main (String args[]){
		int mod = 6%2;
		int div = 6/2;
		
		System.out.println(mod);
		System.out.println(div);
		Calendar c = Calendar.getInstance();
		if(c == null){
			c = Calendar.getInstance();
		}
		SimpleDateFormat sdout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String uid =  sdout.format(c.getTime());
		String[] params = { "currentUser", "d29d2936-072a-00be-91fc-575978bf928e" , "dateStart" , uid };
        
		 if(params != null && params.length > 1){
    		 int boucle = params.length / 2;
    		 try{
        		 for(int i = 0; i < params.length ; i++ ){
	        		 String key = params[i];
	            	 String id = params[++i];
	        		 //ControlConnection.addHeader(key, id, true);
	            	 System.out.println("Key "+key+" id "+id);
	            	 
        		 }
    		 }catch(java.lang.ArrayIndexOutOfBoundsException ie){
    			 ie.printStackTrace();
    		 }
		 }
	}
}