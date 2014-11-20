package com.sharyuke.countdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by yuke on 11/17/14.
 * @version CountDownView yuke 0.0.6
 */
public class CountDownView extends LinearLayout {
  private static final String TAG = "CountDownView";

  private Map<String, TextView> tvs;

  /**
   * 计时状态
   */
  private static final int STATUS_COUNTING = 0x001;

  private CountOver mCountOver;

  public static int DAY_COUNT = 86400;
  public static int HOUR_COUNT = 3600;
  public static int MINUTE_COUNT = 60;
  public static int SECOND_COUNT = 1;

  /**
   * 1秒
   */
  private final static int SECOND = 1000;

  private ArrayList<SplitModel> mSplitModels = new ArrayList<SplitModel>();
  private SplitModelComparator splitModelComparator;

  private long mCountTime = 0;
  private TimeHandler mTimeHandler;

  private int leftPadding = -1;
  private int topPadding = -1;
  private int rightPadding = -1;
  private int bottomPadding = -1;
  private int padding = -1;
  private int width = -1;
  private int height = -1;
  private int textBackground = -1;
  private int splitBackground = -1;
  private int gravity;
  /**
   * 第一位最长长度
   */
  private int maxFirstLength;

  private boolean isTextBackgroundDefault = true;
  private boolean isSplitBackgroundDefault = true;
  private boolean ispPaddingDefault = false;
  private boolean isLeftPaddingDefault = true;
  private boolean isTopPaddingDefault = true;
  private boolean isRightPaddingDefault = true;
  private boolean isBottomPaddingDefault = true;

  /**
   * 记录textView
   */

  private final static String FLAG_A = "A";
  private final static String FLAG_B = "B";
  private final static String FLAG_C = "C";
  private final static String FLAG_D = "D";
  private final static String FLAG_E = "E";
  private final static String FLAG_F = "F";
  private final static String FLAG_G = "G";
  private final static String FLAG_H = "H";

  /**
   * 构造函数　一般用于xml布局
   */
  public CountDownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    splitModelComparator = new SplitModelComparator();
    mTimeHandler = new TimeHandler(this);
    tvs = new HashMap<String, TextView>();

