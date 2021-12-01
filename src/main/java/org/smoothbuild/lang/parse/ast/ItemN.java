package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.type.impl.TypeS;

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

  @Override
  public Optional<TypeS> inferredType() {
    return type();
  }

  public ItemS toItem(ModPath path) {
    checkState(body().isEmpty());
    return new ItemS(type().get(), path, name(), Optional.empty(), loc());
  }
}
