package com.fzh.game.view;

import static com.fzh.game.tool.RectTables.*;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import com.fzh.game.bean.CardBean;
import com.fzh.game.bean.CardBean.CardType;
import com.fzh.game.constant.Flagconstant;
import com.fzh.game.poker.R;
import com.fzh.game.picture.AnimateDrawable;
import com.fzh.game.picture.CardDrawable;
import com.fzh.game.picture.CiclerDrawable;
import com.fzh.game.tool.UtilTool;

public class Game24View extends View {

	public static final int EXIT_GAME = 0;
	public static final int GAME_OVER = 1;
	public static final int SHOW_ANSWER = 2;
	public static final int CLOSE_ANSWER = 3;
	
    public static final int PRE_GAME  = 4;
    public static final int NEXT_GAME = 5;
    public static final int RESET_GAME = 6;

	public static final int ISNULL_BEI = -30000;
	public static final int CANNOT_DIV = -30001;

	private int mWidth = 0;
	private int mHeight = 0;

	private Paint mPaint = null;

	private int paddingLeft = 0;
	private int paddingTop = 0;

	private volatile boolean isAnmation = false;

	private CardBean[] beans = new CardBean[13];
	private CardBean moveBean = null;
	private int srcIndex = -1;
	private int desIndex = -1;

	private CardDrawable blackDrawable;

	private volatile boolean isDrag = false;

	private int[] picRes = null;
	private int[] numbers = null;

	private ArrayList<Integer> pokes = new ArrayList<Integer>();

	private int beginGameTime = 0;

	private OnRectClickListener rectListener;

	private boolean touchable = true;

	private CiclerDrawable[] cicer = new CiclerDrawable[13];

	private PlayStatus playStatus = PlayStatus.WASH_MODE;

	public void setTouchable(boolean flag) {
		touchable = flag;
	}

	public void again() {
		flushAllRect();
		UtilTool.washPoke(pokes);
		calentGameCards(0);
		invalidate();
	}

	public int[] getPic() {
		int[] pics = new int[5];
		if (playStatus == PlayStatus.WASH_MODE) {
			for (int i = 0; i < picRes.length; i++)
				pics[i] = picRes[i];
			pics[4] = 0;
		} else {
			for (int i = 0; i < picRes.length; i++)
				pics[i] = numbers[i];
			pics[4] = 1;
		}
		return pics;
	}

	public int[] getFourNumber() {
		return numbers;
	}

