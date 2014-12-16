package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;

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
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(type.name() + " " + name.value() + "(");
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
