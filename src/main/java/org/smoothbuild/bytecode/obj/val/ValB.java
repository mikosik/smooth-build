package org.smoothbuild.bytecode.obj.val;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Value.
 * This class is thread-safe.
 */
public sealed abstract class ValB extends ObjB
    permits ArrayB, BlobB, BoolB, FuncB, IntB, MethodB, StringB, TupleB {
  public ValB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
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
