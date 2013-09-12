package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.function.base.Type.allowedForParam;

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
    this.type = checkAllowedType(type);
    this.name = checkNotNull(name);
  }

  private Type checkAllowedType(Type type) {
    checkNotNull(type);
    checkArgument(allowedForParam().contains(type));
    return type;
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

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type.name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name, minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
  }
}
