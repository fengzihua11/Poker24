package com.fzh.game.tool;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import com.fzh.game.poker.R;

public class RectTables {
    
    public static int QUESTION_VIEW_LEFT_BEGIN = 0;
    public static int QUESTION_VIEW_RIGHT_END = 0;
    public static int QUESTION_VIEW_TOP_BEGIN = 0;
    public static int QUESTION_VIEW_BOTTOM_END = 0;
    
    public int questionViewWidth = 0;
    public int questionViewHeight = 0;
    
    private DisplayMetrics mDisplay;
    
    private Rect questionViewpadding = new Rect();
    
    private Rect anserViewpadding = new Rect();
    
    // 卡片宽度和高度(2:3), 卡片最小宽度单位，一张卡片等于6个单位
    public static final int UNIT_TOP_COUNT = 1 + 1 + 3 * 2 + 4 * 3 * 2;
    public static int UNIT_TOP_WIDTH = 0;
    public static int POKE_WIDTH = 0;
    public static int POKE_HEIGHT = 0;
    
    // 记数点半径
    public static final int UNIT_CIRCLE_COUNT = 1 + 1 + 12 * 2 + 13 * 4;
    public static int UNIT_CIRCLE_WIDTH = 0;
    public static int CIRCLE_RADIAUS = 0;
    
    // 运算符号宽度和高度
    public static int SIGN_WIDTH = 0;
    public static int SIGN_HEIGHT = 0;
    
    // 底部三个答案牌和运算符之间的空格宽度
    public static final int UNIT_CACULATE_WHITE_COUNT = 2 * 2;
    public static int UNIT_CACULATE_WHITE_WIDTH = 0;

    // 可选运算符之间的空白宽度
    public static final int UNIT_CACULATE_SINGLE_COUNT = 8;
    public static int UNIT_CACULATE_SINGLE_WIDTH = 0;
    
    private int mTopHeight = 0;
    private int mBottomHeight = 0;
    
    private Resources mRes;

    public static int answerViewWidth = 0;
    public static int answerViewHeight = 730;
    
    public static int ANSOWER_VIEW_LEFT_BEGIN = 0;
    public static int ANSOWER_VIEW_RIGHT_END = 0;
    public static int ANSOWER_VIEW_TOP_BEGIN = 0;
    public static int ANSOWER_VIEW_BOTTOM_END = 0;
    
    public static int ANSOWER_POKER_DISTANCE = 0;
    
    public RectTables(Resources res) {
        mRes = res;
        
        mTopHeight = mRes.getDimensionPixelSize(R.dimen.top_height);
        mBottomHeight = mRes.getDimensionPixelSize(R.dimen.bottom_height);
        
        res.getDrawable(R.drawable.question_bg).getPadding(questionViewpadding);
        res.getDrawable(R.drawable.answer_bg).getPadding(anserViewpadding);
        
        mDisplay = mRes.getDisplayMetrics();
        initTable();
    }
    
    public void onSizeChanged(int currentWidth) {
        /*if(currentWidth == -1 || currentWidth != mDisplay.widthPixels) {
            mDisplay = mRes.getDisplayMetrics();
            initTable();
        }*/
    }
    
