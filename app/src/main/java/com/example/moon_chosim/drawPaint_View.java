package com.example.moon_chosim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.params.Face;
import android.view.View;

public class drawPaint_View extends View {

    Face[] faces;

    Paint paint = new Paint();
    float W, H;

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
        if (faces == null) return;

        System.out.println("hhhh wwwww : " + canvas.getHeight() + ", " + canvas.getWidth());
        for (Face face : faces) {
            RectF rect = new RectF(face.getBounds());
            canvas.drawRect(1440-(W*rect.bottom), H*rect.left, 1440-(W*rect.top), H*rect.right, paint);
            //canvas.drawRect((float) (0.36 * rect.left), (float) (0.56 * rect.top), (float) (0.36 * rect.right), (float) (0.56 * rect.bottom), paint);
            //System.out.println("left, top, right, bottom : " + (float) (0.36 * rect.left)+", "+ (float) (0.56 * rect.top)+", "+ (float) (0.36 * rect.right)+", "+ (float) (0.56 * rect.bottom));
            //System.out.println("left, top, right, bottom : " + W * rect.left + ", " + rect.top + ", " + H * rect.right + ", " + rect.bottom);
            System.out.println("left, top, right, bottom : " + (W*rect.bottom) + ", " +H*rect.left + ", " + (W*rect.top) + ", " + H*rect.right);
        }
    }

    public void set_face(Face[] faces) {
        this.faces = faces;
        invalidate();
    }

    public void set_size(Rect size) {
        System.out.println("llllllllllllllll tttttttttttttttttttttttttttt : " + size.left +", "+size.top);
        // llllllllllllllll tttttttttttttttttttttttttttt : 0, 0
        System.out.println("rrrrrrrrrrrrrrrr bbbbbbbbbbbbbbbbbbbbbbbbbbbb : " + size.right +", "+size.bottom);
        // rrrrrrrrrrrrrrrr bbbbbbbbbbbbbbbbbbbbbbbbbbbb : 4032, 3024

        W = (float) (1440.0/size.bottom);
        H = (float) (1680.0/size.right);
        System.out.println("tttttttttttttttttttttttt : " + W +", "+ H);
    }
}
