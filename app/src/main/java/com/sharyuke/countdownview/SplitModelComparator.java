package com.sharyuke.countdownview;

import java.util.Comparator;

/**
 * SplitModelComparator
 * Created by sharyuke on 11/19/14.
 */
public class SplitModelComparator implements Comparator {

  @Override
  public int compare(Object lhs, Object rhs) {
    SplitModel splitModel1 = (SplitModel) lhs;
    SplitModel splitModel2 = (SplitModel) rhs;
    return splitModel2.getSplitNum() - splitModel1.getSplitNum();
  }
}
