package org.smoothbuild.parse.def;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ParamsPool {
  private final ImmutableMap<String, Param> params;
  private final ImmutableMap<Type, Set<Param>> typePools;

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

  public Set<Param> availableForType(Type type) {
    if (type == Type.EMPTY_SET) {
      Set<Param> result = Sets.newHashSet();
      result.addAll(typePools.get(Type.STRING_SET));
      result.addAll(typePools.get(Type.FILE_SET));
      return result;
    } else {
      return typePools.get(type);
    }
  }

  private static ImmutableMap<Type, Set<Param>> createTypePools(
      ImmutableMap<String, Param> allParams) {
    ImmutableMap<Type, Set<Param>> result = Helpers.createMap(Type.allowedForParam());
    for (Param param : allParams.values()) {
      result.get(param.type()).add(param);
    }
    return result;
  }
}
