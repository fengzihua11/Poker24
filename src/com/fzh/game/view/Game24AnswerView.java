package com.fzh.game.view;

import static com.fzh.game.tool.RectTables.*;
import com.fzh.game.bean.CardBean;
import com.fzh.game.bean.CardBean.CardType;
import com.fzh.game.constant.Flagconstant;
import com.fzh.game.poker.R;
import com.fzh.game.picture.CardDrawable;
import com.fzh.game.tool.NNumCalculateToM;
import com.fzh.game.tool.RectTables;
import com.fzh.game.tool.UtilTool;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class Game24AnswerView extends View {

	private CardBean[] beans = new CardBean[15];
	private NNumCalculateToM tool;

	private int mWidth = 0;
	private int mHeight = 0;

	private Paint mPaint = null;

	private String[] answerStr = null;
	private Paint tPaint = null;

	private CardDrawable blackDrawable;

	private String tip = "";

	public Game24AnswerView(Context context) {
		this(context, null);
	}

	public Game24AnswerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		tip = UtilTool.getString(context, R.string.counting_tip_cal);
		initPaint();
		initData();
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec), RectTables.answerViewHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = getWidth();
		mHeight = getHeight();
		//initData();
	}

	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawCards(canvas);
		drawTip(canvas);
	}

	private void drawCards(Canvas canvas) {
		for (int i = 0; i < beans.length; i++) {
			if (beans[i].dr != null)
				beans[i].drawCard(canvas, mPaint);
		}
	}

	private void initPaint() {
		if (mPaint == null)
			mPaint = new Paint();
		mPaint.setAntiAlias(true);
		if (tPaint == null)
			tPaint = new Paint();
		tPaint.setTextSize(32);
		tPaint.setColor(Color.RED);
		tPaint.setAntiAlias(true);
		tPaint.setShadowLayer(2, 3.0f, 2.0f, Color.GRAY);
		tPaint.setTextAlign(Align.CENTER);
	}

	private void drawTip(Canvas canvas) {
		if (tip == null || tip.equals(""))
			return;
		canvas.drawText(tip, mWidth / 2, (mHeight + 30) / 2, tPaint);
	}

	private class CountRunnalbe implements Runnable {
		int[] mIds = new int[5];

		CountRunnalbe(int[] ids) {
			for (int i = 0; i < mIds.length && i < ids.length; i++) {
				mIds[i] = ids[i];
			}
		}

		public void run() {
			if (tool == null)
				tool = new NNumCalculateToM();
			int[] numbers = new int[4];
			if (mIds[4] == 0)
				for (int i = 0; i < numbers.length; i++)
					numbers[i] = mIds[i] / 4 + 1;
			else
				for (int i = 0; i < numbers.length; i++)
					numbers[i] = mIds[i];
			answerStr = tool.getAnswerString(numbers);
			freshView(mIds);
			postInvalidate();
		}
	};

	public void freshView(int ids[]) {
		if (answerStr == null || answerStr.length < 3) {
			tip = UtilTool.getString(getContext(), R.string.counting_tip_no);
			return;
		}
		String[] everyValue = UtilTool.getEveryStr(answerStr);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				switch (j) {
				case 1:
					getFlagpic(everyValue[1 + i * 5], 1 + i * 5);
					break;

				case 3:
					getFlagpic(everyValue[3 + i * 5], 3 + i * 5);
					break;

				case 4:
					getMakeNewpic(everyValue[4 + i * 5], 4 + i * 5);
					break;

				default:
					getPicFromFourCard(everyValue[j + i * 5], j + i * 5, ids);
					break;
				}
			}
		}
		tip = "";
	}

	public void getFlagpic(String flag, int index) {
		if (flag == null)
			return;
		boolean isEmpty = false;
		int value = -1;
		int resId = -1;
		if (flag.equals("+")) {
			value = 0;
			resId = R.drawable.add;
		} else if (flag.equals("#")) {
			value = 1;
			resId = R.drawable.sub;
		} else if (flag.equals("*")) {
			value = 2;
			resId = R.drawable.multiply;
		} else if (flag.equals("/")) {
			value = 3;
			resId = R.drawable.division;
		} else if (flag.equals("=")) {
			isEmpty = true;
			resId = R.drawable.equal;
		}
		beans[index].setEmpty(isEmpty, new CardDrawable(getContext(), resId),
				value);
	}

	public void getMakeNewpic(String flag, int index) {
		int value = UtilTool.getIntByStr(flag, 0);
		beans[index].setEmpty(false, new CardDrawable(getContext(),
				R.drawable.answer, value), value);
	}

	public void getPicFromFourCard(String flag, int index, int[] ids) {
		int value = UtilTool.getIntByStr(flag, 0);
		if (value > 13)
			beans[index].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.answer, value), value);
		else {
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] > -1 && value == ids[i] / 4 + 1) {
					beans[index].setEmpty(false, new CardDrawable(getContext(),
							Flagconstant.picIds[ids[i]]), value);
					ids[i] = -1;
					return;
				} else if (i == ids.length - 1) {
					beans[index].setEmpty(false, new CardDrawable(getContext(),
							R.drawable.answer, value), value);
				}
			}
		}
	}

	public void setPicIds(final int[] ids) {
		new Thread(new CountRunnalbe(ids)).start();
	}

	private void initData() {
	    if(blackDrawable == null)
	        blackDrawable = new CardDrawable(getContext(), R.drawable.black_card_0);
		// 第一行运算
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 1:
				beans[i] = new CardBean(blackDrawable);
				beans[i].type = CardType.PLUG;
				beans[i].canMove = false;
				beans[i].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH,
				        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2),
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + SIGN_HEIGHT);
				break;

			case 3:
				beans[i] = new CardBean(blackDrawable);
				beans[i].type = CardType.EQUAL;
				beans[i].canMove = false;
				beans[i].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2),
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH * 2,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + SIGN_HEIGHT);
				break;

			default:
				beans[i] = new CardBean(blackDrawable);
				beans[i].canMove = false;
				beans[i].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * (i / 2),
				        ANSOWER_VIEW_TOP_BEGIN,
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * ((i / 2) + 1),
						ANSOWER_VIEW_TOP_BEGIN + POKE_HEIGHT);
				break;
			}
		}

		// 第二行运算
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 1:
				beans[i + 5] = new CardBean(blackDrawable);
				beans[i + 5].type = CardType.PLUG;
				beans[i + 5].rect = new Rect(
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT + ANSOWER_POKER_DISTANCE,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT + SIGN_HEIGHT + ANSOWER_POKER_DISTANCE);
				break;
			case 3:
				beans[i + 5] = new CardBean(blackDrawable);
				beans[i + 5].type = CardType.EQUAL;
				beans[i + 5].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT + ANSOWER_POKER_DISTANCE,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH * 2,
                        ANSOWER_VIEW_TOP_BEGIN + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT + SIGN_HEIGHT + ANSOWER_POKER_DISTANCE);
				break;
			default:
				beans[i + 5] = new CardBean(blackDrawable);
				beans[i + 5].canMove = false;
				beans[i + 5].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * (i / 2),
				        ANSOWER_VIEW_TOP_BEGIN + POKE_HEIGHT + ANSOWER_POKER_DISTANCE,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * ((i / 2) + 1),
                        ANSOWER_VIEW_TOP_BEGIN + POKE_HEIGHT * 2 + ANSOWER_POKER_DISTANCE);
				break;
			}
		}

		// 第三行运算
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 1:
				beans[i + 10] = new CardBean(blackDrawable);
				beans[i + 10].type = CardType.PLUG;
				beans[i + 10].rect = new Rect(
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT * 2,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT * 2 + SIGN_HEIGHT);
				break;
			case 3:
				beans[i + 10] = new CardBean(blackDrawable);
				beans[i + 10].type = CardType.EQUAL;
				beans[i + 10].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH,
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT * 2,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH * 2,
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) + POKE_HEIGHT * 2 + SIGN_HEIGHT);
				break;
			default:
				beans[i + 10] = new CardBean(blackDrawable);
				beans[i + 10].canMove = false;
				beans[i + 10].rect = new Rect(
				        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * (i / 2),
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + POKE_HEIGHT * 2,
                        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * (i / 2) + POKE_WIDTH * ((i / 2) + 1),
                        ANSOWER_VIEW_TOP_BEGIN + ANSOWER_POKER_DISTANCE * 2 + POKE_HEIGHT * 3);
				break;
			}
		}
	}

	public void setVisibility(int visibility) {
		if (getVisibility() == visibility)
			return;
		TranslateAnimation tan = null;
		if (visibility == View.VISIBLE) {
			tip = UtilTool.getString(getContext(), R.string.counting_tip_cal);
			tan = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			tan.setDuration(250);
			tan.setInterpolator(new DecelerateInterpolator());
			setAnimation(tan);
		} else {
			tan = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f);
			tan.setDuration(250);
			tan.setInterpolator(new AccelerateInterpolator());
			tan.setAnimationListener(myListener);
			setAnimation(tan);
		}
		super.setVisibility(visibility);
	}

	public AnimationListener myListener = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			new Thread(new FlushRunnable()).start();
		}
	};

	public void flushView() {
		for (int i = 0; i < 15; i++) {
			beans[i].setEmpty(false, blackDrawable, CardBean.NO_ID);
		}
	}

	private class FlushRunnable implements Runnable {
		public void run() {
			flushView();
		}
	};
}