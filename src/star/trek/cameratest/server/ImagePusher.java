package star.trek.cameratest.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.util.Log;

public class ImagePusher implements Runnable{
	private static ImagePusher instance = null;
	private Socket socket = null;
	private BufferedOutputStream bufferedOutputStream = null;
	
	public ImagePusher() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket = new Socket("rc.lkl.kr", 1024);
					bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
					new Thread(ImagePusher.this).start();
				} catch (Exception e) {
					e.printStackTrace();
					close();
				}
			}
		}).start();
	}
	
	public static ImagePusher getInstance() {
		if(instance==null){
			instance = new ImagePusher();
		}
		return instance;
	}

	byte[] image;
	int imageNo=0;
	public void pushImage(byte[] data){
		image = data;
		imageNo++;
	}
	
	public boolean isWriting() {
		return streamWriting;
	}

	public long getPingTime() {
		return pingTime;
	}
	
	long pingTime = 1;
	boolean streamWriting = false;
	@Override
	public void run() {
		int no=0;
		try {
			while(instance!=null) {
				if(image==null||imageNo==no){
					Thread.sleep(10);
				}else{
					streamWriting = true;
					no = imageNo;
					final byte[] image = this.image;
					Log.i("push", "size="+image.length);
					long start = System.currentTimeMillis();
					bufferedOutputStream.write(intToByteArray(image.length));
					bufferedOutputStream.flush();
					bufferedOutputStream.write(image);
					bufferedOutputStream.flush();
					long end = System.currentTimeMillis();
					pingTime = end-start+1;
					streamWriting = false;
				}
			}
		} catch (Exception e) {
			close();
		}
	}
	
	public void close(){
		instance=null;
		try {
			socket.close();
		} catch (Exception e) {}
		socket = null;
		bufferedOutputStream = null;
	}
	
	private static byte[] intToByteArray(final int integer) {
	    ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
	    buff.putInt(integer);
	    buff.order(ByteOrder.BIG_ENDIAN);
	    return buff.array();
	}
}
