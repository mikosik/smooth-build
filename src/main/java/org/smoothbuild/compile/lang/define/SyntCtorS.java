package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Synthetic constructor.
 * This class is immutable.
 */
public final class SyntCtorS extends NamedFuncS {
  public SyntCtorS(FuncTS type, String name, NList<ItemS> params, Loc loc) {
    super(type, name, params, loc);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SyntCtorS that
        && this.resT().equals(that.resT())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT(), name(), params(), loc());
  }

  @Override
  public String toString() {
    return "SyntCtorS(\n" + indent(fieldsToString()) + "\n)";
  }
}
