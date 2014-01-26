package com.fzh.game.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import com.fzh.game.poker.R;

/**
 * 
 * @author zero
 * 
 * {@hide}
 */
public class GlowDelegate {
	private static final String TAG = "GlowDelegate";
	private View mView;
	private boolean mPressed = false;
	
	Drawable mGlowBG;
    int mGlowWidth, mGlowHeight;
    float mGlowAlpha = 0f, mGlowScale = 1f, mDrawingAlpha = 1f;
    RectF mRect = new RectF(0f,0f,0f,0f);
    AnimatorSet mPressedAnim;
    final float GLOW_MAX_SCALE_FACTOR = 1.0f;
    final float BUTTON_QUIESCENT_ALPHA = 0.70f;
    final float GLOW_MIN_SCALE_FACTOR = 0.72f;
    
    
	public GlowDelegate(Context context,View delegateView) {
		mView = delegateView;
		
		mGlowBG = context.getResources().getDrawable(R.drawable.mz_ic_actionbar_highlight);
        if (mGlowBG != null) {
            setDrawingAlpha(BUTTON_QUIESCENT_ALPHA);
            mGlowWidth = mGlowBG.getIntrinsicWidth();
            mGlowHeight = mGlowBG.getIntrinsicHeight();
        }
	}
	
	public void onDraw(Canvas canvas) {
        if (mGlowBG != null) {
            canvas.save();
            final int w = mView.getWidth();
            final int h = mView.getHeight();
            /*final float aspect = (float)mGlowWidth / mGlowHeight;
            final int drawW = (int)(h*aspect);
            final int drawH = h;*/

            final int drawW = mGlowWidth;
            final int drawH = mGlowHeight;
            final int marginWidth = (drawW-w)/2;
            final int marginHeight = (drawH-h)/2;
            
            /*
             * 将canvas进行缩放，形成缩小动画
             * 缩小范围为1f~GLOW_MIN_SCALE_FACTOR
             */
            canvas.scale(mGlowScale, mGlowScale, w*0.5f, h*0.5f);
            /*
             * Glow的位置以mView中心放置.
             */
            mGlowBG.setBounds(-marginWidth, -marginHeight, drawW-marginWidth, drawH-marginHeight);
            mGlowBG.setAlpha((int)(mDrawingAlpha * mGlowAlpha * 255));
            mGlowBG.draw(canvas);
            canvas.restore();
            mRect.right = w;
            mRect.bottom = h;
        }
    }
	
	public float getDrawingAlpha() {
        if (mGlowBG == null) return 0;
        return mDrawingAlpha;
    }

    public void setDrawingAlpha(float x) {
        if (mGlowBG == null) return;
        // Calling setAlpha(int), which is an ImageView-specific
        // method that's different from setAlpha(float). This sets
        // the alpha on this ImageView's drawable directly
        //setAlpha((int) (x * 255));
        mDrawingAlpha = x;
    }

    public float getGlowAlpha() {
        if (mGlowBG == null) return 0;
        return mGlowAlpha;
    }

    public void setGlowAlpha(float x) {
        if (mGlowBG == null) return;
        mGlowAlpha = x;
        invalidate();
    }
    
    private void invalidate() {
    	mView.invalidate();
    }

    public float getGlowScale() {
        if (mGlowBG == null) return 0;
        return mGlowScale;
    }

    public void setGlowScale(float x) {
        if (mGlowBG == null) return;
        mGlowScale = x;
        final float w = mView.getWidth();
        final float h = mView.getHeight();
        /*if (GLOW_MAX_SCALE_FACTOR <= 1.0f) {
            // this only works if we know the glow will never leave our bounds
            invalidate();
        } else {
            final float rx = (w * (GLOW_MAX_SCALE_FACTOR - 1.0f)) / 2.0f + 1.0f;
            final float ry = (h * (GLOW_MAX_SCALE_FACTOR - 1.0f)) / 2.0f + 1.0f;
            invalidateGlobalRegion(
                    mView,
                    new RectF(mView.getLeft() - rx,
                              mView.getTop() - ry,
                              mView.getRight() + rx,
                              mView.getBottom() + ry));

            // also invalidate our immediate parent to help avoid situations where nearby glows
            // interfere
            if(mView.getParent() != null) {
            	((View)mView.getParent()).invalidate();
            }
        }*/
        
        final float rx = (mGlowWidth * mGlowScale - w) / 2.0f + 1.0f;
        final float ry = (mGlowHeight * mGlowScale - h) / 2.0f + 1.0f;
        invalidateGlobalRegion(
                mView,
                new RectF(mView.getLeft() - rx,
                          mView.getTop() - ry,
                          mView.getRight() + rx,
                          mView.getBottom() + ry));

        // also invalidate our immediate parent to help avoid situations where nearby glows
        // interfere
        if(mView.getParent() != null) {
        	((View)mView.getParent()).invalidate();
        }
    }

    public void setPressed(boolean pressed) {
        if (mGlowBG != null) {
            if (pressed != isPressed()) {
                if (mPressedAnim != null && mPressedAnim.isRunning()) {
                    mPressedAnim.cancel();
                }
                final AnimatorSet as = mPressedAnim = new AnimatorSet();
                if (pressed) {
                    if (mGlowScale < GLOW_MAX_SCALE_FACTOR) 
                        mGlowScale = GLOW_MAX_SCALE_FACTOR;
                    if (mGlowAlpha < BUTTON_QUIESCENT_ALPHA)
                        mGlowAlpha = BUTTON_QUIESCENT_ALPHA;
                    /* 按下时，不需要动画，直接设置为高亮状态 */
                    setDrawingAlpha(1f);
                    setGlowScale(1f);
                    setGlowAlpha(1f);
                    /*as.playTogether(
                        ObjectAnimator.ofFloat(this, "glowAlpha", 1f),
                        ObjectAnimator.ofFloat(this, "glowScale", 1f)
                    );
                    as.setDuration(50);*/
                } else {
                	/* 取消按下状态，
                	 * 大小 100% -> GLOW_MIN_SCALE_FACTOR(72%) 
                	 * alpha 100% -> 0
                	 * */
                	setDrawingAlpha(1f);
                    as.playTogether(
                        ObjectAnimator.ofFloat(this, "glowAlpha", 0f),
                        ObjectAnimator.ofFloat(this, "glowScale", GLOW_MIN_SCALE_FACTOR)
                    );
                    /* 25 frame */
                    as.setDuration(25*1000/60);
                }
                as.start();
            }
        }
        
        mPressed = pressed;
        invalidate();
    }
    
    public void invalidateGlobalRegion(View view, RectF childBounds) {
    	while (view.getParent() != null && view.getParent() instanceof View) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(childBounds);
            view.invalidate((int) Math.floor(childBounds.left),
                            (int) Math.floor(childBounds.top),
                            (int) Math.ceil(childBounds.right),
                            (int) Math.ceil(childBounds.bottom));
        }
    }
    
    public boolean isPressed() {
    	return mPressed;
    }
    
    public void jumpToCurrentState()
    {
    	if (mPressedAnim != null && mPressedAnim.isRunning()) {
            mPressedAnim.cancel();
            setGlowAlpha(0f);
            setGlowScale(GLOW_MIN_SCALE_FACTOR);
        }
    }
}
