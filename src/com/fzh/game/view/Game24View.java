package com.fzh.game.view;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

	// 卡片宽度和高度(2:3)
	public static int POKE_WIDTH = 200;
	public static int POKE_HEIGHT = 300;
	
	private static final int SIGN_WIDTH = 80;
	private static final int SIGN_HEIGHT = 80;
	private static final int BTN_WIDTH = 150;
	private static final int BTN_HEIGHT = 150;
	private static final int CIRCLE_RADIAUS = 26;

	private int mWidth = 0;
	private int mHeight = 0;

	private Paint mPaint = null;

	private int paddingLeft = 0;
	private int paddingTop = 0;

	private volatile boolean isAnmation = false;

	private CardBean[] beans = new CardBean[18];
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

	//private Drawable backGround;

	private PlayStatus playStatus = PlayStatus.WASH_MODE;

	public void setTouchable(boolean flag) {
		touchable = flag;
	}

	public void again() {
		flushAllRect();
		// ϴ��
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

	/**
	 * �������õ�ֵ
	 * 
	 * @param numbers
	 */
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
		// 填充beans
		int fourLRpadding = (mWidth - POKE_WIDTH * 4) / 5;
		int calendLRpadding = (mWidth - POKE_WIDTH * 3 - SIGN_WIDTH * 2) / 6;
		int btnLRpadding = (mWidth - BTN_WIDTH * 4) / 5;
		int signLRpadding = (mWidth - SIGN_WIDTH * 4 - BTN_WIDTH - btnLRpadding * 2) / 5;
		int circleLRpadding = (mWidth - CIRCLE_RADIAUS * 2 * 13) / 14;

		// 顶部四个数字牌
		for (int i = 0; i < 4; i++) {
			beans[i] = new CardBean(blackDrawable);
			beans[i].rect = new Rect(fourLRpadding * (i + 1) + POKE_WIDTH * i,
					20, (fourLRpadding + POKE_WIDTH) * (i + 1),
					20 + POKE_HEIGHT);
		}

		// 底部三个数字牌
		for (int i = 0; i < 3; i++) {
			beans[i + 4] = new CardBean(blackDrawable);
			beans[i + 4].rect = new Rect(calendLRpadding + POKE_WIDTH * i
					+ (SIGN_WIDTH + calendLRpadding * 2) * i, mHeight - 120
					- POKE_HEIGHT, calendLRpadding + POKE_WIDTH * (i + 1)
					+ (SIGN_WIDTH + calendLRpadding * 2) * i, mHeight - 120);
		}

		// 画底部运算符号
		beans[7] = new CardBean(new CardDrawable(getContext(), R.drawable.add));
		beans[7].canMove = false;
		beans[7].isEmpty = false;
		beans[7].type = CardType.PLUG;
		beans[7].mValue = 0;
		beans[7].rect = new Rect(calendLRpadding * 2 + POKE_WIDTH,
		        mHeight - 210 - SIGN_HEIGHT, calendLRpadding * 2 + POKE_WIDTH + SIGN_WIDTH,
				mHeight - 210);

		// 画等于号
		beans[8] = new CardBean(
				new CardDrawable(getContext(), R.drawable.equal));
		beans[8].canMove = false;
		beans[8].isEmpty = false;
		beans[8].type = CardType.EQUAL;
		beans[8].rect = new Rect(calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH, 
		        mHeight - 210 - SIGN_HEIGHT, calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH * 2, 
				mHeight - 210);

		// 画运算符号, 4个
		for (int i = 0; i < 4; i++) {
			beans[i + 9] = new CardBean(new CardDrawable(getContext(),
					Flagconstant.signIds[i]));
			beans[i + 9].canMove = false;
			beans[i + 9].isEmpty = false;
			beans[i + 9].type = CardType.BUTTON;
			beans[i + 9].rect = new Rect(SIGN_WIDTH * i + signLRpadding
					* (i + 1), mHeight - 300 - SIGN_HEIGHT, SIGN_WIDTH
					* (i + 1) + signLRpadding * (i + 1), mHeight - 500);
			beans[i + 9].mValue = i;
		}

		// 画上下，还原和退出按钮，新版本可能去掉, 4个
		for (int i = 0; i < 4; i++) {
			beans[i + 13] = new CardBean(new CardDrawable(getContext(),
					Flagconstant.buttons[i]));
			beans[i + 13].canMove = false;
			beans[i + 13].isEmpty = false;
			beans[i + 13].type = CardType.BUTTON;
			beans[i + 13].rect = new Rect(BTN_WIDTH * i + btnLRpadding
					* (i + 1), mHeight - 20 - BTN_HEIGHT, BTN_WIDTH * (i + 1)
					+ btnLRpadding * (i + 1), mHeight - 20);
		}

		beans[17] = new CardBean(new CardDrawable(getContext(),
				R.drawable.answer_0));
		beans[17].canMove = false;
		beans[17].isEmpty = false;
		beans[17].type = CardType.BUTTON;
		beans[17].rect = new Rect(mWidth - btnLRpadding - BTN_WIDTH,
				mHeight - 375, mWidth - btnLRpadding, mHeight - 300);

		// 画局数计数器
		for (int i = 0; i < cicer.length; i++) {
			cicer[i] = new CiclerDrawable(Color.BLACK, CIRCLE_RADIAUS, i + 1,
					circleLRpadding * (i + 1) + 50 * i + CIRCLE_RADIAUS,
					mHeight - 600);
		}
	}

	private void resetGameCards() {
		// ��ԭ������Ŀ��
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
		
		DisplayMetrics display = getResources().getDisplayMetrics();
		
		POKE_WIDTH = (display.widthPixels - 280) / 4;
		POKE_HEIGHT = (POKE_WIDTH * 3) / 2;
		
		//backGround = getContext().getResources().getDrawable(R.drawable.desk_bg);
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
		//backGround.setBounds(0, 0, mWidth, mHeight);
		initData();
		calentGameCards(0);
	}

	public void onDraw(Canvas canvas) {
		//drawBackground(canvas);
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

	/*private void drawBackground(Canvas canvas) {
		if (backGround != null)
			backGround.draw(canvas);
	}*/

	private void drawCards(Canvas canvas) {
		for (int i = 0; i < beans.length - 5; i++) {
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
			beans[13].dr = new CardDrawable(getContext(), R.drawable.pre_0);
			beans[14].dr = new CardDrawable(getContext(), R.drawable.reset_0);
			beans[15].dr = new CardDrawable(getContext(), R.drawable.exit_0);
			beans[16].dr = new CardDrawable(getContext(), R.drawable.next_0);
			beans[17].dr = new CardDrawable(getContext(), R.drawable.answer_0);

			if (srcIndex > CardBean.NO_ID && srcIndex < beans.length) {
				endDrag(pointX, pointY);
			}/* else if (clickShowAnswerRect(pointX, pointY)) {
				if (rectListener != null)
					rectListener.onRectClick(SHOW_ANSWER);
			}*/
			invalidate();
			break;
		}
		return true;
	}

	private void flushAllRect() {
		// ��ʼ���������������Ŀ��
		for (int i = 0; i < 4; i++) {
			beans[i].mValue = CardBean.NO_ID;
			beans[i].canMove = true;
			beans[i].isEmpty = true;
			beans[i].dr = blackDrawable;
		}
		// ��ʼ��������
		for (int i = 0; i < 3; i++) {
			beans[i + 4].mValue = CardBean.NO_ID;
			beans[i + 4].canMove = true;
			beans[i + 4].isEmpty = true;
			beans[i + 4].dr = blackDrawable;
		}

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
                beans[13].setEmpty(false, new CardDrawable(getContext(),
                        R.drawable.pre_1), beans[13].mValue);
                if (beginGameTime - 4 >= 0) {
                    flushAllRect();
                    calentGameCards(beginGameTime - 4);
                }
                invalidate();
                break;

            case RESET_GAME:
                beans[14].setEmpty(false, new CardDrawable(getContext(),
                        R.drawable.reset_1), beans[14].mValue);
                flushAllRect();
                resetGameCards();
                invalidate();
                break;
                
            case NEXT_GAME:
                beans[16].setEmpty(false, new CardDrawable(getContext(),
                        R.drawable.next_1), beans[16].mValue);
                flushAllRect();
                calentGameCards(beginGameTime + 4);
                invalidate();
                break;
                
            case EXIT_GAME:
                if (rectListener != null) {
                    beans[15].dr = new CardDrawable(getContext(), R.drawable.exit_1);
                    invalidate();
                    rectListener.onRectClick(EXIT_GAME);
                }
                break;
        }
        return true;
    }

	/**
	 * 上一局，下一局，还原按钮处理事件
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 * @deprecated
	 * @see #onTouchEvent(MotionEvent)
	 */
	private boolean clickDownFunctionRect(int pointX, int pointY) {
		if (beans[14].isHint(pointX, pointY) && !beans[14].isEmpty) {
			// 还原
			beans[14].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.reset_1), beans[14].mValue);
			flushAllRect();
			resetGameCards();
			return true;
		} else if (beans[13].isHint(pointX, pointY) && !beans[13].isEmpty) {
			// 上一题
			beans[13].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.pre_1), beans[13].mValue);
			if (beginGameTime - 4 >= 0) {
				flushAllRect();
				calentGameCards(beginGameTime - 4);
			}
			return true;
		} else if (beans[16].isHint(pointX, pointY) && !beans[16].isEmpty) {
			// 下一题
			beans[16].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.next_1), beans[16].mValue);
			flushAllRect();
			calentGameCards(beginGameTime + 4);
			return true;
		} else if (beans[15].isHint(pointX, pointY) && !beans[15].isEmpty) {
			// 退出游戏
			if (rectListener != null) {
				beans[15].dr = new CardDrawable(getContext(), R.drawable.exit_1);
				invalidate();
				rectListener.onRectClick(EXIT_GAME);
			}
		} else if (beans[17].isHint(pointX, pointY) && !beans[17].isEmpty) {
			// 答案
			beans[17].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.answer_1), beans[17].mValue);
			return true;
		}
		return false;
	}

	public boolean clickShowAnswerRect(int pointX, int pointY) {
		if (beans[17].isHint(pointX, pointY) && !beans[17].isEmpty) {
			// ��ʾ��
			beans[17].setEmpty(false, new CardDrawable(getContext(),
					R.drawable.answer_0), beans[17].mValue);
			return true;
		}
		return false;
	}

	/**
	 * �����ƶ��ĵ������ڵ�λ��, ֻ�ڰ���ʱ��ʼ����
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	private boolean clickDownSignRect(int pointX, int pointY) {
		// ���ڼ�������
		for (int i = 9; i < 13; i++) {
			if (beans[i].isHint(pointX, pointY) && !beans[i].isEmpty) {
				beans[7].setEmpty(false, beans[i].dr, beans[i].mValue);
				return true;
			}
		}

		// �����л���������
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