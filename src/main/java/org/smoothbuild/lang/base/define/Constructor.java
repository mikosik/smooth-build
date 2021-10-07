package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Constructor extends Function {
  public Constructor(FunctionType type, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Location location) {
    super(type, modulePath, name, parameters, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Constructor that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), parameters(), location());
  }
}
