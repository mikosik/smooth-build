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

public final class FuncP extends GenericRefableP implements TopRefableP {
  private final Optional<TypeP> resTP;
  private final NList<ItemP> params;
  private Optional<FuncTS> type;

  public FuncP(Optional<TypeP> resTP, String name, List<ItemP> params, Optional<ObjP> body,
      Optional<AnnP> ann, Loc loc) {
    this(resTP, name, nListWithNonUniqueNames(ImmutableList.copyOf(params)), body, ann, loc);
  }

  public FuncP(Optional<TypeP> resTP, String name, NList<ItemP> params, Optional<ObjP> body,
      Optional<AnnP> ann, Loc loc) {
    super(name, body, ann, loc);
    this.resTP = resTP;
    this.params = params;
  }

  public Optional<TypeP> resTP() {
    return resTP;
  }

  public NList<ItemP> params() {
    return params;
  }

  @Override
  public Optional<TypeP> evalT() {
    return resTP;
  }

  public Optional<ImmutableList<MonoTS>> paramTs() {
    return Optionals.pullUp(map(params(), ItemP::typeO));
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
