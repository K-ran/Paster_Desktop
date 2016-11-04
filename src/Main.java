import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {
	static boolean start= false;
	static Client client=null;

    public static void main(String[] args) {
    	JFrame frame = new JFrame("Paster");
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	JPanel panel = new JPanel();
    	JLabel label = new  JLabel("Your IP");
    	final JButton button = new JButton("Start");
    	panel.setSize(200,200);
    	GridLayout layout = new GridLayout(2,1);
        layout.setHgap(10);              
        layout.setVgap(10);
        panel.setLayout(layout);  
        panel.add(label);
        panel.add(button);
        button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(start){
					button.setText("Start");
					start=false;
					stopClient();
				}
				else{
					button.setText("Stop");
					start=true;
					startClient();
				}
				
			}
		});
        frame.add(panel);
        frame.setSize(200, 100);
        frame.setVisible(true);
    	label.setText(getIp());
    }
    
   public static void startClient(){			   
	   try {
			client = new Client();
			client.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    public static void stopClient(){
    	client.stopRunning();
    }
    
    public static String getIp(){
	   	String ip="";
		try {
			Enumeration enu;
			enu = NetworkInterface.getNetworkInterfaces();
			 while(enu.hasMoreElements())
			   {
			       NetworkInterface n = (NetworkInterface) enu.nextElement();
			       Enumeration ee = n.getInetAddresses();
			       while (ee.hasMoreElements())
			       {
			           InetAddress i = (InetAddress) ee.nextElement();
			           String str = i.getHostAddress();
			           if(!str.contains("."))
			        	   continue;
			           String[] strArray = str.split("\\.");
			           if(strArray[0].equals("10") || 
			              (strArray[0].equals("192") && strArray[1].equals("168"))||
			              (strArray[0].equals("172") && (Integer.parseInt(strArray[1])>=16 && Integer.parseInt(strArray[1])<=31))
			             ){
			        	   ip=str;
			           }
			       }
			   }
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!ip.equals(""))
			ip= "  Your ip is: " + ip;
		else ip = "  You are not connected";
		return ip;
    }
}