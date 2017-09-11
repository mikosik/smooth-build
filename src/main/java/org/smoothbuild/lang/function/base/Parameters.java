package org.smoothbuild.lang.function.base;

import static com.google.common.base.Predicates.not;
import static org.smoothbuild.util.Lists.filter;

import java.util.List;

import com.google.common.collect.ImmutableMap;

public class Parameters {
  public static List<Parameter> filterRequiredParameters(List<Parameter> parameters) {
    return filter(parameters, Parameter::isRequired);
  }

  public static List<Parameter> filterOptionalParameters(List<Parameter> parameters) {
    return filter(parameters, not(Parameter::isRequired));
  }

  public static ImmutableMap<Name, Parameter> parametersToMap(Iterable<Parameter> params) {
    ImmutableMap.Builder<Name, Parameter> builder = ImmutableMap.builder();
    for (Parameter parameter : params) {
      builder.put(parameter.name(), parameter);
    }
    return builder.build();
  }
}
