package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Constructor extends Function {
  public Constructor(Type resultType, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Location location) {
    super(resultType, modulePath, name, parameters, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Constructor that
        && this.resultType().equals(that.resultType())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), name(), parameters(), location());
  }
}
