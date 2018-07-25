package org.smoothbuild.parse.arg;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Sets.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class ParametersPool {
  private final Set<ConcreteType> types;
  private final Map<ConcreteType, Set<ParameterInfo>> optionalParametersMap;
  private final Map<ConcreteType, Set<ParameterInfo>> requiredParametersMap;

  public ParametersPool(Set<? extends ParameterInfo> optional,
      Set<? extends ParameterInfo> required) {
    this.types = createAllTypes(optional, required);
    this.optionalParametersMap = createParametersMap(types, optional);
    this.requiredParametersMap = createParametersMap(types, required);
  }

  public ParameterInfo take(ParameterInfo parameter) {
    boolean hasBeenRemoved = remove(parameter);
    checkArgument(hasBeenRemoved);
    return parameter;
  }

  private boolean remove(ParameterInfo parameter) {
    ConcreteType type = (ConcreteType) parameter.type();
    if (!types.contains(type)) {
      throw new IllegalArgumentException("unknown parameter '" + parameter.toString() + "'");
    }
    if (requiredParametersMap.get(type).contains(parameter)) {
      return requiredParametersMap.get(type).remove(parameter);
    } else {
      return optionalParametersMap.get(type).remove(parameter);
    }
  }

  public TypedParametersPool assignableFrom(ConcreteType type) {
    Set<ParameterInfo> optional = assignableFrom(type, types, optionalParametersMap);
    Set<ParameterInfo> required = assignableFrom(type, types, requiredParametersMap);
    return new TypedParametersPool(optional, required);
  }

  public Set<ParameterInfo> allRequired() {
    return allSetsValues(requiredParametersMap);
  }

  public Set<ParameterInfo> allOptional() {
    return allSetsValues(optionalParametersMap);
  }

  private static Set<ParameterInfo> allSetsValues(Map<ConcreteType, Set<ParameterInfo>> map) {
    Set<ParameterInfo> result = new HashSet<>();
    for (Set<ParameterInfo> values : map.values()) {
      Iterables.addAll(result, values);
    }
    return result;
  }

  private static Set<ConcreteType> createAllTypes(Set<? extends ParameterInfo> optional,
      Set<? extends ParameterInfo> required) {
    return ImmutableSet.<ConcreteType> builder()
        .addAll(map(optional, p -> (ConcreteType) p.type()))
        .addAll(map(required, p -> (ConcreteType) p.type()))
        .build();
  }

  private static Set<ParameterInfo> assignableFrom(ConcreteType type, Set<ConcreteType> types,
      Map<ConcreteType, Set<ParameterInfo>> paramsMap) {
    HashSet<ParameterInfo> result = new HashSet<>();
    for (ConcreteType currentType : types) {
      if (currentType.isAssignableFrom(type)) {
        result.addAll(paramsMap.get(currentType));
      }
    }
    return result;
  }

  private static Map<ConcreteType, Set<ParameterInfo>> createParametersMap(
      Set<ConcreteType> types, Iterable<? extends ParameterInfo> names) {
    Map<ConcreteType, Set<ParameterInfo>> map = new HashMap<>();
    for (ConcreteType type : types) {
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
