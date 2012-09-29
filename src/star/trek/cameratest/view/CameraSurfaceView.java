package star.trek.cameratest.view;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import star.trek.cameratest.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder holder;
    private Camera camera = null;
    private ImageView ivCam = null;
    BufferedOutputStream bufferedOutputStream = null;
    boolean stream=false;
    PreviewCallback callback = null;

    public CameraSurfaceView(Context context) 
    {
        super(context);

        this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setImageView(ImageView iv) {
		ivCam = iv;
	}

	@Override
    public void surfaceCreated(SurfaceHolder holder) 
    {
		this.camera = Camera.open();
		this.camera.getParameters().getSupportedPreviewSizes();
		try {
			this.camera.setPreviewDisplay(this.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.camera.setPreviewCallback(callback);
    }
	
	public void setPreviewCallback(PreviewCallback callback){
		this.callback = callback;
        if(camera!=null){
    		this.camera.setPreviewCallback(callback);
        }
	}
	
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
    {
        this.camera.startPreview();
    }    

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) 
    {
		this.camera.setPreviewCallback(null);
        this.camera.stopPreview();
        this.camera.release();
        this.camera = null;
    }

    public Camera getCamera()
    {
        return this.camera;
    }
    
    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {  
        final int frameSize = width * height;  
        for(int i=0; i<frameSize; i++){
        	int pixel = (0xff & ((int) yuv420sp[i]));  
            rgb[i] = 0xff000000 | ((pixel << 16) & 0xff0000) | ((pixel << 8) & 0xff00) | ((pixel) & 0xff); 
        }
      /*
        for (int j = 0, yp = 0; j < height; j++) {  
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
            for (int i = 0; i < width; i++, yp++) {  
                int y = (0xff & ((int) yuv420sp[yp])) - 16;  
                if (y < 0)  
                    y = 0;  
                if ((i & 1) == 0) {  
                    v = (0xff & yuv420sp[uvp++]) - 128;  
                    u = (0xff & yuv420sp[uvp++]) - 128;  
                }  
      
                int y1192 = 1192 * y;  
                int r = (y1192 + 1634 * v);  
                int g = (y1192 - 833 * v - 400 * u);  
                int b = (y1192 + 2066 * u);  
      
                if (r < 0)  
                    r = 0;  
                else if (r > 262143)  
                    r = 262143;  
                if (g < 0)  
                    g = 0;  
                else if (g > 262143)  
                    g = 262143;  
                if (b < 0)  
                    b = 0;  
                else if (b > 262143)  
                    b = 262143;  
      
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)  
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
            }  
        }  */
    }
    /*
    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,  
            int height) {  
        final int frameSize = width * height;  
      
        for (int j = 0, yp = 0; j < height; j++) {  
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
            for (int i = 0; i < width; i++, yp++) {  
                int y = (0xff & ((int) yuv420sp[yp])) - 16;  
                if (y < 0)  
                    y = 0;  
                if ((i & 1) == 0) {  
                    v = (0xff & yuv420sp[uvp++]) - 128;  
                    u = (0xff & yuv420sp[uvp++]) - 128;  
                }  
      
                int y1192 = 1192 * y;  
                int r = (y1192 + 1634 * v);  
                int g = (y1192 - 833 * v - 400 * u);  
                int b = (y1192 + 2066 * u);  
      
                if (r < 0)  
                    r = 0;  
                else if (r > 262143)  
                    r = 262143;  
                if (g < 0)  
                    g = 0;  
                else if (g > 262143)  
                    g = 262143;  
                if (b < 0)  
                    b = 0;  
                else if (b > 262143)  
                    b = 262143;  
      
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)  
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
            }  
        }  
    }  */

}