package org.smoothbuild.parse.arg;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.allTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class ParametersPool {
  private final ImmutableMap<Name, TypedName> parameters;
  private final ImmutableMap<Type, TypedParametersPool> typePools;
  private final Map<Type, Set<TypedName>> optionalParametersMap;
  private final Map<Type, Set<TypedName>> requiredParametersMap;

  public ParametersPool(List<? extends TypedName> optional, List<? extends TypedName> required) {
    this.parameters = createTypedNamesMap(required, optional);
    this.optionalParametersMap = createParametersMap(optional);
    this.requiredParametersMap = createParametersMap(required);
    this.typePools = createTypePools(optionalParametersMap, requiredParametersMap);
  }

  public TypedName take(Name name) {
    TypedName parameter = parameters.get(name);
    checkArgument(parameter != null);
    return take(parameter);
  }

  public TypedName take(TypedName parameter) {
    boolean hasBeenRemoved = remove(parameter);
    checkArgument(hasBeenRemoved);
    return parameter;
  }

  private boolean remove(TypedName parameter) {
    if (requiredParametersMap.get(parameter.type()).contains(parameter)) {
      return requiredParametersMap.get(parameter.type()).remove(parameter);
    } else {
      return optionalParametersMap.get(parameter.type()).remove(parameter);
    }
  }

  public TypedParametersPool assignableFrom(Type type) {
    return typePools.get(type);
  }

  public Set<TypedName> allRequired() {
    Set<TypedName> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParameters());
    }
    return result;
  }

  public Set<TypedName> allOptional() {
    Set<TypedName> result = new HashSet<>();
    for (TypedParametersPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.optionalParameters());
    }
    return result;
  }

  private static ImmutableMap<Type, TypedParametersPool> createTypePools(
      Map<Type, Set<TypedName>> optionalParametersMap,
      Map<Type, Set<TypedName>> requiredParametersMap) {

    Builder<Type, TypedParametersPool> builder = ImmutableMap.builder();
    for (Type type : allTypes()) {
      Set<TypedName> optional = parametersAssignableFromType(type, optionalParametersMap);
      Set<TypedName> required = parametersAssignableFromType(type, requiredParametersMap);
      builder.put(type, new TypedParametersPool(optional, required));
    }

    return builder.build();
  }

  private static Set<TypedName> parametersAssignableFromType(Type type,
      Map<Type, Set<TypedName>> paramsMap) {
    Set<TypedName> parameters = paramsMap.get(type);
    for (Type currentType : allTypes()) {
      if (canConvert(type, currentType)) {
        parameters = Sets.union(parameters, paramsMap.get(currentType));
      }
    }
    return parameters;
  }

  private static Map<Type, Set<TypedName>> createParametersMap(
      Iterable<? extends TypedName> names) {
    Map<Type, Set<TypedName>> map = new HashMap<>();
    for (Type type : allTypes()) {
      Set<TypedName> set = new HashSet<>();
      for (TypedName name : names) {
        if (name.type().equals(type)) {
          set.add(name);
        }
      }
      map.put(type, set);
    }
    return map;
  }

  public static ImmutableMap<Name, TypedName> createTypedNamesMap(
      Iterable<? extends TypedName> names1,
      Iterable<? extends TypedName> names2) {
    ImmutableMap.Builder<Name, TypedName> builder = ImmutableMap.builder();
    for (TypedName element : names1) {
      builder.put(element.name(), element);
    }
    for (TypedName element : names2) {
      builder.put(element.name(), element);
    }
    return builder.build();
  }
}
