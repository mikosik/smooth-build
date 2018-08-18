package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Function's signature.
 */
public class Signature {
  private final Type type;
  private final String name;
  private final ImmutableList<Parameter> parameters;

  public Signature(Type type, String name, Iterable<Parameter> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.parameters = ImmutableList.copyOf(params);
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  public ImmutableList<Parameter> parameters() {
    return parameters;
  }

  public List<Type> parameterTypes() {
    return map(parameters, p -> p.type());
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
    builder.append(type.name() + " " + name.toString() + "(");
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
