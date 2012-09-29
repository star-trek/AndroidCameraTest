package star.trek.cameratest;

import java.io.ByteArrayOutputStream;

import star.trek.cameratest.server.ImagePusher;
import star.trek.cameratest.view.CameraSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements PreviewCallback{
	ImageView imageView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layout);
        CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(this);
        cameraSurfaceView.setImageView((ImageView)findViewById(R.id.imageView1));
        cameraSurfaceView.setPreviewCallback(this);
        linearLayout.addView(cameraSurfaceView);
        
        imageView = (ImageView)findViewById(R.id.imageView1);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
    int imageQuality=30;
    
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Camera.Parameters params = camera.getParameters();
		int w = params.getPreviewSize().width;
		int h = params.getPreviewSize().height;
		int format = params.getPreviewFormat();
		
		//if(!ImagePusher.getInstance().isWriting()){
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
			YuvImage image = new YuvImage(data, format, w, h, null);
	        Rect area = new Rect(0, 0, w, h);

	        long start = System.currentTimeMillis();
			image.compressToJpeg(area, imageQuality, out);
	        long end = System.currentTimeMillis();
	        
	        long pingTime = ImagePusher.getInstance().getPingTime()+(end-start);
	        if(pingTime>50 && imageQuality>25){
	        	imageQuality--;
	        }else if(pingTime<50 && imageQuality<100){
	        	imageQuality++;
	        }
	        
			Log.i("image", ""+imageQuality);

	        //Bitmap bm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
	        //imageView.setImageBitmap(bm);
			
	        ImagePusher.getInstance().pushImage(out.toByteArray());
		//}
		
/*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rect area = new Rect(0, 0, w, h);
        image.compressToJpeg(area, 50, out);*/
		/*
        int[] rgba = new int[w*h];
        decodeYUV420SP(rgba, data, w, h);

        Bitmap bm = Bitmap.createBitmap(rgba, w, h, Bitmap.Config.ARGB_8888);
        if(ivCam!=null) ivCam.setImageBitmap(bm);*/
	}
}
