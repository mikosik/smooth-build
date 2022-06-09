package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.util.collect.Named;

public sealed abstract class ArgN extends GenericAstNode implements NamedN
permits DefaultArgN, ExplicitArgN {
  private final String name;
  private final Obj obj;

  public ArgN(String name, Obj obj, Loc loc) {
    super(loc);
    this.name = name;
    this.obj = obj;
  }

  public boolean declaresName() {
    return name != null;
  }

  @Override
  public String name() {
    checkState(declaresName());
    return name;
  }

  public abstract String nameSanitized();

  public String typeAndName() {
    return typeO().map(Named::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public Obj obj() {
    return obj;
  }
}
