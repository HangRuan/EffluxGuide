package com.example.udp_server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.apache.http.conn.util.InetAddressUtils;





import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	 static {
	        System.loadLibrary("alljoyn_java");
	    }
	Handler mhandle;
	
	
	SessionThreadEfflux mSessionThreadEfflux=null;
	ServerListenThreadAllJoyn mServerListenThreadAllJoyn=null;
	Button listen;

   
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
				
			    if(type.equals("SimpleService"))
			    	mSessionThreadEfflux.add(content, 2);
				
			    if(type.equals("SessionThreadEfflux")){
			    	
			    }
			    String temp=((TextView)findViewById(R.id.receiveText)).getText().toString();
		    	temp+="\n";
		    	temp+=content;
		    	((TextView)findViewById(R.id.receiveText)).setText(temp);
			}
		};
		mSessionThreadEfflux=new SessionThreadEfflux(mhandle,new deal());
		mSessionThreadEfflux.start();
	}
	public void ListenBtn(View v) {
	
		listen.setText("ing");
		if(mServerListenThreadAllJoyn!=null){		
			mServerListenThreadAllJoyn.kill();
			
		}
		
		mServerListenThreadAllJoyn=new ServerListenThreadAllJoyn(getApplicationContext(),mhandle);
		
		
		mServerListenThreadAllJoyn.init("org.alljoyn.bus.samples.simple",Short.parseShort("42"));
		mServerListenThreadAllJoyn.start();
		
		
	}

	public void SendBtn(View v) {
		
		
		
		String temp=((EditText)findViewById(R.id.content)) .getText() .toString();
        mSessionThreadEfflux.send(temp.getBytes());
	}



}
				
			
		
	   
    