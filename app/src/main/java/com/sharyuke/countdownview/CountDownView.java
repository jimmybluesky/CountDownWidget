package com.sharyuke.countdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
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
 * @version CountDownView yuke 0.0.7
 */
public class CountDownView extends LinearLayout {
  private static final String TAG = "CountDownView";

  private Map<String, TextView> tvs;
  private Map<String, ImageView> imgs;

  /**
   * 计时状态
   */
  private static final int STATUS_COUNTING = 0x001;

  private CountOver mCountOver;

  public final static int DAY_COUNT = 86400;
  public final static int HOUR_COUNT = 3600;
  public final static int MINUTE_COUNT = 60;
  public final static int SECOND_COUNT = 1;

  public final static int FLAG_NO_BACKGROUND = -1;

  /**
   * 1秒
   */
  private final static int SECOND = 1000;

  private ArrayList<SplitModel> mSplitModels = new ArrayList<>();
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
   * 字体颜色
   */
  private int textColor;
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
   * 记录开始时间
   */
  private long mStartTimeMillis;

  /**
   * 构造函数　一般用于xml布局
   */
  public CountDownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    splitModelComparator = new SplitModelComparator();
    mTimeHandler = new TimeHandler(this);
    tvs = new HashMap<>();
    imgs = new HashMap<>();

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
      } else if (attr == R.styleable.MyText_textColor) {
        textColor = typeArray.getColor(attr, Color.BLACK);
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
      } else {
        textView.setTextColor(textColor);
      }

      textView.setText(content);
      addView(textView, index);
    } else {
      textView.setText(content);
    }
  }

  private ImageView getImageView(String name, int index) {
    ImageView imageView = imgs.get(name);
    if (imageView == null) {
      LayoutParams layoutParams =
          new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
      imageView = new ImageView(getContext());
      imgs.put(name, imageView);
      imageView.setLayoutParams(layoutParams);
      if (!isSplitBackgroundDefault) {
        imageView.setImageResource(splitBackground);
      }
      addView(imageView, index);
    }
    return imageView;
  }

  public CountDownView setNormalFormat() {
    clearSplitModel();
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
    setCountTime(str2long(time));
  }

  private long getTime() {

    // 剩余时间
    long remainTime = (mStartTimeMillis + mCountTime - System.currentTimeMillis()) / 1000;

    if (remainTime > 0) {
      return remainTime;
    } else {
      stopCount();
      if (mCountOver != null) {
        mCountOver.onCountOver();
      }
      return remainTime;
    }
  }

  /**
   * 设置倒计时时间
   *
   * @param time 　倒计时时间
   */
  public CountDownView setCountTime(long time) {
    if (time < 0) {
      throw new IllegalArgumentException("count time must be positive number");
    }
    this.mCountTime = time * 1000;
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

  private long str2long(String time) {
    checkIsValid(time);
    return Long.parseLong(time);
  }

  /**
   * 开始倒计时
   */
  public void startCount() {
    mStartTimeMillis = System.currentTimeMillis();
    if (mTimeHandler != null) {
      mTimeHandler.removeMessages(STATUS_COUNTING);
      mTimeHandler.sendEmptyMessage(STATUS_COUNTING);
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

          // 如果数字要格式化显示
          if (!mSplitModels.get(i).isFormat()) {

            // 如果数字和单位要分开
            if (mSplitModels.get(i).isSplitStr()) {

              setTextView(mSplitModels.get(i), FLAG_A + String.valueOf(i), timeStr.toString(),
                  position++);

              setTextView(mSplitModels.get(i), FLAG_B + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);
            } else {

              // 数字和单位不分开
              setTextView(mSplitModels.get(i), FLAG_C + String.valueOf(i),
                  timeStr.append(mSplitModels.get(i).getSplitStr()).toString(), position++);
            }

            if (!isSplitBackgroundDefault) {
              ImageView imageView = getImageView(FLAG_A + String.valueOf(i), position++);
              if (mSplitModels.get(i).getSplitPic() != 0) {
                imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              }
            }
          } else {

            // 如果要分开每个数字
            if (mSplitModels.get(i).isSplitText()) {
              if (i == 0) {
                if (maxFirstLength > timeStr.length()) {
                  for (int j = 0; j < maxFirstLength - timeStr.length(); j++) {
                    setTextView(mSplitModels.get(i),
                        FLAG_D + String.valueOf(i) + "-" + String.valueOf(j), "0", position++);
                  }
                } else {
                  maxFirstLength = timeStr.length();
                }
              }

              for (int j = 0; j < timeStr.length(); j++) {
                int index = i == 0 ? maxFirstLength - timeStr.length() + j : j;

                setTextView(mSplitModels.get(i),
                    FLAG_D + String.valueOf(i) + "-" + String.valueOf(index),
                    String.valueOf(timeStr.charAt(j)), position++);
              }

              setTextView(mSplitModels.get(i), FLAG_E + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);
            } else if (mSplitModels.get(i).isSplitStr()) {

              // 分开计数器和单位

              // 设置单位
              setTextView(mSplitModels.get(i), FLAG_F + String.valueOf(i),
                  mSplitModels.get(i).getSplitStr(), position++);

              // 设置时间
              setTextView(mSplitModels.get(i), FLAG_G + String.valueOf(i), timeStr.toString(),
                  position++);
            } else {

              // 计数和单位不分开
              setTextView(mSplitModels.get(i), FLAG_H + String.valueOf(i),
                  timeStr.toString() + mSplitModels.get(i).getSplitStr(), position++);
            }

            if (!isSplitBackgroundDefault) {
              ImageView imageView = getImageView(FLAG_A + String.valueOf(i), position++);
              if (mSplitModels.get(i).getSplitPic() == FLAG_NO_BACKGROUND) {
                imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              } else if (mSplitModels.get(i).getSplitPic() != 0) {
                imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              }
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
    return !splitModel.isFormat() ? "" + time : fill.toString() + time;
  }

  private int getPlaces(long count) {
    int place = 1;
    while ((count /= 10) >= 1) {
      place++;
    }
    return place;
  }
}
