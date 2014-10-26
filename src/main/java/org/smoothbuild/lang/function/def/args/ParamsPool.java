package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.Conversions.canConvert;
import static org.smoothbuild.lang.base.Types.allTypes;
import static org.smoothbuild.lang.function.base.Params.filterOptionalParams;
import static org.smoothbuild.lang.function.base.Params.filterRequiredParams;
import static org.smoothbuild.lang.function.base.Params.paramsToMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ParamsPool {
  private final ImmutableMap<String, Param> params;
  private final ImmutableMap<Type<?>, TypedParamsPool> typePools;
  private final Map<Type<?>, Set<Param>> optionalParamsMap;
  private final Map<Type<?>, Set<Param>> requiredParamsMap;

  public ParamsPool(ImmutableList<Param> params) {
    this.params = paramsToMap(params);
    this.optionalParamsMap = createParamsMap(filterOptionalParams(params));
    this.requiredParamsMap = createParamsMap(filterRequiredParams(params));
    this.typePools = createTypePools(optionalParamsMap, requiredParamsMap);
  }

  public Param take(String name) {
    Param param = params.get(name);
    checkArgument(param != null);
    return take(param);
  }

  public Param take(Param param) {
    boolean hasBeenRemoved = remove(param);
    checkArgument(hasBeenRemoved);
    return param;
  }

  private boolean remove(Param param) {
    if (param.isRequired()) {
      return requiredParamsMap.get(param.type()).remove(param);
    } else {
      return optionalParamsMap.get(param.type()).remove(param);
    }
  }

  public TypedParamsPool assignableFrom(Type<?> type) {
    return typePools.get(type);
  }

  public Set<Param> allRequired() {
    Set<Param> result = Sets.newHashSet();
    for (TypedParamsPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParams());
    }
    return result;
  }

  private static ImmutableMap<Type<?>, TypedParamsPool> createTypePools(
      Map<Type<?>, Set<Param>> optionalParamsMap, Map<Type<?>, Set<Param>> requiredParamsMap) {

    Builder<Type<?>, TypedParamsPool> builder = ImmutableMap.builder();
    for (Type<?> type : allTypes()) {
      Set<Param> optional = paramsAssignableFromType(type, optionalParamsMap);
      Set<Param> required = paramsAssignableFromType(type, requiredParamsMap);
      builder.put(type, new TypedParamsPool(optional, required));
    }

    return builder.build();
  }

  private static Set<Param> paramsAssignableFromType(Type<?> type,
      Map<Type<?>, Set<Param>> paramsMap) {
    Set<Param> params = paramsMap.get(type);
    for (Type<?> currentType : allTypes()) {
      if (canConvert(type, currentType)) {
        params = Sets.union(params, paramsMap.get(currentType));
      }
    }
    return params;
  }

  private static Map<Type<?>, Set<Param>> createParamsMap(ImmutableList<Param> params) {
    Map<Type<?>, Set<Param>> map = Maps.newHashMap();
    for (Type<?> type : allTypes()) {
      HashSet<Param> set = Sets.newHashSet();
      for (Param param : params) {
        if (param.type() == type) {
          set.add(param);
        }
      }
      map.put(type, set);
    }
    return map;
  }
}
