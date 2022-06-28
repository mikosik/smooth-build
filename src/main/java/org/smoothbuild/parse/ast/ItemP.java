package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.MonoTS;

public final class ItemP extends MonoRefableP {
  private final TypeP typeP;
  private Optional<ItemSigS> sig;

  public ItemP(TypeP typeP, String name, Optional<ObjP> body, Loc loc) {
    super(name, body, Optional.empty(), loc);
    this.typeP = typeP;
  }

  public TypeP typeN() {
    return typeP;
  }

  @Override
  public Optional<TypeP> evalT() {
    return Optional.of(typeP);
  }

  @Override
  public void setTypeO(Optional<MonoTS> type) {
    super.setTypeO(type);
    sig = typeO().map(t -> new ItemSigS(t, Optional.of(name())));
  }

  public Optional<ItemSigS> sig() {
    return sig;
  }

  public ItemS toItemS() {
    checkState(body().isEmpty());
    return new ItemS(typeO().get(), name(), Optional.empty(), loc());
  }
}
