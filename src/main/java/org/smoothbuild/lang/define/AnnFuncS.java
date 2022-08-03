package org.smoothbuild.lang.define;

import static org.smoothbuild.lang.define.ItemS.mapParams;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.collect.NList;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class AnnFuncS extends FuncS {
  private final AnnS ann;

  public AnnFuncS(AnnS ann, FuncTS type, ModPath modPath, String name, NList<ItemS> params,
      Loc loc) {
    super(type, modPath, name, params, loc);
    this.ann = ann;
  }

  public AnnS ann() {
    return ann;
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new AnnFuncS(
        ann, type().mapVars(mapper), modPath(), name(), mapParams(params(), mapper), loc());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnFuncS that
        && this.ann.equals(that.ann)
        && this.resT().equals(that.resT())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann, resT(), modPath(), name(), params(), loc());
  }

  @Override
  public String toString() {
    return ann + " " + signature();
  }
}
