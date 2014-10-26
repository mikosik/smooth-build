package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MapBuilder {
  private final Map<Param, Argument> map;

  public MapBuilder() {
    this.map = Maps.newHashMap();
  }

  public void add(Param param, Argument argument) {
    checkNotNull(param);
    checkNotNull(argument);
    checkState(!map.containsKey(param));

    map.put(param, argument);
  }

  public ImmutableMap<Param, Argument> build() {
    return ImmutableMap.copyOf(map);
  }
}
