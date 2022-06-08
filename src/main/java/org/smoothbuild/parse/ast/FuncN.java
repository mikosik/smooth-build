package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public final class FuncN extends RefableObjN {
  private final Optional<TypeN> resTN;
  private final NList<ItemN> params;

  public FuncN(Optional<TypeN> resTN, String name, List<ItemN> params, Optional<ObjN> body,
      Optional<AnnN> ann, Loc loc) {
    this(resTN, name, nListWithNonUniqueNames(ImmutableList.copyOf(params)), body, ann, loc);
  }

  public FuncN(Optional<TypeN> resTN, String name, NList<ItemN> params, Optional<ObjN> body,
      Optional<AnnN> ann, Loc loc) {
    super(name, body, ann, loc);
    this.resTN = resTN;
    this.params = params;
  }

  public Optional<TypeN> resTN() {
    return resTN;
  }

  public NList<ItemN> params() {
    return params;
  }

  @Override
  public Optional<TypeN> evalTN() {
    return resTN;
  }

  public Optional<ImmutableList<TypeS>> paramTSs() {
    return Optionals.pullUp(map(params(), ItemN::typeS));
  }

  public Optional<TypeS> resTS() {
    return typeS().map(f -> ((FuncTS) f).res());
  }
}
