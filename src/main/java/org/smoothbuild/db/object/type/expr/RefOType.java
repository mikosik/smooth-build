package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.base.ValType;

public class RefOType extends ExprType {
  public RefOType(Hash hash, ValType evaluationType) {
    super("REF", hash, REF, evaluationType);
  }

  @Override
  public Ref newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Ref(merkleRoot, objDb);
  }
}
