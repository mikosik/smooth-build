package org.smoothbuild.lang.function.base;

import com.google.common.collect.Ordering;

public class ParameterOrdering extends Ordering<Parameter> {
  public static final ParameterOrdering PARAMETER_ORDERING = new ParameterOrdering();

  private ParameterOrdering() {}

  @Override
  public int compare(Parameter left, Parameter right) {
    return left.name().compareTo(right.name());
  }
}
