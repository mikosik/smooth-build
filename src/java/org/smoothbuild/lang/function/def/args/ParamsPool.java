package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.STypes.allTypes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.expr.Convert;
import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ParamsPool {
  private final ImmutableMap<String, Param> params;
  private final ImmutableMap<SType<?>, TypedParamsPool> typePools;
  private final Map<SType<?>, Set<Param>> optionalParamsMap;
  private final Map<SType<?>, Set<Param>> requiredParamsMap;

  public ParamsPool(ImmutableMap<String, Param> params) {
    this.params = params;
    this.optionalParamsMap = createParamsMap(params, false);
    this.requiredParamsMap = createParamsMap(params, true);
    this.typePools = createTypePools(optionalParamsMap, requiredParamsMap);
  }

  public Param takeByName(String name) {
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

  public TypedParamsPool availableForType(SType<?> type) {
    return typePools.get(type);
  }

  public Set<Param> availableRequiredParams() {
    Set<Param> result = Sets.newHashSet();
    for (TypedParamsPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParams());
    }
    return result;
  }

  private static ImmutableMap<SType<?>, TypedParamsPool> createTypePools(
      Map<SType<?>, Set<Param>> optionalParamsMap, Map<SType<?>, Set<Param>> requiredParamsMap) {

    Builder<SType<?>, TypedParamsPool> builder = ImmutableMap.builder();
    for (SType<?> type : allTypes()) {
      Set<Param> optional = optionalParamsMap.get(type);
      Set<Param> required = requiredParamsMap.get(type);

      for (SType<?> superType : Convert.superTypesOf(type)) {
        optional = Sets.union(optional, optionalParamsMap.get(superType));
        required = Sets.union(required, requiredParamsMap.get(superType));
      }

      builder.put(type, new TypedParamsPool(optional, required));
    }

    return builder.build();
  }

  private static Map<SType<?>, Set<Param>> createParamsMap(ImmutableMap<String, Param> allParams,
      boolean requiredParams) {
    Map<SType<?>, Set<Param>> map = Maps.newHashMap();
    for (SType<?> type : allTypes()) {
      HashSet<Param> set = Sets.<Param> newHashSet();
      for (Param param : allParams.values()) {
        if (param.isRequired() == requiredParams && param.type() == type) {
          set.add(param);
        }
      }
      map.put(type, set);
    }
    return map;
  }
}
