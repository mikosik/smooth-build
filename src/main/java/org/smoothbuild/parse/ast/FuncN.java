package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public final class FuncN extends GenericRefableN implements TopRefableN {
  private final Optional<TypeN> resTN;
  private final NList<ItemN> params;
  private Optional<FuncTS> type;

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
  public Optional<TypeN> evalT() {
    return resTN;
  }

  public Optional<ImmutableList<MonoTS>> paramTs() {
    return Optionals.pullUp(map(params(), ItemN::typeO));
  }

  @Override
  public Optional<FuncTS> typeO() {
    return type;
  }

  public void setTypeO(FuncTS type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<FuncTS> type) {
    this.type = type;
  }
}
