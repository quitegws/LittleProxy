package com.gws;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.littleshoot.proxy.HttpFiltersAdapter;

import com.google.common.primitives.Bytes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AttributeKey;

public class MyHttpFilters extends HttpFiltersAdapter{

	private String requestedUri;
	private HttpRequest originalRequest;
	private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");
	
	public MyHttpFilters(HttpRequest originalRequest, String uri){
		super(originalRequest);
		this.requestedUri = uri;
	}
	public MyHttpFilters(HttpRequest originalRequest, ChannelHandlerContext ctx) {
		super(originalRequest, ctx);
		requestedUri = ctx.channel().attr(CONNECTED_URL).get();
	}
	
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	
    	if(httpObject instanceof HttpRequest){
    		HttpRequest httpRequest = (HttpRequest)httpObject;
    		
    		String httpRequestUri = httpRequest.getUri();
    		String method = httpRequest.getMethod().name();
    		System.out.print("clientToProxyRequest "+method + " ： " +httpRequest.getProtocolVersion() +" "+ requestedUri +" "+ httpRequestUri+"  ");
//    		System.out.println("content string ---->" + contentStr);;
    		List<Entry<String, String>> headers = httpRequest.headers().entries();
    		for(Entry<String, String> entry : headers){
    			String key = entry.getKey();
    			String value = entry.getValue();
    			System.out.print(key + " : " + value + " ; ");
    		}
    		System.out.println();
    	 }
        return null;
    }
    
    @Override
  public HttpResponse proxyToServerRequest(HttpObject httpObject) {
    	if(httpObject instanceof HttpRequest){
    		HttpRequest httpRequest = (HttpRequest)httpObject;
    		System.out.print("proxyToServerRequest uri is : " + httpRequest.getUri() +" ");
    		List<Entry<String, String>> headers = httpRequest.headers().entries();
    		for(Entry<String, String> entry : headers){
    			String key = entry.getKey();
    			String value = entry.getValue();
    			System.out.print(key + " : " + value + " ; ");
    		}
    		System.out.println();
    	}
       return null;
  }
    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
    	if(httpObject instanceof HttpResponse){
    		HttpResponse httpResponse = (HttpResponse) httpObject;
    		System.out.print("serverToProxyResponse " + httpResponse.getStatus().code() + " : "+ httpResponse.getStatus().reasonPhrase()+" ");
    		List<Entry<String, String>> headers = httpResponse.headers().entries();
    		for(Entry<String, String> entry : headers){
    			String key = entry.getKey();
    			String value = entry.getValue();
    			System.out.print(key + " : " + value + " ; ");
    		}
    		System.out.println();
    	}
        return httpObject;
    }
    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if(httpObject instanceof HttpResponse){
        	HttpResponse httpResponse = (HttpResponse) httpObject;
        	String reasonPhrase = httpResponse.getStatus().reasonPhrase();
        	int code = httpResponse.getStatus().code();
        	System.out.print("proxyToClientResponse " + code + " : " + reasonPhrase + " ");
    		List<Entry<String, String>> headers = httpResponse.headers().entries();
    		for(Entry<String, String> entry : headers){
    			String key = entry.getKey();
    			String value = entry.getValue();
    			System.out.print(key + " : " + value + " ; ");
    		}
    		System.out.println();
    		System.out.println("----------------------------------------------------------");
    	      System.out.println(cloneAndExtractContent(httpObject, StandardCharsets.UTF_8));
    	      System.out.println("--------------------------------------------------------");
        }
    	return httpObject;
        
    }
    
    String cloneAndExtractContent(HttpObject httpObject, Charset charset){
        List<Byte> bytes = new ArrayList<Byte>();
        HttpContent httpContent = (HttpContent) httpObject;
        ByteBuf buf = httpContent.content();
        byte[] buffer = new byte[buf.readableBytes()];
        if(buf.readableBytes() > 0) {
            int readerIndex = buf.readerIndex();
            buf.getBytes(readerIndex, buffer);
        }
        for(byte b : buffer){
            bytes.add(b);
        }
        return new String(Bytes.toArray(bytes), charset);
    }

}