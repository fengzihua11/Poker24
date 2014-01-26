package com.fzh.game.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.fzh.game.poker.R;

public class GameTopFrm extends RelativeLayout {
    
    private View mGetScore;
    private View mMoreSetting;

    public GameTopFrm(Context context) {
        super(context);
        init();
    }

    public GameTopFrm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameTopFrm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    protected void init() {
        inflate(mContext, R.layout.top_frm, this);
        
        mGetScore = findViewById(R.id.getScore);
        mMoreSetting = findViewById(R.id.moreSetting);
        
        long timeCurrent = System.currentTimeMillis();
        // 2014-1-29为1390924800000
        if(timeCurrent <= 1390924800000l) {
            mGetScore.setVisibility(View.GONE);
        } else {
            mGetScore.setVisibility(View.VISIBLE);
        }
        
    }
    
    public void setOnClickListener(OnClickListener l) {
        mGetScore.setOnClickListener(l);
        mMoreSetting.setOnClickListener(l);
    }
    
    public View getAnchorView() {
        return mMoreSetting;
    }
}