package com.sharyuke.countdownview;

import java.util.Comparator;

/**
 * SplitModelComparator
 * Created by sharyuke on 11/19/14.
 */
public class SplitModelComparator implements Comparator<SplitModel> {

  @Override
  public int compare(SplitModel lhs, SplitModel rhs) {
    return rhs.getSplitNum() - lhs.getSplitNum();
  }
}
