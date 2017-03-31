package com.gws;

import java.net.InetSocketAddress;
 
import org.littleshoot.proxy.HttpProxyServer;  
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;  
  
public class LittleProxy {  
  
    public static void main(String[] args)throws Exception {  


        HttpProxyServer server =  
                DefaultHttpProxyServer.bootstrap()
                	.withAddress(new InetSocketAddress("192.168.0.190", 9090)).withManInTheMiddle(new CertificateSniffingMitmManager()) 
                    .withFiltersSource(new MyHttpFiltersSourceAdapter())  
                    .start();  
    }  
} 