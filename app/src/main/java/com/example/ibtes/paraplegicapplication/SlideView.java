package com.example.ibtes.paraplegicapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

/**
 * Created by User on 3/28/2018.
 */

public class SlideView extends ScrollView {


    public SlideView(Context context) {
        super(context);

        init(null);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }


    public void init(@Nullable AttributeSet set){

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
    }
}
