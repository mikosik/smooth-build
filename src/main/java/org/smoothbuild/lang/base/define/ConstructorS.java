package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class ConstructorS extends FunctionS {
  public ConstructorS(FunctionTypeS type, ModulePath modulePath, String name,
      NList<Item> params, Location location) {
    super(type, modulePath, name, params, location);
    checkArgument(type.result() instanceof StructTypeS);
  }

  @Override
  public StructTypeS resultType() {
    return (StructTypeS) type().result();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ConstructorS that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), params(), location());
  }
}
