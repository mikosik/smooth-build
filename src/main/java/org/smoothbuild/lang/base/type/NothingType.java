package org.smoothbuild.lang.base.type;

import org.smoothbuild.record.base.Nothing;

public class NothingType extends ConcreteBasicType {
  public NothingType() {
    super(TypeNames.NOTHING, Nothing.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
