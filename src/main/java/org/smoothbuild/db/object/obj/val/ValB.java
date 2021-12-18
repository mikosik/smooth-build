package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.type.base.TypeB;

/**
 * Value.
 * This class is thread-safe.
 */
public sealed abstract class ValB extends ObjB
    permits ArrayB, BlobB, BoolB, FuncB, IntB, MethodB, StringB, TupleB {
  public ValB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  @Override
  public TypeB cat() {
    return (TypeB) super.cat();
  }

  @Override
  public TypeB type() {
    return cat();
  }
}
