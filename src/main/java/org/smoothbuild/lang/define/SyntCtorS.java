package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Synthetic constructor.
 *
 * This class is immutable.
 */
public final class SyntCtorS extends MonoFuncS {

  public SyntCtorS(MonoFuncTS type, ModPath modPath, String name, NList<ItemS> params, Loc loc) {
    super(type, modPath, name, params, loc);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SyntCtorS that
        && this.resT().equals(that.resT())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT(), modPath(), name(), params(), loc());
  }

  @Override
  public String toString() {
    return "SyntCtor(`" + signature() + "`)";
  }
}
