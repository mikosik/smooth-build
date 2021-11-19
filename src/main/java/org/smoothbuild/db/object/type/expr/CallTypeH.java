package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.TypeKindH.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public class CallTypeH extends TypeHE {
  public CallTypeH(Hash hash, TypeHV evaluationType) {
    super("CALL", hash, CALL, evaluationType);
  }

  @Override
  public CallH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (CallH) super.newObj(merkleRoot, objectHDb);
  }
}
