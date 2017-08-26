package org.smoothbuild.lang.function.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Parameters {

  public static ImmutableList<Parameter> filterRequiredParameters(Iterable<Parameter> parameters) {
    return filterParameters(parameters, true);
  }

  public static ImmutableList<Parameter> filterOptionalParameters(Iterable<Parameter> parameters) {
    return filterParameters(parameters, false);
  }

  private static ImmutableList<Parameter> filterParameters(Iterable<Parameter> parameters,
      boolean isRequired) {
    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    for (Parameter parameter : parameters) {
      if (parameter.isRequired() == isRequired) {
        builder.add(parameter);
      }
    }
    return builder.build();
  }

  public static ImmutableMap<Name, Parameter> parametersToMap(Iterable<Parameter> params) {
    ImmutableMap.Builder<Name, Parameter> builder = ImmutableMap.builder();
    for (Parameter parameter : params) {
      builder.put(parameter.name(), parameter);
    }
    return builder.build();
  }
}
