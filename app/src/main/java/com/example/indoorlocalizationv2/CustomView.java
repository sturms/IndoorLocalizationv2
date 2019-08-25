package com.example.indoorlocalizationv2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.Nullable;

public class CustomView extends View {

    private final float scale = 120f;
    private final float maxSize = 0.200f;
    private Paint beaconPaint;
    private Paint shelfPaint;
    private Paint textPaint;
    private float beaconRadius = 10;
    private float beaconFrontViewCX;
    private float beaconFrontViewCY;
    private float beaconTopViewCX;
    private float beaconTopViewCY;
    private float shelfWidth;
    private float shelfHeightFrontView;
    private float shelfDepthTopView;

    public CustomView(Context context) {
        super(context);
        this.init(null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        this.beaconPaint = new Paint();
        this.beaconPaint.setColor(Color.GREEN);
        this.shelfPaint = new Paint();
        this.shelfPaint.setColor(Color.RED);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(30f);
    }

    public void changeBeaconFrontViewCoordinates(float cx, float cy, float radius) {
        this.beaconFrontViewCX = cx;
        this.beaconFrontViewCY = cy;
        this.beaconRadius = radius;

        // Convert to meters
        this.beaconFrontViewCX = this.beaconFrontViewCX * this.scale;
        this.beaconFrontViewCY = this.beaconFrontViewCY * this.scale;

        postInvalidate();
    }

    public void changeBeaconTopViewCoordinates(float cx, float cy, float radius) {
        this.beaconTopViewCX = cx;
        this.beaconTopViewCY = cy;
        this.beaconRadius = radius;

        // Convert to meters
        this.beaconTopViewCX = this.beaconTopViewCX * this.scale;
        this.beaconTopViewCY = this.beaconTopViewCY * this.scale;

        postInvalidate();
    }

    public void setTheShelfSizeFrontView(float width, float height) {
        this.shelfWidth = width; // < this.maxSize ? width : this.maxSize;
        this.shelfHeightFrontView = height; // < this.maxSize ? height : this.maxSize;

        // Convert to meters
        this.shelfWidth = this.shelfWidth * this.scale;
        this.shelfHeightFrontView = this.shelfHeightFrontView * this.scale;
    }

    public void setTheShelfDepthTopView(float depth) {
        this.shelfDepthTopView = depth; // < this.maxSize ? depth : this.maxSize;

        // Convert to meters
        this.shelfDepthTopView = this.shelfDepthTopView * this.scale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float paddingTop = 150;
        float paddingLeft = 30;

        // Shows captions
        canvas.drawText("Front view", paddingLeft + 20f, paddingTop - 20f, this.textPaint);
        canvas.drawText("Top view", (paddingLeft * 2) + this.shelfWidth + 40f, paddingTop - 20f, this.textPaint);

        // Draw shelf front view
        canvas.drawRect(paddingLeft, paddingTop, paddingLeft + this.shelfWidth, paddingTop + this.shelfHeightFrontView, this.shelfPaint);

        // Draw shelf top view
        canvas.drawRect((paddingLeft * 2) + this.shelfWidth, paddingTop, (paddingLeft * 2) + (this.shelfWidth * 2), paddingTop + this.shelfDepthTopView, this.shelfPaint);

        // Draw beacon
        // This is needed because in canvas coordinates starts from top left (0;0)
        // Also, in case if coordinates goes out of borders, set limit
        float newY = this.shelfHeightFrontView - this.beaconFrontViewCY + paddingTop;
        float newY2 = this.shelfDepthTopView - this.beaconTopViewCY + paddingTop;
        canvas.drawCircle(paddingLeft + this.beaconFrontViewCX, newY, this.beaconRadius, this.beaconPaint);
        canvas.drawCircle((paddingLeft * 2) + this.shelfWidth + this.beaconTopViewCX, newY2, this.beaconRadius, this.beaconPaint);
    }
}
