package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.smoothbuild.lang.type.SType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Function's signature.
 */
public class Signature {
  private final SType<?> type;
  private final Name name;
  private final ImmutableMap<String, Param> params;

  public Signature(SType<?> type, Name name, Iterable<Param> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.params = createParamsMap(params);
  }

  public SType<?> type() {
    return type;
  }

  public Name name() {
    return name;
  }

  /**
   * Parameters sorted lexicographically by their names.
   */
  public ImmutableMap<String, Param> params() {
    return params;
  }

  private static ImmutableMap<String, Param> createParamsMap(Iterable<Param> params) {
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
