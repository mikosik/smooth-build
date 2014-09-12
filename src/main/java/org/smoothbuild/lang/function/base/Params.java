package org.smoothbuild.lang.function.base;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class Params {

  public static ImmutableList<Param> filterRequiredParams(ImmutableList<Param> params) {
    return filterParams(params, true);
  }

  public static ImmutableList<Param> filterOptionalParams(ImmutableList<Param> params) {
    return filterParams(params, false);
  }

  private static ImmutableList<Param> filterParams(ImmutableList<Param> params,
      boolean isRequired) {
    ImmutableList.Builder<Param> builder = ImmutableList.builder();
    for (Param param : params) {
      if (param.isRequired() == isRequired) {
        builder.add(param);
      }
    }
    return builder.build();
  }

  public static ImmutableList<String> paramsToNames(Iterable<Param> params) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (Param param : params) {
      builder.add(param.name());
    }
    return builder.build();
  }

  public static ImmutableMap<String, Param> map(Param... params) {
    return map(Arrays.asList(params));
  }

  /**
   * @return Parameters ordered lexicographically by their names. Methods
   * values(), keySet(), entrySet() of returned map returns collections
   * which elements keep that order.
   */
  public static ImmutableMap<String, Param> map(Iterable<Param> params) {
    Set<String> names = Sets.newHashSet();

    /*
     * ImmutableMap keeps order of elements.
     */
    ImmutableMap.Builder<String, Param> builder = ImmutableMap.builder();

    for (Param param : ParamOrdering.PARAM_ORDERING.sortedCopy(params)) {
      String name = param.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException("Duplicate param name = '" + name + "'");
      }
      builder.put(name, param);
      names.add(name);
    }
    return builder.build();
  }
}
