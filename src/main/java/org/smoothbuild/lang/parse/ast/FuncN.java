package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.parse.ast.StructN.CtorN;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public sealed class FuncN extends EvalN permits RealFuncN, CtorN {
  private final NList<ItemN> params;

  public FuncN(Optional<TypeN> typeNode, String name, Optional<ExprN> body,
      List<ItemN> params, Optional<AnnN> annotation, Loc loc) {
    super(typeNode, name, body, annotation, loc);
    this.params = nListWithDuplicates(ImmutableList.copyOf(params));
  }

  public NList<ItemN> params() {
    return params;
  }

  public Optional<ImmutableList<TypeS>> optParamTypes() {
    return Optionals.pullUp(map(params(), ItemN::type));
  }

  public Optional<TypeS> resType() {
    return type().map(f -> ((FuncTypeS) f).res());
  }
}
