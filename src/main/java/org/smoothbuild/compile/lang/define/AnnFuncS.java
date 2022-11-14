package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class AnnFuncS extends NamedFuncS {
  private final AnnS ann;

  public AnnFuncS(AnnS ann, FuncTS type, String name, NList<ItemS> params, Loc loc) {
    super(type, name, params, loc);
    this.ann = ann;
  }

  public AnnS ann() {
    return ann;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnFuncS that
        && this.ann.equals(that.ann)
        && this.resT().equals(that.resT())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann, resT(), name(), params(), loc());
  }

  @Override
  public String toString() {
    var fields = ann.toString() + "\n" + funcFieldsToString();
    return "AnnFuncS(\n" + indent(fields) + "\n)";
  }
}
