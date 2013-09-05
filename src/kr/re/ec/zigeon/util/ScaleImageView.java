package kr.re.ec.zigeon.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
 
public class ScaleImageView extends ImageView implements OnTouchListener{
    // debugging info
    private static final String TAG = "ViewTouchImage";
    private static final boolean D = false;
 
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix savedMatrix2 = new Matrix();
 
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
 
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
 
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;
 
    private boolean isInit = false;
 
    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
 
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX); // MATRIX);
    }
 
    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public ScaleImageView(Context context) {
        this(context, null);
    }
 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (D) Log.i(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
        if (isInit == false){
            init();
            isInit = true;
        }
    }
 
    @Override
    public void setImageBitmap(Bitmap bm) {
        if (D) Log.i(TAG, "setImageBitmap");
        super.setImageBitmap(bm);
        isInit = false;
        init();
    }
 
    @Override
    public void setImageDrawable(Drawable drawable) {
        if (D) Log.i(TAG, "setImageDrawable");
        super.setImageDrawable(drawable);
        isInit = false;
        init();
    }
 
    @Override
    public void setImageResource(int resId) {
        if (D) Log.i(TAG, "setImageResource");
        super.setImageResource(resId);
        isInit = false;
        init();
    }
 
    protected void init() {
        matrixTurning(matrix, this);
        setImageMatrix(matrix);
        setImagePit();
    }
 
    /**
     * image fit
     */
    public void setImagePit(){
 
        // value of matrix
        float[] value = new float[9];
        this.matrix.getValues(value);
 
        // size of view
        int width = this.getWidth();
        int height = this.getHeight();
 
        // size of image
        Drawable d = this.getDrawable();
        if (d == null)  return;
       int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        int scaleWidth = (int) (imageWidth * value[0]);
        int scaleHeight = (int) (imageHeight * value[4]);
 
       // image cannot move outside
        value[2] = 0;
        value[5] = 0;
 
        if (imageWidth > width || imageHeight > height){
            int target = WIDTH;
            if (imageWidth < imageHeight) target = HEIGHT;
 
            if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
            if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;
 
            scaleWidth = (int) (imageWidth * value[0]);
            scaleHeight = (int) (imageHeight * value[4]);
 
            if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
            if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
        }
 
        // and locate at center
        scaleWidth = (int) (imageWidth * value[0]);
        scaleHeight = (int) (imageHeight * value[4]);
        if (scaleWidth < width){
            value[2] = (float) width / 2 - (float)scaleWidth / 2;
        }
        if (scaleHeight < height){
            value[5] = (float) height / 2 - (float)scaleHeight / 2;
        }
 
        matrix.setValues(value);
 
        setImageMatrix(matrix);
    }
 
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
 
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
           savedMatrix.set(matrix);
           start.set(event.getX(), event.getY());
           mode = DRAG;
           break;
        case MotionEvent.ACTION_POINTER_DOWN:
           oldDist = spacing(event);
           if (oldDist > 10f) {
              savedMatrix.set(matrix);
              midPoint(mid, event);
              mode = ZOOM;
           }
           break;
       case MotionEvent.ACTION_UP:
 
        case MotionEvent.ACTION_POINTER_UP:
           mode = NONE;
           break;
        case MotionEvent.ACTION_MOVE:
           if (mode == DRAG) {
              matrix.set(savedMatrix);
             matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
 
           }
           else if (mode == ZOOM) {
              float newDist = spacing(event);
              if (newDist > 10f) {
                 matrix.set(savedMatrix);
                 float scale = newDist / oldDist;
                 matrix.postScale(scale, scale, mid.x, mid.y);
              }
           }
           break;
        }
 
        // matrix value turning
        matrixTurning(matrix, view);
 
        view.setImageMatrix(matrix);
 
        return true;
    }
 
    private float spacing(MotionEvent event) {
       float x = event.getX(0) - event.getX(1);
       float y = event.getY(0) - event.getY(1);
       return FloatMath.sqrt(x * x + y * y);
    }
 
    private void midPoint(PointF point, MotionEvent event) {
       float x = event.getX(0) + event.getX(1);
       float y = event.getY(0) + event.getY(1);
       point.set(x / 2, y / 2);
    }
 
    private void matrixTurning(Matrix matrix, ImageView view){
        // value of matrix
        float[] value = new float[9];
        matrix.getValues(value);
        float[] savedValue = new float[9];
        savedMatrix2.getValues(savedValue);
 
        // size of view
        int width = view.getWidth();
        int height = view.getHeight();
 
        // size of image
        Drawable d = view.getDrawable();
        if (d == null)  return;
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        int scaleWidth = (int) (imageWidth * value[0]);
        int scaleHeight = (int) (imageHeight * value[4]);
 
        // image cannot move outside
        if (value[2] < width - scaleWidth)   value[2] = width - scaleWidth;
        if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
        if (value[2] > 0)   value[2] = 0;
        if (value[5] > 0)   value[5] = 0;
 
        // max scale is X10
        if (value[0] > 10 || value[4] > 10){
            value[0] = savedValue[0];
            value[4] = savedValue[4];
            value[2] = savedValue[2];
            value[5] = savedValue[5];
        }
 
        // cannot zoom out less than screen size
        if (imageWidth > width || imageHeight > height){
            if (scaleWidth < width && scaleHeight < height){
                int target = WIDTH;
                if (imageWidth < imageHeight) target = HEIGHT;
 
                if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
                if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;
 
                scaleWidth = (int) (imageWidth * value[0]);
                scaleHeight = (int) (imageHeight * value[4]);
 
                if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
                if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
            }
        }
 
        // if image is small originally, cannot zoom out less than origin size
        else{
            if (value[0] < 1)   value[0] = 1;
            if (value[4] < 1)   value[4] = 1;
        }
 
        // and locate center
        scaleWidth = (int) (imageWidth * value[0]);
        scaleHeight = (int) (imageHeight * value[4]);
        if (scaleWidth < width){
            value[2] = (float) width / 2 - (float)scaleWidth / 2;
        }
        if (scaleHeight < height){
            value[5] = (float) height / 2 - (float)scaleHeight / 2;
        }
 
        matrix.setValues(value);
        savedMatrix2.set(matrix);
    }
}
