package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Function's signature.
 */
public class Signature extends TypedName {
  private final ImmutableList<Parameter> parameters;

  public Signature(Type type, Name name, Iterable<Parameter> params) {
    super(type, name);
    this.parameters = ImmutableList.copyOf(params);
  }

  public ImmutableList<Parameter> parameters() {
    return parameters;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(super.toString() + "(");
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
