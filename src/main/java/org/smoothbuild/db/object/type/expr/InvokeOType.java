package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.INVOKE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Invoke;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.base.ValType;

/**
 * Invoke represents call to native java method.
 * This class is immutable.
 */
public class InvokeOType extends ExprType {
  public InvokeOType(Hash hash, ValType evaluationType) {
    super("INVOKE", hash, INVOKE, evaluationType);
  }

  @Override
  public Invoke newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Invoke(merkleRoot, objDb);
  }
}
