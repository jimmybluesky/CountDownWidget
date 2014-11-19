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

  public SplitModel(int splitNum, String splitStr, int splitBackground, int splitPic) {
    this.splitNum = splitNum;
    this.splitStr = splitStr;
    this.splitBackground = splitBackground;
    this.splitPic = splitPic;
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

    public Builder setSplitNum(int splitNum) {
      this.splitNum = splitNum;
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
      return new SplitModel(splitNum, splitStr, splitBackground, splitPic);
    }
  }
}
