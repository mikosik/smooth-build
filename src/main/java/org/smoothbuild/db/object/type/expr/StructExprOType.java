package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT_EXPR;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.StructExpr;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.val.StructTypeO;

/**
 * This class is immutable.
 */
public class StructExprOType extends TypeE {
  public StructExprOType(Hash hash, StructTypeO evaluationType) {
    super("STRUCT", hash, STRUCT_EXPR, evaluationType);
  }

  @Override
  public StructTypeO evaluationType() {
    return (StructTypeO) super.evaluationType();
  }

  @Override
  public StructExpr newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new StructExpr(merkleRoot, objDb);
  }
}
