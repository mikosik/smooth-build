package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class CtorS extends FuncS {
  public CtorS(FuncTypeS type, ModulePath modulePath, String name, NList<Item> params,
      Loc loc) {
    super(type, modulePath, name, params, loc);
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
    return object instanceof CtorS that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), params(), loc());
  }
}
