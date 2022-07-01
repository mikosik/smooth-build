package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.collect.Named;

public final class ArgP extends GenericP implements NamedP {
  private final Optional<String> name;
  private final ObjP objP;

  public ArgP(Optional<String> name, ObjP objP, Loc loc) {
    super(loc);
    this.name = name;
    this.objP = objP;
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

  public String nameSanitized() {
    return declaresName() ? name() : "<nameless>";
  }

  public String typeAndName() {
    return typeS().map(Named::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public ObjP obj() {
    return objP;
  }
}
