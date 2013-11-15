package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.function.base.Type.EMPTY_SET;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ParamsPool {
  private final ImmutableMap<String, Param> params;
  private final ImmutableMap<Type, TypedParamsPool> typePools;

  public ParamsPool(ImmutableMap<String, Param> params) {
    this.params = params;
    this.typePools = createTypePools(params);
  }

  public Param takeByName(String name) {
    Param param = params.get(name);
    checkArgument(param != null);
    return take(param);
  }

  public Param take(Param param) {
    boolean hasBeenRemoved = typePools.get(param.type()).remove(param);
    checkArgument(hasBeenRemoved);
    return param;
  }

  public TypedParamsPool availableForType(Type type) {
    return typePools.get(type);
  }

  public Set<Param> availableRequiredParams() {
    Set<Param> result = Sets.newHashSet();
    for (TypedParamsPool typedParamPool : typePools.values()) {
      Iterables.addAll(result, typedParamPool.requiredParams());
    }
    return result;
  }

  private static ImmutableMap<Type, TypedParamsPool> createTypePools(
      ImmutableMap<String, Param> allParams) {
    ImmutableMap<Type, TypedParamsPool> result = createMap();
    for (Param param : allParams.values()) {
      result.get(param.type()).add(param);
    }
    return result;
  }

  public static <T> ImmutableMap<Type, TypedParamsPool> createMap() {
    Map<Type, TypedParamsPool> builder = Maps.newHashMap();
    for (Type type : Type.allowedForParam()) {
      builder.put(type, new TypedParamsPool());
    }
    builder.put(EMPTY_SET, new TypedParamsPool(builder.get(FILE_SET), builder.get(STRING_SET)));

    return ImmutableMap.copyOf(builder);
  }
}
