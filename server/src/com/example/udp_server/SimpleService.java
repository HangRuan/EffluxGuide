package com.example.udp_server;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.alljoyn.bus.BusObject;
import org.apache.http.conn.util.InetAddressUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
 /* The class that is our AllJoyn service.  It implements the SimpleInterface. */
    class SimpleService implements SimpleInterface, BusObject {
        private Handler mhandle;
        public SimpleService(Handler mhandle)
        {
        	this.mhandle=mhandle;
        }
        public String Ping(String inStr) {      	
			String[] temp=inStr.split("Request IP");
			if(temp!=null)
			{
				Bundle b = new Bundle();
				Message msg = mhandle.obtainMessage();		
				b.putString("type", "SimpleService");
				b.putString("data", temp[0]);			
				msg.setData(b);
				mhandle.sendMessage(msg);
			}
            return getLocalHostIp();
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

        
    }