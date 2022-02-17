package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public final class FuncN extends EvalN {
  private final NList<ItemN> params;

  public FuncN(Optional<TypeN> resT, String name, List<ItemN> params, Optional<ExprN> body,
      Optional<AnnN> ann, Loc loc) {
    this(resT, name, nListWithNonUniqueNames(ImmutableList.copyOf(params)), body, ann, loc);
  }

  public FuncN(Optional<TypeN> resT, String name, NList<ItemN> params, Optional<ExprN> body,
      Optional<AnnN> ann, Loc loc) {
    super(resT, name, body, ann, loc);
    this.params = params;
  }

  public NList<ItemN> params() {
    return params;
  }

  public Optional<ImmutableList<TypeS>> paramTsOpt() {
    return Optionals.pullUp(map(params(), ItemN::type));
  }

  public Optional<TypeS> resT() {
    return type().map(f -> ((FuncTS) f).res());
  }
}
