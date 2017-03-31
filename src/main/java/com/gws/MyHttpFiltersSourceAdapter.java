package com.gws;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;

public class MyHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter{
	private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");
    
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
    	
    	String uri = originalRequest.getUri();
        if (originalRequest.getMethod() == HttpMethod.CONNECT) {
            if (ctx != null) {
                String prefix = "https://" + uri.replaceFirst(":443$", "");
                ctx.channel().attr(CONNECTED_URL).set(prefix);
            }
            return new MyHttpFilters(originalRequest, ctx);
        }
        String connectedUrl = ctx.channel().attr(CONNECTED_URL).get();
        if (connectedUrl == null) {
            return new MyHttpFilters(originalRequest,uri);
        }
        return new MyHttpFilters(originalRequest,connectedUrl + uri);
//       return new HttpFiltersAdapter(originalRequest) {   
    }
    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return 1024 * 1024 * 10;
    }

    @Override
    public int getMaximumResponseBufferSizeInBytes() {
        return 1024 * 1024 * 10;
    }
}
