package com.example.moon_chosim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.params.Face;
import android.util.Log;
import android.view.View;

public class drawPaint_View extends View {

    Face[] faces;

    Paint paint = new Paint();
    float W, H;
    String TAG = "drawPaint_View";

    boolean c = true;

    public drawPaint_View(Context context) {
        super(context);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);
    }

    //mPreviousPosition == bounds[l=0.00 t=280.00 r=1440.00 b=1680.00]
    //2960×1440
    @Override
    protected void onDraw(Canvas canvas) {
        //System.out.println("hhhh wwwww : " + canvas.getHeight() + ", " + canvas.getWidth());
        //hhhh wwwww : 1400, 1440
        try {
            for (Face face : faces) {
                if (face.getScore() < 80) {
                    continue;
                }
                Rect rect = face.getBounds();
                canvas.drawRect((int) (1440 - (W * rect.bottom)), (int) (H * rect.left), (int) (1440 - (W * rect.top)), (int) (H * rect.right), paint);
                Log.i(TAG, "left, top, right, bottom : " + (int) (1440 - (W * rect.bottom)) + ", " + (int) (H * rect.left) + ", 1440-" + (int) (1440 - (W * rect.top)) + ", " + (int) (H * rect.right));
            }
        } catch (NullPointerException e) {
        }
    }

    public void set_face(Face[] faces) {
        this.faces = faces;
        invalidate();
    }

    public void set_size(Rect size) {
        System.out.println("llllllllllllllll tttttttttttttttttttttttttttt : " + size.left + ", " + size.top);
        // llllllllllllllll tttttttttttttttttttttttttttt : 0, 0
        System.out.println("rrrrrrrrrrrrrrrr bbbbbbbbbbbbbbbbbbbbbbbbbbbb : " + size.right + ", " + size.bottom);
        // rrrrrrrrrrrrrrrr bbbbbbbbbbbbbbbbbbbbbbbbbbbb : 4032, 3024

        W = (float) (1440.0 / size.bottom);
        H = (float) (1680.0 / size.right);
        //W = 1;
        //H = 1;
        System.out.println("tttttttttttttttttttttttt : " + W + ", " + H);
    }

    public void set_turn(boolean c) {
        this.c = c;
    }
}
