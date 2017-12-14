package org.smoothbuild.parse.arg;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.Types.allTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class ParametersPool {
  private final TypeSystem typeSystem;
  private final ImmutableMap<Type, TypedParametersPool> typePools;
  private final Map<Type, Set<ParameterInfo>> optionalParametersMap;
  private final Map<Type, Set<ParameterInfo>> requiredParametersMap;

  public ParametersPool(TypeSystem typeSystem, Collection<? extends ParameterInfo> optional,
      Collection<? extends ParameterInfo> required) {
    this.typeSystem = typeSystem;
    this.optionalParametersMap = createParametersMap(optional);
    this.requiredParametersMap = createParametersMap(required);
    this.typePools = createTypePools(optionalParametersMap, requiredParametersMap);
  }

  public ParameterInfo take(ParameterInfo parameter) {
    boolean hasBeenRemoved = remove(parameter);
    checkArgument(hasBeenRemoved);
    return parameter;
  }

  private boolean remove(ParameterInfo parameter) {
    if (requiredParametersMap.get(parameter.type()).contains(parameter)) {
      return requiredParametersMap.get(parameter.type()).remove(parameter);
    } else {
      return optionalParametersMap.get(parameter.type()).remove(parameter);
    }
  }

  public TypedParametersPool assignableFrom(Type type) {
    return typePools.get(type);
  }

  public Set<ParameterInfo> allRequired() {
    Set<ParameterInfo> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParameters());
    }
    return result;
  }

  public Set<ParameterInfo> allOptional() {
    Set<ParameterInfo> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.optionalParameters());
    }
    return result;
  }

  private ImmutableMap<Type, TypedParametersPool> createTypePools(
      Map<Type, Set<ParameterInfo>> optionalParametersMap,
      Map<Type, Set<ParameterInfo>> requiredParametersMap) {

    Builder<Type, TypedParametersPool> builder = ImmutableMap.builder();
    for (Type type : allTypes()) {
      Set<ParameterInfo> optional = parametersAssignableFromType(type, optionalParametersMap);
      Set<ParameterInfo> required = parametersAssignableFromType(type, requiredParametersMap);
      builder.put(type, new TypedParametersPool(optional, required));
    }

    return builder.build();
  }

  private Set<ParameterInfo> parametersAssignableFromType(Type type,
      Map<Type, Set<ParameterInfo>> paramsMap) {
    Set<ParameterInfo> parameters = paramsMap.get(type);
    for (Type currentType : allTypes()) {
      if (typeSystem.canConvert(type, currentType)) {
        parameters = Sets.union(parameters, paramsMap.get(currentType));
      }
    }
    return parameters;
  }

  private static Map<Type, Set<ParameterInfo>> createParametersMap(
      Iterable<? extends ParameterInfo> names) {
    Map<Type, Set<ParameterInfo>> map = new HashMap<>();
    for (Type type : allTypes()) {
      Set<ParameterInfo> set = new HashSet<>();
      for (ParameterInfo name : names) {
        if (name.type().equals(type)) {
          set.add(name);
        }
      }
      map.put(type, set);
    }
    return map;
  }
}
