package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.base.Nothing;

public class NothingType extends ConcreteBasicType {
  public NothingType() {
    super("Nothing", Nothing.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
