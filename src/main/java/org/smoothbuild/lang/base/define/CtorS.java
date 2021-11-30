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
  public CtorS(FuncTypeS type, ModPath modPath, String name, NList<Item> params, Loc loc) {
    super(type, modPath, name, params, loc);
    checkArgument(type.res() instanceof StructTypeS);
  }

  @Override
  public StructTypeS resType() {
    return (StructTypeS) type().res();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof CtorS that
        && this.resType().equals(that.resType())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resType(), modPath(), name(), params(), loc());
  }
}
