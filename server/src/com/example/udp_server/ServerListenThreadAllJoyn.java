package com.example.udp_server;


import java.net.DatagramSocket;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;

import android.content.Context;
import android.os.Handler;

 class ServerListenThreadAllJoyn extends Thread {
		private DatagramSocket sock = null;
		private boolean ListenRunning = false;
		private String buffstr = null;
		String SERVICE_NAME ;short CONTACT_PORT;
		SimpleService mSimpleService;
		BusAttachment mBus;
		Context context;
		Handler mhandle;
		public ServerListenThreadAllJoyn(Context context,Handler mhandle)
		{
			this.context=context;
			this.mhandle=mhandle;
		}
		public String getString() {
			return buffstr;
		}
		public void kill() {
			 mBus.unregisterBusObject(mSimpleService);
	            
             mBus.disconnect();
			 mBus.release();
			 mBus=null;
		}
		
		public boolean init(String name,short port) {
			SERVICE_NAME=name;
			CONTACT_PORT=port;
			mSimpleService=new SimpleService(mhandle);
			return true;
		}
		public void run() 
		{
			 org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(context);
            
             mBus = new BusAttachment(context.getPackageName(), BusAttachment.RemoteMessage.Receive);
         
             mBus.registerBusListener(new BusListener());

             
             Status status = mBus.registerBusObject(mSimpleService, "/SimpleService");
     
             if (status != Status.OK) {
                 
                 return;
             }

             status = mBus.connect();
      
             if (status != Status.OK) {
                 
                 return;
             }
   
             Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

             SessionOpts sessionOpts = new SessionOpts();
             sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
             sessionOpts.isMultipoint = false;
             sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;

             
             sessionOpts.transports = SessionOpts.TRANSPORT_ANY + SessionOpts.TRANSPORT_WFD;

             status = mBus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {
                 @Override
                 public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                     if (sessionPort == CONTACT_PORT) {
                         return true;
                     } else {
                         return false;
                     }
                 }
             });
           
             if (status != Status.OK) {
               
                 return;
             }

             /*
              * request a well-known name from the bus
              */
             int flag = BusAttachment.ALLJOYN_REQUESTNAME_FLAG_REPLACE_EXISTING | BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE;

             status = mBus.requestName(SERVICE_NAME, flag);
    
             if (status == Status.OK) {
                 /*
                  * If we successfully obtain a well-known name from the bus
                  * advertise the same well-known name
                  */
                 status = mBus.advertiseName(SERVICE_NAME, sessionOpts.transports);
     
                 if (status != Status.OK) {
                     /*
                      * If we are unable to advertise the name, release
                      * the well-known name from the local bus.
                      */
                     status = mBus.releaseName(SERVICE_NAME);
       
                  
                     return;
                 }
             }

            
         }
	}