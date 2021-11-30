package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public final class FuncN extends EvalN {
  private final NList<ItemN> params;

  public FuncN(Optional<TypeN> type, String name, List<ItemN> params, Optional<ExprN> body,
      Optional<AnnN> ann, Loc loc) {
    this(type, name, nListWithDuplicates(ImmutableList.copyOf(params)), body, ann, loc);
  }

  public FuncN(Optional<TypeN> type, String name, NList<ItemN> params, Optional<ExprN> body,
      Optional<AnnN> ann, Loc loc) {
    super(type, name, body, ann, loc);
    this.params = params;
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
