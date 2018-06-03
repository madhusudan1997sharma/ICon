package com.iconnect.icon.interfaces;


public interface ISocketOperator {
	
	public String sendHttpRequest(String params);
	public int startListening(int port);
	public void stopListening();
	public void exit();
	public int getListeningPort();
	public boolean sendMessage(String message, String ip, int port);

}