    final TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MyText);
    final int N = typeArray.getIndexCount();
    for (int i = 0; i < N; i++) {
      int attr = typeArray.getIndex(i);
      if (attr == R.styleable.MyText_charPadding) {
        padding = typeArray.getDimensionPixelSize(attr, -1);
        ispPaddingDefault = true;
      } else if (attr == R.styleable.MyText_charPaddingLeft) {
        leftPadding = typeArray.getDimensionPixelSize(attr, -1);
        isLeftPaddingDefault = false;
      } else if (attr == R.styleable.MyText_charPaddingTop) {
        topPadding = typeArray.getDimensionPixelSize(attr, -1);
        isTopPaddingDefault = false;
      } else if (attr == R.styleable.MyText_charPaddingRight) {
        rightPadding = typeArray.getDimensionPixelSize(attr, -1);
        isRightPaddingDefault = false;
      } else if (attr == R.styleable.MyText_charPaddingButton) {
        bottomPadding = typeArray.getDimensionPixelSize(attr, -1);
        isBottomPaddingDefault = false;
      } else if (attr == R.styleable.MyText_charWidth) {
        width = typeArray.getLayoutDimension(attr, "char_width");
      } else if (attr == R.styleable.MyText_charHeight) {
        height = typeArray.getLayoutDimension(attr, "char_height");
      } else if (attr == R.styleable.MyText_charBackground) {
        textBackground = typeArray.getResourceId(attr, -1);
        isTextBackgroundDefault = false;
      } else if (attr == R.styleable.MyText_splitBackground) {
        splitBackground = typeArray.getResourceId(attr, -1);
        isSplitBackgroundDefault = false;
      } else if (attr == R.styleable.MyText_textGravity) {
        gravity = typeArray.getInt(attr, Gravity.LEFT | Gravity.TOP);
      }
    }
    typeArray.recycle();
  }

  private void setTextView(SplitModel splitModel, String name, String content, int index) {
    TextView textView = tvs.get(name);
    if (textView == null) {
      textView = new TextView(getContext());
      tvs.put(name, textView);
      LayoutParams layoutParams = new LayoutParams(width, height);
      textView.setLayoutParams(layoutParams);
      textView.setGravity(gravity);
      textView.setPadding(isLeftPaddingDefault ? padding : leftPadding,
          isTopPaddingDefault ? padding : topPadding,
          isRightPaddingDefault ? padding : rightPadding,
          isBottomPaddingDefault ? padding : bottomPadding);
      if (!isTextBackgroundDefault) {
        textView.setBackgroundResource(textBackground);
      }

      if (splitModel.getSplitBackground() != 0) {
        textView.setBackgroundResource(splitModel.getSplitBackground());
      }

      if (splitModel.getTextColor() != 0) {
        textView.setTextColor(splitModel.getTextColor());
      }

      textView.setText(content);
      addView(textView, index);
    } else {
      textView.setText(content);
    }
  }

  private ImageView getImageView() {
    ImageView imageView = new ImageView(getContext());
    if (!isTextBackgroundDefault) {
      imageView.setBackgroundResource(splitBackground);
    }
    return imageView;
  }

  public CountDownView setNormalFormat() {
    addSplitModel(new SplitModel.Builder().setSplitNum(DAY_COUNT).setSplitStr("天").build());
    addSplitModel(new SplitModel.Builder().setSplitNum(SECOND_COUNT)
        .setSplitStr("秒")
        .setIsFormat(true)
        .build());
    addSplitModel(new SplitModel.Builder().setSplitNum(HOUR_COUNT)
        .setSplitStr("时")
        .setIsFormat(true)
        .build());
    addSplitModel(new SplitModel.Builder().setSplitNum(MINUTE_COUNT)
        .setSplitStr("分")
        .setIsFormat(true)
        .build());
    return this;
  }

  public CountDownView addSplitModel(SplitModel splitModel) {
    if (splitModel != null && mSplitModels != null) {
      mSplitModels.add(splitModel);
    }
    Collections.sort(mSplitModels, splitModelComparator);
    return this;
  }

  public CountDownView clearSplitModel() {
    if (mSplitModels != null) {
      mSplitModels.clear();
    }
    return this;
  }

  /**
   * 设置倒计时总时间
   *
   * @param time 到时间总时间
   */
  public void setCountTime(String time) {
    try {
      this.mCountTime = str2int(time);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private long getTime() {
    if (mCountTime > 0) {
      return mCountTime--;
    } else {
      stopCount();
      if (mCountOver != null) {
        mCountOver.onCountOver();
      }
      return mCountTime;
    }
  }

  /**
   * 设置倒计时时间
   *
   * @param time 　倒计时时间
   */
  public CountDownView setCountTime(int time) {
    if (time < 0) {
      throw new IllegalArgumentException("count time must be positive number");
    }
    this.mCountTime = time;
    return this;
  }

  /**
   * 设置倒计时时间　引用string.xml资源文件
   *
   * @param res 资源Id
   */
  public void setCountTimeByRes(int res) {
    setCountTime(getContext().getResources().getString(res));
  }

  private void checkIsValid(String time) {
    if (TextUtils.isEmpty(time)) {
      throw new IllegalArgumentException("count time must not be null or empty");
    }

    for (int i = 0; i < time.length(); i++) {
      if ('0' > time.charAt(i) || time.charAt(i) > '9') {
        throw new IllegalArgumentException("count time must be positive number");
      }
    }
  }

  private int str2int(String time) {
    checkIsValid(time);
    return Integer.parseInt(time);
  }

  /**
   * 开始倒计时
   */
  public void startCount() {
    if (mTimeHandler != null) {
      mTimeHandler.removeMessages(STATUS_COUNTING);
      mTimeHandler.sendEmptyMessageDelayed(STATUS_COUNTING, 0);
    }
  }

  /**
   * 清除显示
   */
  public void clear() {
    stopCount();
    //todo
  }

  /**
   * 设置倒计时归零回调函数
   *
   * @param countOver 回调
   */
  public void setOnCountOverListener(CountOver countOver) {
    this.mCountOver = countOver;
  }

  /**
   * 暂停倒计时
   */
  public void stopCount() {
    if (mTimeHandler != null) {
      mTimeHandler.removeMessages(STATUS_COUNTING);
    }
  }

  private class TimeHandler extends Handler {
    CountDownView tv;

    public TimeHandler(CountDownView tv) {
      this.tv = tv;
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      sendEmptyMessageDelayed(STATUS_COUNTING, SECOND);

      long time = tv.getTime();
      int position = 0;

      for (int i = 0; i < mSplitModels.size(); i++) {
        StringBuilder timeStr = new StringBuilder();
        int splitNum = mSplitModels.get(i).getSplitNum();
        if (splitNum > 0) {
          timeStr.append(formatTime(time / splitNum, i == 0 ? 0
              : mSplitModels.get(i - 1).getSplitNum() / (i == mSplitModels.size() - 1 ? 1
                  : mSplitModels.get(i).getSplitNum()), mSplitModels.get(i)));

          if (!mSplitModels.get(i).isFormat()) {

            if (mSplitModels.get(i).isSplitStr()) {
              setTextView(mSplitModels.get(i), FLAG_A + String.valueOf(i), timeStr.toString(),
                  position++);
              setTextView(mSplitModels.get(i), FLAG_B + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);
            } else {
              setTextView(mSplitModels.get(i), FLAG_C + String.valueOf(i),
                  timeStr.append(mSplitModels.get(i).getSplitStr()).toString(), position++);
            }

            if (mSplitModels.get(i).getSplitPic() != 0) {
              ImageView imageView = getImageView();
              imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              addView(imageView);
            }
          } else {
            if (mSplitModels.get(i).isSplitText()) {
              if (i == 0) {
                if (maxFirstLength > timeStr.length()) {
                  for (int j = timeStr.length() - 1; j < maxFirstLength; j++) {
                    setTextView(mSplitModels.get(i),
                        FLAG_D + String.valueOf(i) + "-" + String.valueOf(timeStr.length() - j + 1),
                        "0", position++);
                  }
                } else {
                  maxFirstLength = timeStr.length();
                }
              }
              for (int j = 0; j < timeStr.length(); j++) {
                setTextView(mSplitModels.get(i),
                    FLAG_D + String.valueOf(i) + "-" + String.valueOf(timeStr.length() - j),
                    String.valueOf(timeStr.charAt(j)), position++);
              }

              setTextView(mSplitModels.get(i), FLAG_E + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);
            } else if (mSplitModels.get(i).isSplitStr()) {
              setTextView(mSplitModels.get(i), FLAG_F + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);
              setTextView(mSplitModels.get(i), FLAG_G + String.valueOf(i), String.valueOf(time),
                  position++);
            } else {
              setTextView(mSplitModels.get(i), FLAG_H + String.valueOf(i),
                  time + mSplitModels.get(i).getSplitStr(), position++);
            }

            if (mSplitModels.get(i).getSplitPic() != 0) {
              ImageView imageView = new ImageView(getContext());
              imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              addView(imageView);
            }
          }
          time = time % splitNum;
        }
      }
    }
  }

  /**
   * 倒计时回调接口
   */
  public interface CountOver {
    /**
     * 回调方法
     */
    void onCountOver();
  }

  private String formatTime(long time, int maxLong, SplitModel splitModel) {
    if (maxLong == 0) {
      return String.valueOf(time);
    }
    StringBuilder fill = new StringBuilder();
    for (int i = 0; i < getPlaces(maxLong) - getPlaces(time); i++) {
      fill.append("0");
    }
    return !splitModel.isFormat() ? "" + time : (time > 9 ? "" + time : fill.toString() + time);
  }

  private int getPlaces(long count) {
    int place = 1;
    while (count / 10 >= 1) {
      place++;
      count /= 10;
    }
    return place;
  }
}
