package com.fzh.game.tool;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;
import com.fzh.game.poker.R;

public class ToastUtils {
    
    /**
     * 显示提示语
     * @param anchorView    The view to use as an anchor.
     * @param text          The text to show.  Can be formatted text.
     */
    public static Toast showToast(View anchorView, CharSequence text) {
        return showToast(anchorView, text, Toast.LENGTH_SHORT);
    }
    
    /**
     * 显示提示语
     * @param anchorView    The view to use as an anchor.
     * @param text          The text to show.  Can be formatted text.
     * @param duration      How long to display the message. Either {@link #LENGTH_SHORT} or {@link #LENGTH_LONG}
     */
    public static Toast showToast(View anchorView, CharSequence text, int duration) {
        final Context context = anchorView.getContext();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        final int midScreenHeight = screenHeight / 2;
        final Rect displayFrame = new Rect();
        anchorView.getWindowVisibleDisplayFrame(displayFrame);
        
        final int[] screenPos = new int[2];
        anchorView.getLocationOnScreen(screenPos);
        final int width = anchorView.getWidth();
        final int height = anchorView.getHeight();
        final int bottom = screenPos[1] + height;
        
        Toast cheatSheet = Toast.makeText(context, text, duration);
        View toastView = cheatSheet.getView();
        //viewRoolImpl中measureHierarchy方法内限制了toast窗口最大宽度config_prefDialogWidth
        int maxWidth = context.getResources().getDimensionPixelSize(R.dimen.config_prefDialogWidth);
        toastView.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), MeasureSpec.UNSPECIFIED);
        final int toastWidth = toastView.getMeasuredWidth();
        //toast窗口在smartbar之上
        final int smartbarHeight = context.getResources().getDimensionPixelSize(R.dimen.mz_action_button_min_height);
        
        int yOffset = 0;
        if (context.isDeviceDefaultTheme()) {
            yOffset = 30;
        }
        
        if (bottom < midScreenHeight) {
            // Show along the top; follow action buttons
            yOffset += screenPos[1] + height ;
            cheatSheet.setGravity(Gravity.TOP | Gravity.LEFT,
                    screenPos[0] + width / 2 - toastWidth / 2, yOffset);
        } else {
            // Show along the bottom; follow action buttons
            yOffset += screenHeight - screenPos[1] - smartbarHeight;
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.LEFT,
                    screenPos[0] + width / 2 - toastWidth / 2, yOffset);
        }
        
        cheatSheet.show();
        return cheatSheet;
    }
    
}
