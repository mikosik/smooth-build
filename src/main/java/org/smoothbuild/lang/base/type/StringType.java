package org.smoothbuild.lang.base.type;

import org.smoothbuild.record.base.SString;

public class StringType extends ConcreteBasicType {
  public StringType() {
    super(TypeNames.STRING, SString.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
