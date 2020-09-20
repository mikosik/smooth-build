package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record Signature(Type type, String name, ImmutableList<Parameter> parameters) {
  public static Signature signature(
      Type type, String name, Iterable<? extends Parameter> parameters) {
    return new Signature(type, name, ImmutableList.copyOf(parameters));
  }

  public Signature {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.parameters = requireNonNull(parameters);
  }

  public List<Type> parameterTypes() {
    return map(parameters, ItemSignature::type);
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Signature that)) {
      return false;
    }
    return this.type.equals(that.type)
        && this.name.equals(that.name)
        && this.parameters.equals(that.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, parameters);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(type.name());
    builder.append(" ");
    builder.append(name);
    builder.append("(");
    int count = 0;
    for (Parameter parameter : parameters) {
      if (count != 0) {
        builder.append(", ");
      }
      count++;
      builder.append(parameter.type().name());
      builder.append(" ");
      builder.append(parameter.name());
    }

    builder.append(")");
    return builder.toString();
  }
}
