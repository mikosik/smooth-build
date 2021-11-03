package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.CONST;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.base.ValType;

/**
 * This class is immutable.
 */
public class ConstOType extends ExprType {
  public ConstOType(Hash hash, ValType evaluationType) {
    super("CONST", hash, CONST, evaluationType);
  }

  @Override
  public Const newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Const(merkleRoot, objectDb);
  }
}
