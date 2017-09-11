package org.smoothbuild.lang.function.base;

import static com.google.common.base.Predicates.not;
import static org.smoothbuild.util.Lists.filter;

import java.util.List;

public class Parameters {
  public static List<Parameter> filterRequiredParameters(List<Parameter> parameters) {
    return filter(parameters, Parameter::isRequired);
  }

  public static List<Parameter> filterOptionalParameters(List<Parameter> parameters) {
    return filter(parameters, not(Parameter::isRequired));
  }
}
