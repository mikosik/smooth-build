package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.base.Blob;

public class BlobType extends ConcreteBasicType {
  public BlobType() {
    super("Blob", Blob.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
