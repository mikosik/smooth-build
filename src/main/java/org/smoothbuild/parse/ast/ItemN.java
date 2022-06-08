package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.TypeS;

public final class ItemN extends RefableN {
  private final TypeN typeN;
  private Optional<ItemSigS> sig;

  public ItemN(TypeN typeN, String name, Optional<ObjN> body, Loc loc) {
    super(name, body, Optional.empty(), loc);
    this.typeN = typeN;
  }

  public TypeN typeN() {
    return typeN;
  }

  @Override
  public Optional<TypeN> evalTN() {
    return Optional.of(typeN);
  }

  @Override
  public void setTypeS(Optional<TypeS> type) {
    super.setTypeS(type);
    sig = typeS().map(t -> new ItemSigS(t, Optional.of(name())));
  }

  public Optional<ItemSigS> sig() {
    return sig;
  }

  public ItemS toItemS() {
    checkState(body().isEmpty());
    return new ItemS(typeS().get(), name(), Optional.empty(), loc());
  }
}
