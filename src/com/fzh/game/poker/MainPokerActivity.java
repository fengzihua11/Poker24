
package com.fzh.game.poker;

import com.fzh.game.tool.UtilTool;
import com.fzh.game.view.Game24AnswerView;
import com.fzh.game.view.Game24View;
import com.fzh.game.view.GameBottomFrm;
import com.fzh.game.view.GameMenuPopup;
import com.fzh.game.view.GameTopFrm;
import com.fzh.game.view.Game24View.OnRectClickListener;



//import net.miidipush.SDK.SDKConnector;
import net.miidiwall.SDK.AdWall;
import net.miidiwall.SDK.IAdWallShowAppsNotifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainPokerActivity extends Activity implements OnRectClickListener, OnClickListener, IAdWallShowAppsNotifier {

    public static final String XMLNAME = "showdialog";
    public static final String AD_KEY = "showad";

    private static final String MIIDI_PUBLISHER_ID = "16764";//"6"
    private static final String MIIDI_APP_PASSWORD = "4f0wz1sq9vhi2cq1";//"6666666666666666"

    private Game24View gameView;
    private Game24AnswerView answerView;

    private DisplayMetrics mDisplay;

    private GameBottomFrm mBottomFrm;
    private GameTopFrm mTopFrm;
    
    // 多个收件人时弹出收件人列表
    GameMenuPopup mGameMenuPopup;
    
    public static boolean mIsMeizu = false;

    public void onCreate(Bundle savedInstanceState) {
        isMeizuProduct();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getDisplay();

        View main = LayoutInflater.from(this).inflate(R.layout.main, null);
        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(main);

        inflaterView();

        initDomobofferWallManager();

        /*
         * if (getInt(1) == 1) openAdTip();
         */
    }
    
    private final void isMeizuProduct() {
        if(Build.DEVICE.equals("mx3") || Build.DEVICE.equals("mx2")) {
            mIsMeizu = true;
        }
        mIsMeizu = false;
    }

    /**
     * 初始化
     */
    private final void initDomobofferWallManager() {
        // 设置应用的开发者帐号信息，appId和appPassword是在米迪广告平台网站上，注册后会提交应用后，网站提供的信息
        PokerActivity.set(this, MIIDI_PUBLISHER_ID, MIIDI_APP_PASSWORD);
        //PokerActivity.setIcon(R.drawable.icon);
        //SDKConnector.getInstance(this).setAppId(MIIDI_PUBLISHER_ID, MIIDI_APP_PASSWORD);
        // 设置push广告的显示icon
        //SDKConnector.getInstance(this).setIconId(R.drawable.answer_0);
        
        // 积分墙
        AdWall.init(this, MIIDI_PUBLISHER_ID, MIIDI_APP_PASSWORD);
    }

    private void getDisplay() {
        Resources res = getResources();
        mDisplay = res.getDisplayMetrics();
        Log.d("fengzihua", "--" + mDisplay.toString());
    }

    private void inflaterView() {

        mTopFrm = (GameTopFrm) findViewById(R.id.topFrm);
        mTopFrm.setOnClickListener(this);

        mBottomFrm = (GameBottomFrm) findViewById(R.id.bottomFrm);
        mBottomFrm.setOnClickListener(this);

        gameView = (Game24View) findViewById(R.id.gameView);
        gameView.setOnRectClickListener(this);
        answerView = (Game24AnswerView) findViewById(R.id.answerView);
        answerView.setOnRectClickListener(this);        

        mGameMenuPopup = new GameMenuPopup(this, mTopFrm.getAnchorView(), this);
    }

    public void pushInt(int value) {
        SharedPreferences.Editor editor = getSharedPreferences(XMLNAME,
                Context.MODE_WORLD_WRITEABLE).edit();
        editor.putInt(AD_KEY, value);
        editor.commit();
    }

    public void onRectClick(int flag) {
        switch (flag) {
            case Game24View.EXIT_GAME:
                showCloseAppDailog();
                break;

            case Game24View.GAME_OVER:
                showGameAgaimDailog();
                break;

            case Game24View.SHOW_ANSWER:
                answerView.setVisibility(View.VISIBLE);
                answerView.setPicIds(gameView.getPic());
                gameView.setTouchable(false);
                break;

            case Game24View.CLOSE_ANSWER:
                answerView.setVisibility(View.GONE);
                gameView.setTouchable(true);
                break;
        }
    }

    protected void openAdTip() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.ad_miss_icon).setMessage(
                R.string.ad_show_content).setPositiveButton(
                R.string.ad_noshow_miss_icon,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pushInt(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.ad_show_miss_icon, null).create();
        dialog.show();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (answerView.isShown()) {
                answerView.setVisibility(View.GONE);
                gameView.setTouchable(true);
                return true;
            } else {
                // 移到后台
                boolean ret = moveTaskToBack(false);
                if (!ret) {
                    showCloseAppDailog();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    protected void showCloseAppDailog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.login_out_title_tip)
                .setPositiveButton(R.string.login_out_sure,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.login_out_cancel, null)
                .create();
        dialog.show();
    }

    protected void showGameAgaimDailog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.game_over_title_tip)
                .setPositiveButton(R.string.game_over_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.game_over_again,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                gameView.again();
                            }
                        }).create();
        dialog.show();
    }
    
    protected void showMenuPopup() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.game_over_title_tip)
                .setPositiveButton(R.string.game_over_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.game_over_again,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                gameView.again();
                            }
                        }).create();
        dialog.show();
    }

    protected void openGameDescription() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.game_description_icon)
                .setMessage(R.string.game_description).setNegativeButton(
                        R.string.rule_sure, null).create();
        dialog.show();
    }

    private void getHelpBySms() {
        int ids[] = gameView.getFourNumber();
        if (ids == null) {
            Toast.makeText(this,
                    UtilTool.getString(this, R.string.counting_tip_washing),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String content = UtilTool.getString(this, R.string.msg_tip_title);
        for (int i = 0; i < ids.length; i++) {
            content += ids[i] + ", ";
        }
        content += UtilTool.getString(this, R.string.msg_tip_tip);
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        mIntent.putExtra("sms_body", content);
        startActivity(mIntent);
    }

    protected void makeQuestionDailog() {
        View group = ((LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.questions, null);
        final EditText number1 = (EditText) group.findViewById(R.id.number1);
        final EditText number2 = (EditText) group.findViewById(R.id.number2);
        final EditText number3 = (EditText) group.findViewById(R.id.number3);
        final EditText number4 = (EditText) group.findViewById(R.id.number4);
        final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.drawable.icon).setTitle(R.string.make_question_demo).setView(
                group).setNeutralButton(R.string.make_question_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendNumber(number1.getText().toString(), number2
                                .getText().toString(), number3.getText()
                                .toString(), number4.getText().toString());
                    }
                }).setNegativeButton(R.string.make_question_cancel, null)
                .create();
        dialog.show();
    }

    public void sendNumber(String num1, String num2, String num3, String num4) {
        int[] numbers = new int[4];
        numbers[0] = getNumber(num1);
        if (numbers[0] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[1] = getNumber(num2);
        if (numbers[1] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[2] = getNumber(num3);
        if (numbers[2] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[3] = getNumber(num4);
        if (numbers[3] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        gameView.setPics(numbers);
    }

    public int getNumber(String num) {
        int value = -1;
        try {
            value = Integer.parseInt(num);
        } catch (Exception e) {
        }
        return value;
    }

    private void showMsg(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (answerView.isShown()) {
            answerView.setVisibility(View.GONE);
            gameView.setTouchable(true);
            return;
        }

        switch (v.getId()) {
            case R.id.makeQuestion:
                makeQuestionDailog();
                mGameMenuPopup.dismiss();
                break;
            case R.id.testOther:
                getHelpBySms();
                mGameMenuPopup.dismiss();
                break;
            case R.id.descriptionGame:
                openGameDescription();
                mGameMenuPopup.dismiss();
                break;
            
            case R.id.exitApp:
                showCloseAppDailog();
                break;
            case R.id.preQuestion:
                gameView.clickDownFunctionRect(Game24View.PRE_GAME);
                break;
            case R.id.nextQuestion:
                gameView.clickDownFunctionRect(Game24View.NEXT_GAME);
                break;
            case R.id.resetQuestion:
                gameView.clickDownFunctionRect(Game24View.RESET_GAME);
                break;
            case R.id.answer:
                answerView.setVisibility(View.VISIBLE);
                answerView.setPicIds(gameView.getPic());
                gameView.setTouchable(false);
                break;
                
            case R.id.moreSetting:
                mGameMenuPopup.show();
                break;
                
            case R.id.getScore:
                // 打开积分墙
                AdWall.showAppOffers(MainPokerActivity.this);
                /*if(mIsMeizu) {
                    overridePendingTransition(R.anim.mz_activity_to_next_open_enter, R.anim.mz_activity_to_next_open_exit);
                }*/
                // 推送广告[API方式]
                //SDKConnector.getInstance(PokerActivity.this).PushByAPI();
                //PokerActivity.get();
                break;
        }
    }

    public void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainPokerActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }
    // ////////================积分墙打开回调====================//////////////

    @Override
    public void onShowApps() {
        //Toast.makeText(this, "开始显示积分墙的Activity", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismissApps() {
       //Toast.makeText(this, "关闭积分墙的Activity", Toast.LENGTH_SHORT).show();
    }
    
    //==================================
}