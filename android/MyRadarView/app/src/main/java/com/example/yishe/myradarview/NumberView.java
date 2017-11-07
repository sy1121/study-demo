package com.example.yishe.myradarview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**d
 *
 * Created by yishe on 2017/9/11.
 */

public class NumberView extends View{
    private static final String TAG="NumberView";
    private Bitmap backNum;
    private int ratio;
    private boolean stop=false;
    private int target=-1;
    public NumberView(Context context){
        this(context,null);
    }
    public NumberView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public NumberView(Context context,AttributeSet attrs,int defAttrStyle){
        super(context,attrs,defAttrStyle);
        init();
    }

    private void init(){
        backNum = Bitmap.createBitmap(100,2000, Bitmap.Config.ARGB_8888);
        Canvas backCanvas=new Canvas(backNum);
        //backCanvas.drawColor(Color.BLUE);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(150);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        Rect rect =new Rect();

        paint.getTextBounds("1",0,1,rect);
        Log.i(TAG,"height ="+rect.height());
        int x=(backNum.getWidth()-rect.width())/2,y=0;
        for(int i=0;i<10;i++) {
            backCanvas.drawText(i + "", x, y+200, paint);
            y+=200;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.GREEN);
        int x=(getMeasuredWidth()-backNum.getWidth())/2;
        canvas.drawBitmap(backNum,x,-ratio,null);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        int layid=canvas.saveLayer(0,0,width,height,null,Canvas.ALL_SAVE_FLAG);
        Xfermode xfermode=new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xfermode);
        Rect rect=new Rect(0,0,width,height);
        canvas.drawRect(rect,paint);
        paint.setXfermode(null);
        canvas.restoreToCount(layid);

        ratio+=25;
        if(target>9) return;
        if(ratio==(100+200*target)&&stop){

        }else
        invalidate();
        if(ratio>=2000){
            ratio=0;
            stop=true;
        }
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

}
