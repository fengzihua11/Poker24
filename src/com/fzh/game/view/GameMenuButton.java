package com.fzh.game.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.fzh.game.poker.R;
import com.fzh.game.tool.ToastUtils;

public class GameMenuButton extends LinearLayout implements View.OnLongClickListener {
    
    // 点击效果
    private GlowDelegate mDelegate;
    
    private ImageView mFlymeIcon;
    //private ProgressBar mProgressBar;
    
    private CharSequence mDescription;
    // 是否含有email帐号，如果含email帐号，则开关为enable状态

    public GameMenuButton(Context context) {
        this(context, null);
    }
    
    public GameMenuButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public GameMenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GameMenuButton, defStyle, 0);
        final int srcId = a.getResourceId(R.styleable.GameMenuButton_src, 0);
        mDescription = a.getText(R.styleable.GameMenuButton_text);
        a.recycle();
        
        
        inflate(mContext, R.layout.flyme_button_mz, this);
        //mProgressBar = (ProgressBar) findViewById(com.android.internal.R.id.progress_circular);
        mFlymeIcon = (ImageView) findViewById(R.id.flyme_icon);
        mFlymeIcon.setImageResource(srcId);
        
        // 最小宽度、高度和Action menu item 一致
        //setMinimumWidth(mContext.getResources().getDimensionPixelSize(com.android.internal.R.dimen.mz_action_button_min_width));
        //setMinimumHeight(mContext.getResources().getDimensionPixelSize(com.android.internal.R.dimen.mz_action_button_min_height));
        //setBackgroundResource(com.android.internal.R.drawable.menuitem_background_mz);
        setGravity(Gravity.CENTER);
        
        //setClickable(true);
        //setEnabled(true);
        setOnLongClickListener(this);
        
        initGlowDelegate(context);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mDescription == null) {
            return false;
        }
        ToastUtils.showToast(this, mDescription);
        return true;
    }
    
    /**
     * 长按Icon时, taost的提示语
     * @param onTextResId flyme打开时的toast提示语
     * @param offTextResId flyme关闭时的提示语
     */
    public void setDescriptionResId(int onTextResId) {
        mDescription = mContext.getResources().getString(onTextResId);
    }
    
    /**
     * 初始化长按效果对像
     * @param context
     */
    private void initGlowDelegate(Context context) {
        setBackground(null);
        mDelegate = new GlowDelegate(context, this);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDelegate.onDraw(canvas);
    }

    public float getDrawingAlpha() {
        return mDelegate.getGlowAlpha();
    }

    public void setDrawingAlpha(float x) {
        mDelegate.setDrawingAlpha(x);
    }

    public float getGlowAlpha() {
        return mDelegate.getGlowAlpha();
    }

    public void setGlowAlpha(float x) {
        mDelegate.setGlowAlpha(x);
    }

    public float getGlowScale() {
        return mDelegate.getGlowScale();
    }

    public void setGlowScale(float x) {
        mDelegate.setGlowScale(x);
    }

    public void setPressed(boolean pressed) {
        mDelegate.setPressed(pressed);
        super.setPressed(pressed);
    }
}