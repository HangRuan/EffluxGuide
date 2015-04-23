package com.example.udp_client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;




import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 static {
	        System.loadLibrary("alljoyn_java");
	    }
	Handler mhandle;
	
	
	
	FindServerThreadAllJoyn mFindServerThread=null;
	
	Button listen; 	
	
	BusAttachment mBus;
	
	ParticipantThreadEfflux mParticipantThreadEfflux;
	String HostIP=null;
	int ssrc;
	int Port;
   
	public class deal implements DealingDataInterface
	{

		/* (non-Javadoc)
		 * @see com.example.udp_client.DealingDataInterface#analyseData(byte[])
		 */
		@Override
		public void analyseData(byte[] buffer) {
			// TODO Auto-generated method stub
			
			
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listen=(Button)findViewById(R.id.listenBtn);
		
	
	
		mhandle = new Handler() { // receive processing

			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String type= msg.getData().getString("type");	
				String content= msg.getData().getString("data");	
				
				String temp=((TextView)findViewById(R.id.receiveText)).getText().toString();				
				temp+="\n";
				temp+=type+": "+"\n";
				temp+=content;			
				((TextView)findViewById(R.id.receiveText)).setText(temp);
				
				if(type.equals("ParticipantThreadEfflux"))
				{
					
					Toast.makeText(getApplicationContext(), "Data received from server :"+ content, Toast.LENGTH_SHORT).show();
					
				}
				if(type.equals("FindServerThreadAllJoyn"))
				{
					HostIP=content;
					Toast.makeText(getApplicationContext(), "Found server ip", Toast.LENGTH_SHORT).show();
					mParticipantThreadEfflux=new ParticipantThreadEfflux(HostIP,42,2,mhandle,new deal());
					mParticipantThreadEfflux.start();
				}
			
			}
		};
		
	}
	public void ListenBtn(View v) {
	

		if(mFindServerThread!=null)mFindServerThread.kill();
		mFindServerThread=new FindServerThreadAllJoyn(getApplicationContext(),mhandle);
		mFindServerThread.start();
		
		

	}

	public void SendBtn(View v) {
		
	
		String temp=((EditText)findViewById(R.id.content)) .getText() .toString();
		mParticipantThreadEfflux.send(temp.getBytes());
		
		
	}


    
		 

		 
	  
}
				
			
		
	   
    