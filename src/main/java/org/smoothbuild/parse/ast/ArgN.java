package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class ArgN extends MonoNamedN permits DefaultArgN, ExplicitArgN {
  private final Obj obj;

  public ArgN(String name, Obj obj, Loc loc) {
    super(name, loc);
    this.obj = obj;
  }

  public boolean declaresName() {
    return super.name() != null;
  }

  @Override
  public String name() {
    checkState(declaresName());
    return super.name();
  }

  public abstract String nameSanitized();

  public String typeAndName() {
    return typeS().map(TypeS::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public Obj obj() {
    return obj;
  }
}
