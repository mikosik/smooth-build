package org.smoothbuild.lang.base.type;

import org.smoothbuild.record.base.SString;

public class StringType extends ConcreteBasicType {
  public StringType() {
    super("String", SString.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
