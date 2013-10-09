package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.function.base.Type.allowedForParam;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;

public class Param {
  private final Type type;
  private final String name;
  private final boolean isRequired;
  private final HashCode hash;

  public static Param param(Type type, String name, boolean isRequired, HashCode hash) {
    return new Param(type, name, isRequired, hash);
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

  protected Param(Type type, String name, boolean isRequired, HashCode hash) {
    this.type = checkAllowedType(type);
    this.name = checkNotNull(name);
    this.isRequired = isRequired;
    this.hash = checkNotNull(hash);
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

  public boolean isRequired() {
    return isRequired;
  }

  public HashCode hash() {
    return hash;
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

  public static String paramsToString(Set<Param> params) {
    int typeLength = longestParamType(params);
    int nameLength = longestParamName(params);

    StringBuilder builder = new StringBuilder();
    for (Param param : params) {
      builder.append("  " + param.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
  }

  private static int longestParamType(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.type().name().length());
    }
    return result;
  }

  private static int longestParamName(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.name().length());
    }
    return result;
  }
}