	public void setPics(int nums[]) {
		playStatus = PlayStatus.QUESTION_MODE;
		flushAllRect();
		if (numbers == null)
			numbers = new int[4];
		for (int i = 0; i < 4; i++) {
			numbers[i] = nums[i];
			beans[i].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.answer, nums[i]), nums[i]);
		}
		invalidate();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 填充beans, 顶部四个数字牌
		for (int i = 0; i < 4; i++) {
			beans[i] = new CardBean(blackDrawable);
			beans[i].rect = new Rect(
			        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + 2 * UNIT_TOP_WIDTH * i + POKE_WIDTH * i, 
			        QUESTION_VIEW_TOP_BEGIN, 
			        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + 2 * UNIT_TOP_WIDTH * i + POKE_WIDTH * (i + 1),
			        QUESTION_VIEW_TOP_BEGIN + POKE_HEIGHT);
		}

		// 底部三个数字牌
		for (int i = 0; i < 3; i++) {
			beans[i + 4] = new CardBean(blackDrawable);
			beans[i + 4].rect = new Rect(
			        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * 2 * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * i + POKE_WIDTH * i, 
			        mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END - POKE_HEIGHT,
                    QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + i * 2 * UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH * i + POKE_WIDTH * (i + 1),
			        mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END);
		}

		// 画底部运算符号
		beans[7] = new CardBean(new CardDrawable(getContext(), R.drawable.add));
		beans[7].canMove = false;
		beans[7].isEmpty = false;
		beans[7].type = CardType.PLUG;
		beans[7].mValue = 0;
		beans[7].rect = new Rect(
		        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH,
		        mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END - ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) - SIGN_HEIGHT,
                QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH + UNIT_CACULATE_WHITE_WIDTH + SIGN_WIDTH,
                mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END - ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2));

		// 画等于号
		beans[8] = new CardBean(new CardDrawable(getContext(), R.drawable.equal));
		beans[8].canMove = false;
		beans[8].isEmpty = false;
		beans[8].type = CardType.EQUAL;
		beans[8].rect = new Rect(
		        QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH,
                mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END - ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2) - SIGN_HEIGHT,
                QUESTION_VIEW_LEFT_BEGIN + UNIT_TOP_WIDTH + POKE_WIDTH * 2 + UNIT_CACULATE_WHITE_WIDTH * 3 + SIGN_WIDTH * 2,
                mHeight - CIRCLE_RADIAUS * 2 * 2 - QUESTION_VIEW_BOTTOM_END - ((POKE_HEIGHT - SIGN_HEIGHT + 1) / 2));

		// 画四个可点击运算符号, 4个(9,10,11,12)
		for (int i = 0; i < 4; i++) {
			beans[i + 9] = new CardBean(new CardDrawable(getContext(),Flagconstant.signIds[i]));
			beans[i + 9].canMove = false;
			beans[i + 9].isEmpty = false;
			beans[i + 9].type = CardType.BUTTON;
			beans[i + 9].rect = new Rect(
	                QUESTION_VIEW_LEFT_BEGIN + (POKE_WIDTH + 1) / 2 + UNIT_CACULATE_SINGLE_WIDTH + UNIT_CACULATE_SINGLE_WIDTH * 2 * i +  SIGN_WIDTH * i,
	                mHeight - CIRCLE_RADIAUS * 2 * 2 - POKE_HEIGHT - UNIT_CACULATE_SINGLE_WIDTH - SIGN_WIDTH - QUESTION_VIEW_BOTTOM_END,
                    QUESTION_VIEW_LEFT_BEGIN + (POKE_WIDTH + 1) / 2 + UNIT_CACULATE_SINGLE_WIDTH + UNIT_CACULATE_SINGLE_WIDTH * 2 * i +  SIGN_WIDTH * (i + 1),
                    mHeight - CIRCLE_RADIAUS * 2 * 2 - POKE_HEIGHT - UNIT_CACULATE_SINGLE_WIDTH - QUESTION_VIEW_BOTTOM_END);
			beans[i + 9].mValue = i;
		}

		// 画局数计数器
		for (int i = 0; i < cicer.length; i++) {
			cicer[i] = new CiclerDrawable(Color.BLACK, CIRCLE_RADIAUS, i + 1,
			        QUESTION_VIEW_LEFT_BEGIN + UNIT_CIRCLE_WIDTH + 2 * UNIT_CIRCLE_WIDTH * i + 2 * CIRCLE_RADIAUS * i + CIRCLE_RADIAUS,
					mHeight - 2 * CIRCLE_RADIAUS - QUESTION_VIEW_BOTTOM_END);
		}
	}

	private void resetGameCards() {
		if(playStatus == PlayStatus.WASH_MODE) {
			if (picRes == null) {
				numbers = null;
				if (rectListener != null) {
					rectListener.onRectClick(GAME_OVER);
				}
				return;
			}
			for (int i = 0; i < 4; i++) {
				beans[i].setEmpty(false, new CardDrawable(getContext(),
						Flagconstant.picIds[picRes[i]]), picRes[i] / 4 + 1);
			}
		}
		else {
			if (numbers == null) {
				picRes = null;
				if (rectListener != null) {
					rectListener.onRectClick(GAME_OVER);
				}
				return;
			}
			for (int i = 0; i < 4; i++) {
				beans[i].setEmpty(false, new CardDrawable(getContext(),
						R.drawable.answer, numbers[i]), numbers[i]);
		}
		
		}
		
	}

	private void calentGameCards(int begin) {
		playStatus = PlayStatus.WASH_MODE;
		beginGameTime = begin;
		picRes = UtilTool.getResIdforCards(pokes, begin, 4);
		if (picRes == null) {
			numbers = null;
			if (rectListener != null) {
				rectListener.onRectClick(GAME_OVER);
			}
			return;
		}
		// ��� ��ͼƬ��ֵ
		numbers = new int[picRes.length];
		for (int i = 0; i < picRes.length; i++)
			numbers[i] = picRes[i] / 4 + 1;

		for (int i = 0; i < 4; i++) {
			beans[i].setEmpty(false, new CardDrawable(getContext(),
					Flagconstant.picIds[picRes[i]]), picRes[i] / 4 + 1);
		}
	}

	public Game24View(Context context) {
		this(context, null);
	}

	public Game24View(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		for (int i = 0; i < Flagconstant.picIds.length; i++) {
			pokes.add(i);
		}
		// 洗牌
		UtilTool.washPoke(pokes);
		blackDrawable = new CardDrawable(getContext(), R.drawable.black_card_0);
	}

	public void setOnRectClickListener(OnRectClickListener listener) {
		rectListener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = getWidth();
		mHeight = getHeight();
		if(rectListener != null) {
		    rectListener.onSizeChanged(mHeight);
		}
		initData();
		calentGameCards(0);
	}

	public void onDraw(Canvas canvas) {
		drawCicler(canvas);
	}

	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (isAnmation && mDrawable != null && an != null && an.hasEnded()) {
			isAnmation = false;
			endAnimation(AnimationStatus.MOVE_DES);
		}
		drawCards(canvas);
		drawMoveCard(canvas);
		drawAnimation(canvas);
	}

	public void drawAnimation(Canvas canvas) {
		if (isAnmation && mDrawable != null && an != null) {
			mDrawable.draw(canvas);
			invalidate();
		}
	}

	private void drawCards(Canvas canvas) {
		for (int i = 0; i < beans.length; i++) {
			if (beans[i].dr != null)
				beans[i].drawCard(canvas, mPaint);
		}
	}

	private void drawCicler(Canvas canvas) {
		for (int i = 0; i < cicer.length; i++) {
			// 画题目数计数圈
			if (i * 4 <= beginGameTime) {
				cicer[i].draw(canvas, Color.BLUE);
			} else {
				cicer[i].draw(canvas, Color.BLACK);
			}
		}
	}

	/**
	 * 绘制移动卡片
	 * @param canvas
	 * @see #draw(Canvas)
	 */
	private void drawMoveCard(Canvas canvas) {
		if (moveBean == null)
			return;
		if (moveBean.dr != null)
			moveBean.drawCard(canvas, mPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isAnmation || !touchable)
			return super.onTouchEvent(event);
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			hindDownRect(pointX, pointY);
			if (srcIndex > CardBean.NO_ID && srcIndex < 7) {
				startDrag(pointX, pointY);
			} else if (clickDownSignRect(pointX, pointY)) {
				calendResult();
				invalidate();
			}/* else if (clickDownFunctionRect(pointX, pointY)) {
				invalidate();
			}*/
			break;

		case MotionEvent.ACTION_MOVE:
			if (isDrag) {
				draging(pointX, pointY);
			}
			break;

		default:
			if (srcIndex > CardBean.NO_ID && srcIndex < beans.length) {
				endDrag(pointX, pointY);
			}
			invalidate();
			break;
		}
		return true;
	}

	private void flushAllRect() {
		// 还原顶部卡片状态
		for (int i = 0; i < 4; i++) {
			beans[i].mValue = CardBean.NO_ID;
			beans[i].canMove = true;
			beans[i].isEmpty = true;
			beans[i].dr = blackDrawable;
		}
		// 还原底部三张卡片状态
		for (int i = 0; i < 3; i++) {
			beans[i + 4].mValue = CardBean.NO_ID;
			beans[i + 4].canMove = true;
			beans[i + 4].isEmpty = true;
			beans[i + 4].dr = blackDrawable;
		}

		// 底部运算符号
		beans[7].dr = new CardDrawable(getContext(), R.drawable.add);
		beans[7].canMove = false;
		beans[7].type = CardType.PLUG;
		beans[7].isEmpty = false;
		beans[7].mValue = 0;
	}
	
	/**
     * 上一局，下一局，还原按钮处理事件
     * 
     * @param pointX
     * @param pointY
     * @return
     * 
     * @see #onTouchEvent(MotionEvent)
     */
    public final boolean clickDownFunctionRect(int event) {
        switch (event) {
            case PRE_GAME:
                if (beginGameTime - 4 >= 0) {
                    flushAllRect();
                    calentGameCards(beginGameTime - 4);
                }
                invalidate();
                break;

            case RESET_GAME:
                flushAllRect();
                resetGameCards();
                invalidate();
                break;
                
            case NEXT_GAME:
                flushAllRect();
                calentGameCards(beginGameTime + 4);
                invalidate();
                break;
                
            case EXIT_GAME:
                if (rectListener != null) {
                    invalidate();
                    rectListener.onRectClick(EXIT_GAME);
                }
                break;
        }
        return true;
    }

	/**
	 * 判断点击点
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	private boolean clickDownSignRect(int pointX, int pointY) {
		// 点击选择运算符号
		for (int i = 9; i < 13; i++) {
			if (beans[i].isHint(pointX, pointY) && !beans[i].isEmpty) {
				beans[7].setEmpty(false, beans[i].dr, beans[i].mValue);
				return true;
			}
		}

		// 点击底部运算符号，切换运算符号
		if (beans[7].isHint(pointX, pointY) && !beans[7].isEmpty) {
			int newValue = (beans[7].mValue + 1) % 4;
			beans[7].setEmpty(false, beans[9 + newValue].dr, newValue);
			return true;
		}
		return false;
	}

	/**
	 * 计算点击点所处的rect
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	private int hindDownRect(int pointX, int pointY) {
		for (int i = 0; i < 7; i++) {
			if (beans[i].isHint(pointX, pointY) && !beans[i].isEmpty
					&& beans[i].canMove) {
				srcIndex = i;
				desIndex = srcIndex;
				break;
			}
		}
		return srcIndex;
	}

	/**
	 * ��ʼ�϶�
	 * 
	 * @param pointX
	 * @param pointY
	 */
	private void startDrag(int pointX, int pointY) {
		isDrag = true;
		paddingLeft = pointX - beans[srcIndex].rect.left;
		paddingTop = pointY - beans[srcIndex].rect.top;
		calculateStartDragBean(pointX, pointY);
		// 请求重新绘制
		invalidate();
	}

	/**
	 * 计算开始拖动时的卡片大小
	 * 
	 * @param pointX
	 * @param pointY
	 */
	private void calculateStartDragBean(int pointX, int pointY) {
		if (moveBean == null)
			moveBean = new CardBean();
		int left = pointX - paddingLeft;
		int top = pointY - paddingTop;
		int right = left + POKE_WIDTH;
		int bottom = top + POKE_HEIGHT;
		if (moveBean.rect == null)
			moveBean.rect = new Rect(left, top, right, bottom);
		else
			moveBean.rect.set(left, top, right, bottom);
		if (moveBean.dr == null && srcIndex < beans.length
				&& srcIndex > CardBean.NO_ID) {
			moveBean.setEmpty(false, beans[srcIndex].dr, beans[srcIndex].mValue);
			beans[srcIndex].setEmpty(true, blackDrawable, CardBean.NO_ID);
		}
	}

	/**
	 * 拖动时计算位置并重绘
	 * 
	 * @param pointX
	 * @param pointY
	 * 
	 * @see #onTouchEvent(MotionEvent)
	 */
	private void draging(int pointX, int pointY) {
		// 计算移动的rect
	    calculateDragingMoveBean(pointX, pointY);
		// �����Ƿ�����Чͣ��λ��.
		calendDragingDesRect(pointX, pointY);
		invalidate();
	}

	/**
	 * 计算移动卡片的rect
	 * 
	 * @param pointX
	 * @param pointY
	 */
	private void calculateDragingMoveBean(int pointX, int pointY) {
		if (moveBean == null || moveBean.dr == null)
			return;
		int left = pointX - paddingLeft;
		int top = pointY - paddingTop;
		int right = left + POKE_WIDTH;
		int bottom = top + POKE_HEIGHT;
		if (moveBean.rect == null)
			moveBean.rect = new Rect(left, top, right, bottom);
		else
			moveBean.rect.set(left, top, right, bottom);
	}

	/**
	 * �����ƶ��ĵ������ڵ�λ��
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	private int calendDragingDesRect(int pointX, int pointY) {
		for (int i = 0; i < 6; i++) {
			if (beans[i].isHint(pointX, pointY) && beans[i].isEmpty
					&& beans[i].canMove) {
				desIndex = i;
				break;
			}
		}
		return desIndex;
	}

	/**
	 * 拖动放手时
	 * 
	 * @param pointX
	 * @param pointY
	 */
	private void endDrag(int pointX, int pointY) {
		startAnimation();
		if (moveBean != null) {
			moveBean.dr = null;
		}
	}

	private void calendResult() {
		if (srcIndex == 6 && desIndex != 6) {
			beans[4].setEmpty(true, blackDrawable, CardBean.NO_ID);
			beans[5].setEmpty(true, blackDrawable, CardBean.NO_ID);
		} else if (!beans[4].isEmpty && !beans[5].isEmpty) {
			int value = getresult(beans[4].mValue, beans[5].mValue);
			beans[6].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.answer, value), value);
			beans[6].canMove = value >= 0;
		} else {
			beans[6].setEmpty(false, blackDrawable, CardBean.NO_ID);
		}
	}

	private int getresult(int first, int end) {
		switch (beans[7].mValue) {
		case 0:
			return first + end;

		case 1:
			return first - end;

		case 2:
			return first * end;

		case 3:
			if (end == 0)
				return ISNULL_BEI;
			else if (first == 0)
				return 0;
			else if (first % end == 0)
				return first / end;
			else
				return CANNOT_DIV;
		}
		return CardBean.NO_ID;
	}

	public interface OnRectClickListener {
		void onRectClick(int flag);
		void onSizeChanged(int currentHeight);
	}

	// //////////
	private Animation an = null;
	private AnimateDrawable mDrawable;

	/**
	 * 拖动放手时，开始动画
	 * 
	 * @see #endDrag(int, int)
	 */
	private void startAnimation() {
		isAnmation = true;
		an = new TranslateAnimation(moveBean.rect.left,
				beans[desIndex].rect.left, moveBean.rect.top,
				beans[desIndex].rect.top);
		an.setDuration(300);
		an.setInterpolator(new DecelerateInterpolator(1.5f));
		an.setRepeatCount(0);
		an.initialize(10, 10, 10, 10);

		mDrawable = new AnimateDrawable(moveBean.dr, an);
		an.startNow();
	}

	private void endAnimation(AnimationStatus anStatus) {
		switch (anStatus) {
		case MOVE_DES:
			if (moveBean != null) {
				if (desIndex > CardBean.NO_ID && desIndex < beans.length) {
					beans[desIndex].setEmpty(false, ((CardDrawable) mDrawable
							.getProxy()), moveBean.mValue);
				}
				moveBean.clear();
			}
			calendResult();
			srcIndex = CardBean.NO_ID;
			desIndex = CardBean.NO_ID;
			isDrag = false;
			break;
		}
	}

	private enum AnimationStatus {
		MOVE_DES, FA_POKE, SHOU_POKE
	}

	private enum PlayStatus {
		WASH_MODE, QUESTION_MODE
	}
}