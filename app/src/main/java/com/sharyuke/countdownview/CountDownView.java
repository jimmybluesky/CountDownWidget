package com.sharyuke.countdownview;

import android.content.Context;
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
  private boolean isFormat = true;

  /**
   * 构造函数　一般用于xml布局
   */
  public CountDownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    splitModelComparator = new SplitModelComparator();
    mTimeHandler = new TimeHandler(this);
  }

  public CountDownView setNormalFormat() {
    addSplitModel(new SplitModel.Builder().setSplitNum(86400).setSplitStr("天").build());
    addSplitModel(new SplitModel.Builder().setSplitNum(1).setSplitStr("秒").build());
    addSplitModel(new SplitModel.Builder().setSplitNum(3600).setSplitStr("时").build());
    addSplitModel(new SplitModel.Builder().setSplitNum(60).setSplitStr("分").build());
    return this;
  }

  public void addSplitModel(SplitModel splitModel) {
    if (splitModel != null) {
      mSplitModels.add(splitModel);
    }
    Collections.sort(mSplitModels, splitModelComparator);
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

  /**
   * 是否格式化时间显示，例如９秒显示为09秒，
   *
   * @param isFormat 　true 需要
   */
  public void setFormat(boolean isFormat) {
    this.isFormat = isFormat;
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
                  : mSplitModels.get(i).getSplitNum())));

          if (!isFormat) {
            TextView textView = new TextView(getContext());
            if (mSplitModels.get(i).getSplitBackground() != 0) {
              textView.setBackgroundResource(mSplitModels.get(i).getSplitBackground());
            }
            textView.setGravity(Gravity.CENTER);
            textView.setText(timeStr.append(mSplitModels.get(i).getSplitStr()).toString());
            addView(textView);

            if (mSplitModels.get(i).getSplitPic() != 0) {
              ImageView imageView = new ImageView(getContext());
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
    for (int i = 0; i < time.length(); i++) {
      TextView textView = new TextView(getContext());
      textView.setText(String.valueOf(time.charAt(i)));
      if (mSplitModels.get(i).getSplitBackground() != 0) {
        textView.setBackgroundResource(splitModel.getSplitBackground());
      }
      addView(textView);
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

  private String formatTime(int time, int maxLong) {
    if (maxLong == 0) {
      return String.valueOf(time);
    }
    StringBuilder fill = new StringBuilder();
    for (int i = 0; i < getPlaces(maxLong) - getPlaces(time); i++) {
      fill.append("0");
    }
    return !isFormat ? "" + time : (time > 9 ? "" + time : fill.toString() + time);
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
