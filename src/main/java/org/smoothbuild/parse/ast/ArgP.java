package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.util.collect.Named;

public sealed abstract class ArgP extends GenericP implements NamedP
    permits DefaultArgP, ExplicitArgP {
  private final Optional<String> name;
  private final Obj obj;

  public ArgP(Optional<String> name, Obj obj, Loc loc) {
    super(loc);
    this.name = name;
    this.obj = obj;
  }

  public boolean declaresName() {
    return nameO().isPresent();
  }

  @Override
  public Optional<String> nameO() {
    return name;
  }

  @Override
  public String name() {
    return name.get();
  }

  public abstract String nameSanitized();

  public String typeAndName() {
    return typeO().map(Named::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public Obj obj() {
    return obj;
  }
}
