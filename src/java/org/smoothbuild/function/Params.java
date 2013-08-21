package org.smoothbuild.function;

import static org.smoothbuild.function.Param.PARAM_TO_NAME;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Ordering;

public class Params {
  private final ImmutableList<Param> list;
  private final ImmutableMap<String, Param> map;

  public static Params params(Param... params) {
    return new Params(params);
  }

  public Params(Param... params) {
    this.list = ImmutableList.copyOf(params);
    this.map = createMap(list);
  }

  public Param param(String name) {
    Param result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Params doesn't contain " + name
          + ". Available param names sorted alphabetically = {"
          + Joiner.on(", ").join(sortedParamNames()) + "}.");
    }
    return result;
  }

  private static ImmutableMap<String, Param> createMap(ImmutableList<Param> list) {
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Param param : list) {
      builder.put(param.name(), param);
    }
    return builder.build();
  }

  private ImmutableList<String> sortedParamNames() {
    Iterable<String> names = FluentIterable.from(list).transform(PARAM_TO_NAME);
    return Ordering.from(String.CASE_INSENSITIVE_ORDER).immutableSortedCopy(names);
  }

  @Override
  public String toString() {
    return "Params(" + Joiner.on(", ").join(list) + ")";
  }
}
