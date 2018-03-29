package com.example.ibtes.paraplegicapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by User on 3/29/2018.
 */

public class ReviewLayout extends LinearLayout {


    private TextView mName =  new TextView(getContext());
    private TextView mReview =  new TextView(getContext());
   // private TextView name;

    public ReviewLayout(Context context, String name, String review) {
        super(context);

        mName.setText(name);
        mReview.setText(review);

        init(null);
    }

    public ReviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public ReviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set){


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        mName.setTextSize(18);

        mName.setLayoutParams(params);
        mReview.setLayoutParams(params);

        addView(mName);
        addView(mReview);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.LTGRAY);

    }
}
