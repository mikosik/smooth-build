package org.smoothbuild.function;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;

public class Param {
  private final Type type;
  private final String name;

  public static Param param(Type type, String name) {
    return new Param(type, name);
  }

  public static ImmutableMap<String, Param> params(Param... params) {
    Set<String> names = Sets.newHashSet();

    Builder<String, Param> builder = ImmutableMap.builder();
    for (Param param : params) {
      String name = param.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException("Duplicate param name = '" + name + "'");
      }
      builder.put(name, param);
      names.add(name);
    }
    return builder.build();
  }

  protected Param(Type type, String name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Param)) {
      return false;
    }
    Param that = (Param) object;
    return this.type.equals(that.type) && this.name.equals(that.name);
  }

  @Override
  public final int hashCode() {
    return 17 * type.hashCode() + name.hashCode();
  }

  @Override
  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
  }
}
