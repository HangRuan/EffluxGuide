package com.example.udp_client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import MyLib.*;
class ParticipantThreadEfflux extends Thread
   {   
      	 String RemoteHost;
      	 int Port;
      	 RtpParticipant mParticipant;
      	 MultiParticipantSession mMultiParticipantSession;
      	 Handler mhandle;
      	 public int LocalSsrc;
      	 DealingDataInterface mDealingObject;
         boolean SendFlag=false,RunningFlag=true;
         byte[] SendBuffer;
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
      	 public void Alert(String buff)
		 {
			    Bundle b = new Bundle();
				Message msg = mhandle.obtainMessage();			
				b.putString("type","ParticipantThreadEfflux");	
				b.putString("data", buff);	
				msg.setData(b);
				mhandle.sendMessage(msg);
		 }
      	 public synchronized void kill()
      	 {	
  			RunningFlag=false;    	   
      		if(mMultiParticipantSession!=null)mMultiParticipantSession.terminate();
      		mMultiParticipantSession=null;
      	 }
     	 public ParticipantThreadEfflux(String remoteHost,int port,int localSsrc,Handler mhandle,final DealingDataInterface mDealingObject)
     	 {   this.mDealingObject=mDealingObject;
     		 this.RemoteHost=remoteHost;
     		 this.Port=port;
     		 this.LocalSsrc=localSsrc;
     		 this.mhandle=mhandle;
     		 RtpParticipant LocalParticipant = RtpParticipant.createReceiver(new RtpParticipantInfo(LocalSsrc), getLocalHostIp(), 6000, 6001);
    		 RtpParticipant RemoteParticipant = RtpParticipant.createReceiver(new RtpParticipantInfo(1), RemoteHost, 7000, 7001);     		 
    		 mMultiParticipantSession= new MultiParticipantSession("session1"  , 8, LocalParticipant);    	
		 
    		 mMultiParticipantSession.init();
    		 mMultiParticipantSession.addReceiver(RemoteParticipant);
    		 mMultiParticipantSession.addDataListener(new RtpSessionDataListener() {
	            @Override
	            public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
	            	Alert("Client receive from server:"+packet.getSequenceNumber());
	            	mDealingObject.analyseData(packet.getDataAsArray());
	                
	            }
	        });
     	 }
     	 public void send(byte[] buff)
     	 {
     		synchronized(this){
     			SendFlag=true;
     		}
     		SendBuffer=buff;
     	 }
     	 
    	 public void run()
    	 {
    		while(RunningFlag)
    		{
    			 synchronized(this){
    				 if(SendFlag)
    				 {  SendFlag=false;
    				    if(mMultiParticipantSession.sendData(SendBuffer, 0x45, false)==false)Alert("Session send data fail");
    				 }
    				 else continue;
    			 }
    			 Thread.yield();
    			 
    			
    		}
    	 }
    	 
 }
		