    public void initTable() {
        if(mDisplay == null)
            return;
        questionViewWidth = mDisplay.widthPixels - questionViewpadding.left - questionViewpadding.right;
        QUESTION_VIEW_LEFT_BEGIN = questionViewpadding.left;
        QUESTION_VIEW_RIGHT_END = questionViewpadding.right;
        questionViewHeight = mDisplay.heightPixels - mTopHeight - mBottomHeight -  questionViewpadding.top - questionViewpadding.bottom;;
        QUESTION_VIEW_TOP_BEGIN = questionViewpadding.top;
        QUESTION_VIEW_BOTTOM_END = questionViewpadding.bottom;
        
        // 扑克高度和宽度
        UNIT_TOP_WIDTH = (questionViewWidth +  (UNIT_TOP_COUNT/ 2))/ UNIT_TOP_COUNT;
        POKE_WIDTH = (questionViewWidth * 2 * 3) / UNIT_TOP_COUNT;;
        POKE_HEIGHT = (POKE_WIDTH * 3 + 1) / 2;
        
        // 记数器圆半径
        UNIT_CIRCLE_WIDTH = (questionViewWidth +  (UNIT_CIRCLE_COUNT/ 2))/ UNIT_CIRCLE_COUNT;
        CIRCLE_RADIAUS = (questionViewWidth * 2) / UNIT_CIRCLE_COUNT;
        
        // 运算符号宽度和高度
        SIGN_HEIGHT = SIGN_WIDTH = (POKE_WIDTH + 1) / 2;
        
        // 底部三个答案牌和运算符之间的空格宽度
        UNIT_CACULATE_WHITE_WIDTH = (questionViewWidth - UNIT_TOP_WIDTH * 2 - 3 * POKE_WIDTH - 2 * SIGN_WIDTH + (UNIT_CACULATE_WHITE_COUNT / 2)) / UNIT_CACULATE_WHITE_COUNT;
        
        // 可选运算符之间的空白宽度
        UNIT_CACULATE_SINGLE_WIDTH = (questionViewWidth - POKE_WIDTH - 4 * SIGN_WIDTH + (UNIT_CACULATE_SINGLE_COUNT / 2)) / UNIT_CACULATE_SINGLE_COUNT;
        
        
        
        // 答案视图相关参数
        answerViewWidth = mDisplay.widthPixels - anserViewpadding.left - anserViewpadding.right;
        answerViewHeight = Math.max(mDisplay.heightPixels - mTopHeight - mBottomHeight - (POKE_HEIGHT * 3 + 1) / 2, POKE_HEIGHT * 3 + UNIT_TOP_WIDTH * 2);
        ANSOWER_VIEW_LEFT_BEGIN = anserViewpadding.left;
        ANSOWER_VIEW_RIGHT_END = anserViewpadding.right;
        ANSOWER_VIEW_TOP_BEGIN = anserViewpadding.top;
        ANSOWER_VIEW_BOTTOM_END = anserViewpadding.bottom;
        
        ANSOWER_POKER_DISTANCE = (answerViewHeight - anserViewpadding.top - anserViewpadding.bottom - POKE_HEIGHT * 3) / 2;
        
        
        /*Log.d("fengzihua", mDisplay.heightPixels + ", " + mDisplay.widthPixels);
        Log.d("fengzihua", "questionViewWidth = " + questionViewWidth + ", questionViewHeight = " + questionViewHeight);
        Log.d("fengzihua", "questionViewpadding = " + questionViewpadding);
        Log.d("fengzihua", "anserViewpadding = " + anserViewpadding);
        Log.d("fengzihua", "POKE_WIDTH = " + POKE_WIDTH + ", POKE_HEIGHT = " + POKE_HEIGHT);
        Log.d("fengzihua", "UNIT_CIRCLE_WIDTH = " + UNIT_CIRCLE_WIDTH + ", CIRCLE_RADIAUS = " + CIRCLE_RADIAUS);
        Log.d("fengzihua", "SIGN_HEIGHT = " + SIGN_HEIGHT);
        Log.d("fengzihua", "UNIT_CACULATE_WHITE_WIDTH = " + UNIT_CACULATE_WHITE_WIDTH);
        Log.d("fengzihua", "UNIT_CACULATE_SINGLE_WIDTH = " + UNIT_CACULATE_SINGLE_WIDTH);
        Log.d("fengzihua", "answerViewHeight = " + answerViewHeight + ", answerViewWidth = " + answerViewWidth);
        Log.d("fengzihua", "ANSOWER_POKER_DISTANCE = " + ANSOWER_POKER_DISTANCE);*/
    }
}