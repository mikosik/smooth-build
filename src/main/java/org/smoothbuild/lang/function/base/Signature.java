package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Function's signature.
 */
public class Signature {
  private final Type type;
  private final Name name;
  private final ImmutableList<Parameter> parameters;

  public Signature(Type type, Name name, Iterable<Parameter> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.parameters = ImmutableList.copyOf(params);
  }

  public Type type() {
    return type;
  }

  public Name name() {
    return name;
  }

  public ImmutableList<Parameter> parameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Signature)) {
      return false;
    }
    Signature that = (Signature) object;
    return type.equals(that.type)
        && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(type.toString() + " " + name.toString() + "(");
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
