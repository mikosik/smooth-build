package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ParamToArgMapBuilder {
  private final Map<Param, Argument> map;

  public ParamToArgMapBuilder() {
    this.map = Maps.newHashMap();
  }

  public void add(Param param, Argument arg) {
    checkNotNull(param);
    checkNotNull(arg);
    checkState(!map.containsKey(param));

    map.put(param, arg);
  }

  public ImmutableMap<Param, Argument> build() {
    return ImmutableMap.copyOf(map);
  }
}
