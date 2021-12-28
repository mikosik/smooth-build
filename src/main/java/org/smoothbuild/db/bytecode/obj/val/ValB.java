package org.smoothbuild.db.bytecode.obj.val;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.db.bytecode.type.base.TypeB;

/**
 * Value.
 * This class is thread-safe.
 */
public sealed abstract class ValB extends ObjB
    permits ArrayB, BlobB, BoolB, FuncB, IntB, MethodB, StringB, TupleB {
  public ValB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
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
