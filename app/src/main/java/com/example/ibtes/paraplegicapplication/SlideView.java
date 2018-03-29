package com.example.ibtes.paraplegicapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by User on 3/28/2018.
 */

public class SlideView extends ScrollView {

    private LinearLayout mlinearLayout;

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

        mlinearLayout = new LinearLayout(getContext());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        mlinearLayout.setPadding(0,0,0,100);
        mlinearLayout.setLayoutParams(params);

        mlinearLayout.setOrientation(LinearLayout.VERTICAL);

        addView(mlinearLayout);

    }

    public void createAndAdd(String name, String review){

        ReviewLayout rLayout = new ReviewLayout(getContext(), name.replace(".", ":  "), review);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        rLayout.setLayoutParams(params);

        mlinearLayout.addView(rLayout);

    }

    @Override
    public void removeAllViews() {

        mlinearLayout.removeAllViews();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
    }
}
