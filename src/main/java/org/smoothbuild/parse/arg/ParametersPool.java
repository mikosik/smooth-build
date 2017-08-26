package org.smoothbuild.parse.arg;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.function.base.Parameters.filterOptionalParameters;
import static org.smoothbuild.lang.function.base.Parameters.filterRequiredParameters;
import static org.smoothbuild.lang.function.base.Parameters.parametersToMap;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.allTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class ParametersPool {
  private final ImmutableMap<Name, Parameter> parameters;
  private final ImmutableMap<Type, TypedParametersPool> typePools;
  private final Map<Type, Set<Parameter>> optionalParametersMap;
  private final Map<Type, Set<Parameter>> requiredParametersMap;

  public ParametersPool(List<Parameter> parameters) {
    this.parameters = parametersToMap(parameters);
    this.optionalParametersMap = createParametersMap(filterOptionalParameters(parameters));
    this.requiredParametersMap = createParametersMap(filterRequiredParameters(parameters));
    this.typePools = createTypePools(optionalParametersMap, requiredParametersMap);
  }

  public Parameter take(Name name) {
    Parameter parameter = parameters.get(name);
    checkArgument(parameter != null);
    return take(parameter);
  }

  public Parameter take(Parameter parameter) {
    boolean hasBeenRemoved = remove(parameter);
    checkArgument(hasBeenRemoved);
    return parameter;
  }

  private boolean remove(Parameter parameter) {
    if (parameter.isRequired()) {
      return requiredParametersMap.get(parameter.type()).remove(parameter);
    } else {
      return optionalParametersMap.get(parameter.type()).remove(parameter);
    }
  }

  public TypedParametersPool assignableFrom(Type type) {
    return typePools.get(type);
  }

  public Set<Parameter> allRequired() {
    Set<Parameter> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParameters());
    }
    return result;
  }

  public Set<Parameter> allOptional() {
    Set<Parameter> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.optionalParameters());
    }
    return result;
  }

  private static ImmutableMap<Type, TypedParametersPool> createTypePools(
      Map<Type, Set<Parameter>> optionalParametersMap,
      Map<Type, Set<Parameter>> requiredParametersMap) {

    Builder<Type, TypedParametersPool> builder = ImmutableMap.builder();
    for (Type type : allTypes()) {
      Set<Parameter> optional = parametersAssignableFromType(type, optionalParametersMap);
      Set<Parameter> required = parametersAssignableFromType(type, requiredParametersMap);
      builder.put(type, new TypedParametersPool(optional, required));
    }

    return builder.build();
  }

  private static Set<Parameter> parametersAssignableFromType(Type type,
      Map<Type, Set<Parameter>> paramsMap) {
    Set<Parameter> parameters = paramsMap.get(type);
    for (Type currentType : allTypes()) {
      if (canConvert(type, currentType)) {
        parameters = Sets.union(parameters, paramsMap.get(currentType));
      }
    }
    return parameters;
  }

  private static Map<Type, Set<Parameter>> createParametersMap(
      ImmutableList<Parameter> parameters) {
    Map<Type, Set<Parameter>> map = new HashMap<>();
    for (Type type : allTypes()) {
      HashSet<Parameter> set = new HashSet<>();
      for (Parameter parameter : parameters) {
        if (parameter.type() == type) {
          set.add(parameter);
        }
      }
      map.put(type, set);
    }
    return map;
  }
}
