package com.sharyuke.countdownview;

/**
 * SplitModel
 * Created by sharyuke on 11/19/14.
 */
public class SplitModel {

  /**
   * 单位进制
   */
  private int splitNum;

  /**
   * 单位
   */
  private String splitStr;

  /**
   * 分割背景
   */
  private int splitBackground;

  /**
   * 分割的图片
   */
  private int splitPic;

  /**
   * 字体颜色
   */
  private int textColor;

  /**
   * 是否分开数字和单位
   */
  private boolean isSplitStr;

  /**
   * 是否格式化数组显示 例如 1 格式化成 01
   */
  private boolean isFormat;

  /**
   * 是否分开每个阿拉伯数字
   */
  private boolean isSplitText;

  public SplitModel(int splitNum, String splitStr, int splitBackground, int splitPic, int textColor,
      boolean isSplitStr, boolean isFormat, boolean isSplitText) {
    this.splitNum = splitNum;
    this.splitStr = splitStr;
    this.splitBackground = splitBackground;
    this.splitPic = splitPic;
    this.textColor = textColor;
    this.isSplitStr = isSplitStr;
    this.isFormat = isFormat;
    this.isSplitText = isSplitText;
  }

  public boolean isSplitStr() {
    return isSplitStr;
  }

  public boolean isSplitText() {
    return isSplitText;
  }

  public boolean isFormat() {
    return isFormat;
  }

  public int getTextColor() {
    return textColor;
  }

  public int getSplitNum() {
    return splitNum;
  }

  public String getSplitStr() {
    return splitStr == null ? "" : splitStr;
  }

  public int getSplitBackground() {
    return splitBackground;
  }

  public int getSplitPic() {
    return splitPic;
  }

  public static class Builder {
    private int splitNum;
    private String splitStr;
    private int splitBackground;
    private int splitPic;
    private int textColor;
    private boolean isSplitStr;
    private boolean isFormat;
    private boolean isSplitText;

    public Builder setIsSplitStr(boolean isSplitStr) {
      this.isSplitStr = isSplitStr;
      return this;
    }

    public Builder setIsSplitText(boolean isSplitText) {
      this.isSplitText = isSplitText;
      this.isFormat = isSplitText;
      this.isSplitStr = isSplitText;
      return this;
    }

    public Builder setIsFormat(boolean isFormat) {
      this.isFormat = isFormat;
      return this;
    }

    public Builder setSplitNum(int splitNum) {
      this.splitNum = splitNum;
      return this;
    }

    public Builder setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
    }

    public Builder setSplitStr(String splitStr) {
      this.splitStr = splitStr;
      return this;
    }

    public Builder setSplitBackground(int splitBackground) {
      this.splitBackground = splitBackground;
      return this;
    }

    public Builder setSplitPic(int splitPic) {
      this.splitPic = splitPic;
      return this;
    }

    public SplitModel build() {
      return new SplitModel(splitNum, splitStr, splitBackground, splitPic, textColor, isSplitStr,
          isFormat, isSplitText);
    }
  }
}
