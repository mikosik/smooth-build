package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableMap;

/**
 * Function's signature.
 */
public class Signature<T extends SValue> {
  private final SType<T> type;
  private final Name name;
  private final ImmutableMap<String, Param> params;

  public Signature(SType<T> type, Name name, Iterable<Param> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.params = Params.map(params);
  }

  public SType<T> type() {
    return type;
  }

  public Name name() {
    return name;
  }

  /**
   * @return Parameters ordered lexicographically by their names. Methods
   *         values(), keySet(), entrySet() of returned map returns collections
   *         which elements keep that order.
   */
  public ImmutableMap<String, Param> params() {
    return params;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(type.name() + " " + name.value() + "(");
    int count = 0;
    for (Param param : params.values()) {
      if (count != 0) {
        builder.append(", ");
      }
      count++;
      builder.append(param.type().name());
      builder.append(" ");
      builder.append(param.name());
    }

    builder.append(")");
    return builder.toString();
  }
}
