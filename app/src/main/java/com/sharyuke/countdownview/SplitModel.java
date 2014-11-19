package com.sharyuke.countdownview;

/**
 * SplitModel
 * Created by sharyuke on 11/19/14.
 */
public class SplitModel {
  private int splitNum;
  private String splitStr;
  private int splitBackground;
  private int splitPic;
  private int textColor;

  public SplitModel(int splitNum, String splitStr, int splitBackground, int splitPic,
      int textColor) {
    this.splitNum = splitNum;
    this.splitStr = splitStr;
    this.splitBackground = splitBackground;
    this.splitPic = splitPic;
    this.textColor = textColor;
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
      return new SplitModel(splitNum, splitStr, splitBackground, splitPic, textColor);
    }
  }
}
