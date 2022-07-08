package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.MonoTS;

public final class ItemP extends MonoRefableP {
  private final TypeP type;
  private Optional<ItemSigS> sig;

  public ItemP(TypeP type, String name, Optional<ObjP> body, Loc loc) {
    super(name, body, Optional.empty(), loc);
    this.type = type;
  }

  public TypeP type() {
    return type;
  }

  @Override
  public Optional<TypeP> evalT() {
    return Optional.of(type);
  }

  @Override
  public void setTypeS(Optional<? extends MonoTS> type) {
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
