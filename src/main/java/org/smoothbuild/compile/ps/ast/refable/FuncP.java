package org.smoothbuild.compile.ps.ast.refable;

import static org.smoothbuild.compile.ps.ast.refable.ItemP.toTypeS;

import java.util.Optional;

import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public interface FuncP extends EvaluableP {
  public Optional<TypeP> resT();

  public NList<ItemP> params();

  public default ImmutableList<TypeS> paramTs() {
    return toTypeS(params());
  }

  @Override
  public FuncTS typeS();

  public void setTypeS(FuncTS funcTS);
}
