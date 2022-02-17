package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.type.impl.TypeS;

public final class ItemN extends EvalN {
  private Optional<ItemSigS> sig;

  public ItemN(TypeN typeN, String name, Optional<ExprN> body, Loc loc) {
    super(Optional.of(typeN), name, body, Optional.empty(), loc);
  }

  @Override
  public void setType(Optional<TypeS> type) {
    super.setType(type);
    sig = type().map(t -> new ItemSigS(t, Optional.of(name()), body().flatMap(Node::type)));
  }

  public Optional<ItemSigS> sig() {
    return sig;
  }

  public ItemS toItem(ModPath path) {
    checkState(body().isEmpty());
    return new ItemS(type().get(), path, name(), Optional.empty(), loc());
  }
}
