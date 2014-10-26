package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.smoothbuild.lang.function.base.Parameter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MapBuilder {
  private final Map<Parameter, Argument> map;

  public MapBuilder() {
    this.map = Maps.newHashMap();
  }

  public void add(Parameter parameter, Argument argument) {
    checkNotNull(parameter);
    checkNotNull(argument);
    checkState(!map.containsKey(parameter));

    map.put(parameter, argument);
  }

  public ImmutableMap<Parameter, Argument> build() {
    return ImmutableMap.copyOf(map);
  }
}
