package com.fzh.game.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.fzh.game.poker.R;

public class GameBottomFrm extends LinearLayout {
    
    private View exitApp, preQuestion, resetQuestion, nextQuestion, answer;

    public GameBottomFrm(Context context) {
        super(context);
        init();
    }

    public GameBottomFrm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameBottomFrm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    protected void init() {
        inflate(mContext, R.layout.bottom_frm, this);
        
        exitApp =  findViewById(R.id.exitApp);
        preQuestion =  findViewById(R.id.preQuestion);
        resetQuestion = findViewById(R.id.resetQuestion);
        nextQuestion = findViewById(R.id.nextQuestion);
        answer = findViewById(R.id.answer);
    }
    
    public void setOnClickListener(OnClickListener l) {
        exitApp.setOnClickListener(l);
        preQuestion.setOnClickListener(l);
        resetQuestion.setOnClickListener(l);
        nextQuestion.setOnClickListener(l);
        answer.setOnClickListener(l);
    }
}