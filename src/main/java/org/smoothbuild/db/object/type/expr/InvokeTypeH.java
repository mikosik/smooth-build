package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.INVOKE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * Invoke represents call to native java method.
 * This class is immutable.
 */
public class InvokeTypeH extends TypeHE {
  public InvokeTypeH(Hash hash, TypeHV evaluationType) {
    super("INVOKE", hash, INVOKE, evaluationType);
  }

  @Override
  public InvokeH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new InvokeH(merkleRoot, objectHDb);
  }
}
