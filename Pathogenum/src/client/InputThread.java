package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import utils.Conversions;

public class InputThread extends Thread {
    Socket sock;
    InputStream is;
    LinkedList<String> chatBuffer = new LinkedList<String>();
    boolean ok = true;
    public InputThread(Socket sock){
        this.sock = sock;
        try {
            is = sock.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        while(ok){
            byte[] command = new byte[4];
            try {
                is.read(command);
                int intCommand = Conversions.ByteArrayToInt(command);
                switch(intCommand){
                case ClientConnectionHandler.SENDMESSAGE:
                   
                    int check = is.read(command);       
                    if(checkInput(check))
                        return;
                    intCommand = Conversions.ByteArrayToInt(command);
                    byte[] input = new byte[intCommand];
                    check = is.read(input);   
                    if(checkInput(check))
                        return;
                    String text = new String(input);
                    chatBuffer.offer(text);
                    break;
                }
               
            } catch (IOException e) {
                //e.printStackTrace();
                return;
            }
        }
    }
   
    private boolean checkInput(int check) {
        if(check == -1){
            try {
            	if(sock.isClosed())
            		return true;
                sock.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public ArrayList<String> getChatMessages() {

        ArrayList<String> list = new ArrayList<String>();
        int size = chatBuffer.size();
        for(int i = 0; i < size; i++){
            list.add(chatBuffer.pop());
        }
        return list;
    }
   
}