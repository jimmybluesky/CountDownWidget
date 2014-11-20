package com.sharyuke.countdownview;

import android.content.Context;
import android.content.res.TypedArray;
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

/**
 * @author Created by yuke on 11/17/14.
 * @version CountDownView yuke 0.0.6
 */
public class CountDownView extends LinearLayout {
  private static final String TAG = "CountDownView";
  private static final int DAY = 86400;
  private static final int HOUR = 3600;
  private static final int MINUTE = 60;
  private static final int SECOND = 1000;

  /**
   * 计时状态
   */
  private static final int STATUS_COUNTING = 0x001;

  private CountOver mCountOver;

  private ArrayList<SplitModel> mSplitModels = new ArrayList<SplitModel>();
  private SplitModelComparator splitModelComparator;

  private int mCountTime = 0;
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

  private boolean isTextBackgroundDefault = true;
  private boolean isSplitBackgroundDefault = true;
  private boolean ispPaddingDefault = true;

  /**
   * 构造函数　一般用于xml布局
   */
  public CountDownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    splitModelComparator = new SplitModelComparator();
    mTimeHandler = new TimeHandler(this);

    final TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MyText);
    final int N = typeArray.getIndexCount();
    for (int i = 0; i < N; i++) {
      int attr = typeArray.getIndex(i);
      if (attr == R.styleable.MyText_charPadding) {
        padding = typeArray.getDimensionPixelSize(attr, -1);
        ispPaddingDefault = false;
      } else if (attr == R.styleable.MyText_charPaddingLeft) {
        leftPadding = typeArray.getDimensionPixelSize(attr, -1);
      } else if (attr == R.styleable.MyText_charPaddingTop) {
        topPadding = typeArray.getDimensionPixelSize(attr, -1);
      } else if (attr == R.styleable.MyText_charPaddingRight) {
        rightPadding = typeArray.getDimensionPixelSize(attr, -1);
      } else if (attr == R.styleable.MyText_charPaddingButton) {
        bottomPadding = typeArray.getDimensionPixelSize(attr, -1);
      } else if (attr == R.styleable.MyText_charWidth) {
        width = typeArray.getDimensionPixelSize(attr, -1);
      } else if (attr == R.styleable.MyText_charHeight) {
        height = typeArray.getDimensionPixelSize(attr, -1);
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

  private TextView getTextView(SplitModel splitModel) {
    TextView textView = new TextView(getContext());
    textView.setHeight(height);
    textView.setWidth(width);
    textView.setGravity(gravity);
    textView.setPadding(ispPaddingDefault ? padding : leftPadding,
        ispPaddingDefault ? padding : topPadding, ispPaddingDefault ? padding : rightPadding,
        ispPaddingDefault ? padding : bottomPadding);
    if (!isTextBackgroundDefault) {
      textView.setBackgroundResource(textBackground);
    }

    if (splitModel.getSplitBackground() != 0) {
      textView.setBackgroundResource(splitModel.getSplitBackground());
    }

    if (splitModel.getTextColor() != 0) {
      textView.setTextColor(splitModel.getTextColor());
    }

    return textView;
  }

  private ImageView getImageView() {
    ImageView imageView = new ImageView(getContext());
    if (!isTextBackgroundDefault) {
      imageView.setBackgroundResource(splitBackground);
    }
    return imageView;
  }

  public CountDownView setNormalFormat() {
    addSplitModel(new SplitModel.Builder().setSplitNum(86400).setSplitStr("天").build());
    addSplitModel(
        new SplitModel.Builder().setSplitNum(1).setSplitStr("秒").setIsFormat(true).build());
    addSplitModel(
        new SplitModel.Builder().setSplitNum(3600).setSplitStr("时").setIsFormat(true).build());
    addSplitModel(
        new SplitModel.Builder().setSplitNum(60).setSplitStr("分").setIsFormat(true).build());
    return this;
  }

  public void addSplitModel(SplitModel splitModel) {
    if (splitModel != null && mSplitModels != null) {
      mSplitModels.add(splitModel);
    }
    Collections.sort(mSplitModels, splitModelComparator);
  }

  public void clearSplitModel() {
    if (mSplitModels != null) {
      mSplitModels.clear();
    }
  }

  /**
   * 设置倒计时总时间
   *
   * @param time 到时间总时间
   */
  public void setCountTime(String time) {
    this.mCountTime = str2int(time);
  }

  private int getTime() {
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
  public void setCountTime(int time) {
    if (time < 0) {
      throw new IllegalArgumentException("count time must be positive number");
    }
    this.mCountTime = time;
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

      int time = tv.getTime();

      removeAllViews();
      for (int i = 0; i < mSplitModels.size(); i++) {
        StringBuilder timeStr = new StringBuilder();
        int splitNum = mSplitModels.get(i).getSplitNum();
        if (splitNum > 0) {
          timeStr.append(formatTime(time / splitNum, i == 0 ? 0
              : mSplitModels.get(i - 1).getSplitNum() / (i == mSplitModels.size() - 1 ? 1
                  : mSplitModels.get(i).getSplitNum()), mSplitModels.get(i)));

          if (!mSplitModels.get(i).isFormat()) {
            TextView textView = getTextView(mSplitModels.get(i));
            TextView textViewStr = getTextView(mSplitModels.get(i));

            if (mSplitModels.get(i).isSplitStr()) {
              textView.setText(timeStr.toString());
              addView(textView);
              textViewStr.setText(mSplitModels.get(i).getSplitStr());
              addView(textViewStr);
            } else {
              textView.setText(timeStr.append(mSplitModels.get(i).getSplitStr()).toString());
              addView(textView);
            }

            if (mSplitModels.get(i).getSplitPic() != 0) {
              ImageView imageView = getImageView();
              imageView.setImageResource(mSplitModels.get(i).getSplitPic());
              addView(imageView);
            }
          } else {
            drawSplit(timeStr.toString(), mSplitModels.get(i));
          }
          time = time % splitNum;
        }
      }
    }
  }

  private void drawSplit(String time, SplitModel splitModel) {
    if (time == null) return;
    if (splitModel.isSplitText()) {
      for (int i = 0; i < time.length(); i++) {
        TextView textView = getTextView(splitModel);
        textView.setText(String.valueOf(time.charAt(i)));
        addView(textView);
      }
      TextView textViewStr = getTextView(splitModel);
      textViewStr.setText(splitModel.getSplitStr());
      addView(textViewStr);
    } else {
      TextView textView = getTextView(splitModel);

      if (splitModel.isSplitStr()) {
        TextView textViewStr = getTextView(splitModel);

        textView.setText(time);
        textViewStr.setText(splitModel.getSplitStr());
        addView(textView);
        addView(textViewStr);
      } else {
        textView.setText(time + splitModel.getSplitStr());
        addView(textView);
      }
    }

    if (splitModel.getSplitPic() != 0) {
      ImageView imageView = new ImageView(getContext());
      imageView.setImageResource(splitModel.getSplitPic());
      addView(imageView);
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

  private String formatTime(int time, int maxLong, SplitModel splitModel) {
    if (maxLong == 0) {
      return String.valueOf(time);
    }
    StringBuilder fill = new StringBuilder();
    for (int i = 0; i < getPlaces(maxLong) - getPlaces(time); i++) {
      fill.append("0");
    }
    return !splitModel.isFormat() ? "" + time : (time > 9 ? "" + time : fill.toString() + time);
  }

  private int getPlaces(int count) {
    int place = 1;
    while (count / 10 >= 1) {
      place++;
      count /= 10;
    }
    return place;
  }
}
