package com.fzh.game.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import com.fzh.game.poker.R;

public class GameMenuPopup extends PopupWindow {
    
    public static int POPUP_MAX_HEIGHT_PROT = 880;
    public static int POPUP_MAX_HEIGHT_LAND = 520;
    public static int POPUP_HOR_OFFSET_PROT = -210;
    public static int POPUP_HOR_OFFSET_LAND = -300;
    
    private View mAnchorView;
    
    private OnClickListener mListener;
    
    private View main;
    private View makeQuestion;
    private View testOther;
    private View description;
    
    private Context mContext;
    
    public GameMenuPopup(Context context, View anchorView, OnClickListener l) {
        super(context);
        mContext = context;
        setFocusable(true);
        main = LayoutInflater.from(context).inflate(R.layout.more_menu, null);
        setContentView(main);
        
        mAnchorView = anchorView;
        mListener = l;
        
        inflaterView();
    }
    
    private void inflaterView() {
        makeQuestion = main.findViewById(R.id.makeQuestion);
        makeQuestion.setOnClickListener(mListener);
        testOther = main.findViewById(R.id.testOther);
        testOther.setOnClickListener(mListener);
        description = main.findViewById(R.id.descriptionGame);
        description.setOnClickListener(mListener);
    }

    public void show() {
        setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.more_menu_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        showAsDropDown(mAnchorView, 0, mContext.getResources().getDimensionPixelSize(R.dimen.more_menu_offy));
    }
    
    public void dismiss() {
        super.dismiss();
    }
}