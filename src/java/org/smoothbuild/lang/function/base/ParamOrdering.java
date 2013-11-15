package org.smoothbuild.lang.function.base;

import com.google.common.collect.Ordering;

public class ParamOrdering extends Ordering<Param> {
  public static final ParamOrdering PARAM_ORDERING = new ParamOrdering();

  private ParamOrdering() {}

  @Override
  public int compare(Param left, Param right) {
    return left.name().compareTo(right.name());
  }
}
