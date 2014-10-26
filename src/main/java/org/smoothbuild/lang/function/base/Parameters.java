package org.smoothbuild.lang.function.base;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class Parameters {

  public static ImmutableList<Parameter> filterRequiredParameters(
      ImmutableList<Parameter> parameters) {
    return filterParameters(parameters, true);
  }

  public static ImmutableList<Parameter> filterOptionalParameters(
      ImmutableList<Parameter> parameters) {
    return filterParameters(parameters, false);
  }

  private static ImmutableList<Parameter> filterParameters(ImmutableList<Parameter> parameters,
      boolean isRequired) {
    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    for (Parameter parameter : parameters) {
      if (parameter.isRequired() == isRequired) {
        builder.add(parameter);
      }
    }
    return builder.build();
  }

  public static ImmutableList<String> parametersToNames(Iterable<Parameter> params) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (Parameter parameter : params) {
      builder.add(parameter.name());
    }
    return builder.build();
  }

  public static ImmutableMap<String, Parameter> parametersToMap(Parameter... parameters) {
    return parametersToMap(Arrays.asList(parameters));
  }

  public static ImmutableMap<String, Parameter> parametersToMap(Iterable<Parameter> params) {
    ImmutableMap.Builder<String, Parameter> builder = ImmutableMap.builder();
    for (Parameter parameter : params) {
      builder.put(parameter.name(), parameter);
    }
    return builder.build();
  }

  /**
   * @return Parameters ordered lexicographically by their names.
   */
  public static ImmutableList<Parameter> sortedParameters(Iterable<Parameter> params) {
    Set<String> names = Sets.newHashSet();

    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    for (Parameter parameter : ParameterOrdering.PARAMETER_ORDERING.sortedCopy(params)) {
      String name = parameter.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException("Duplicate param name = '" + name + "'");
      }
      builder.add(parameter);
      names.add(name);
    }
    return builder.build();
  }
}
