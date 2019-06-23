package com.example.imagetest.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.imagetest.R;

public class LikeImageView extends AppCompatImageView {
    private boolean mLiked;

    private static final int[] LIKED_STATE_SET = {
            R.attr.state_like
    };

    public LikeImageView(Context context) {
        super(context);
    }

    public LikeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isLiked() {
        return mLiked;
    }

    public void setLiked(boolean liked) {
        if (mLiked != liked) {
            this.mLiked = liked;
            refreshDrawableState();
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (isLiked()) {
            int[] res = super.onCreateDrawableState(extraSpace + 1);
            return mergeDrawableStates(res, LIKED_STATE_SET);
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }
}
