package com.example.udp_client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.apache.http.conn.util.InetAddressUtils;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

class FindServerThreadAllJoyn extends Thread {
		 private BusAttachment mBus;
		 private ProxyBusObject mProxyObj;
		 private static final short CONTACT_PORT=42;
		 private static final String SERVICE_NAME="org.alljoyn.bus.samples.simple";
		 SessionOpts sessionOpts;
		 Mutable.IntegerValue sessionId;
		 boolean runflag=false;
		 String NAME;
		 SimpleInterface mSimpleService;
		 String HostIP;
		 Context context;
		 Handler mhandle;
		 public void kill()
		 {
			
	            
             mBus.disconnect();
			 mBus.release();
			 mBus=null;
		 }
		 public FindServerThreadAllJoyn(Context context,Handler mhandle)
		 {
			 this.context=context;
			 this.mhandle=mhandle;
			 mBus = new BusAttachment(getClass().getName(), BusAttachment.RemoteMessage.Receive);

		     Status status = mBus.connect();
		      if (Status.OK != status) { 
		    	 /* Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_SHORT).show();*/
		        return;
		      }
		      mBus.registerBusListener(new BusListener() {
		    	  @Override
		    	  public void foundAdvertisedName(String name,short transport, String namePrefix) {
		    		  
		       
		    		  short contactPort = CONTACT_PORT;
		    		  sessionOpts = new SessionOpts();
		    		  sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES; 
		    		  sessionOpts.isMultipoint = false; 
		    		  sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		    		  sessionOpts.transports = SessionOpts.TRANSPORT_ANY; 
		    		  sessionId = new Mutable.IntegerValue();
		    		  NAME=name;
		    		  synchronized(FindServerThreadAllJoyn.this){
		    		  runflag=true;
		    		  }
		    	  }
		      });

		      status = mBus.findAdvertisedName(SERVICE_NAME);
		      	if (status != Status.OK) { 
		      		
		      		return;
		      	}
		 }
		 public void Alert(String buff)
		 {
			    Bundle b = new Bundle();
				Message msg = mhandle.obtainMessage();			
				b.putString("type","FindServerThreadAllJoyn");	
				b.putString("data", buff);	
				msg.setData(b);
				mhandle.sendMessage(msg);
		 }
		  public String getLocalHostIp()
	        {
	            String ipaddress = "";
	            try
	            {
	                Enumeration<NetworkInterface> en = NetworkInterface
	                        .getNetworkInterfaces();
	                // 遍历所用的网络接口
	                while (en.hasMoreElements())
	                {
	                    NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
	                    Enumeration<InetAddress> inet = nif.getInetAddresses();
	                    // 遍历每一个接口绑定的所有ip
	                    while (inet.hasMoreElements())
	                    {
	                        InetAddress ip = inet.nextElement();
	                        if (!ip.isLoopbackAddress()
	                                && InetAddressUtils.isIPv4Address(ip
	                                        .getHostAddress()))
	                        {
	                            return ipaddress = ""+ ip.getHostAddress();
	                        }
	                    }

	                }
	            }
	            catch (SocketException e)
	            {
	                Log.e("feige", "获取本地ip地址失败");
	                e.printStackTrace();
	            }
	            return ipaddress;

	        }
		
	
		 public String GetServerIPadress(String buff)
		 {   String strPing=null;
			 try {
				   
				   strPing = mSimpleService.Ping(buff);
			
				   
				} catch (Exception e) {
			
				}
			 return strPing;

		 }
		 public void run()
		 {   boolean go=true;
			 while(go)
			 {
				  synchronized(FindServerThreadAllJoyn.this){
		    		  if(runflag==false)continue;
		    		  }
				
				
					 Status status = mBus.joinSession(NAME, 
							 CONTACT_PORT,
				    		   sessionId, 
				    		   sessionOpts,
				    		   new SessionListener());
					 
				      mProxyObj = mBus.getProxyBusObject(SERVICE_NAME, "/SimpleService",sessionId.value,new Class[] { SimpleInterface.class });
				      mSimpleService = mProxyObj.getInterface(SimpleInterface.class);
				      if(status==Status.OK)go=false;
				      
				      //server found and return the ip
				      HostIP=GetServerIPadress(getLocalHostIp()+"Request IP");
				      Alert(HostIP);
				      
			 }
		 }
	 }