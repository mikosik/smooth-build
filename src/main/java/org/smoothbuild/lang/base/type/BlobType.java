package org.smoothbuild.lang.base.type;

import org.smoothbuild.record.base.Blob;

public class BlobType extends ConcreteBasicType {
  public BlobType() {
    super(TypeNames.BLOB, Blob.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
