package org.smoothbuild.lang.function.base;

import com.google.common.collect.Ordering;

public class ParameterOrdering extends Ordering<Parameter> {
  public static final ParameterOrdering PARAMETER_ORDERING = new ParameterOrdering();

  private ParameterOrdering() {}

  public int compare(Parameter left, Parameter right) {
    return left.name().toString().compareTo(right.name().toString());
  }
}
