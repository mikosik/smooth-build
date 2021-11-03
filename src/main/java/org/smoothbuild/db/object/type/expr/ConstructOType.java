package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.CONSTRUCT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.val.TupleOType;

/**
 * This class is immutable.
 */
public class ConstructOType extends ExprType {
  public ConstructOType(Hash hash, TupleOType evaluationType) {
    super("CONSTRUCT", hash, CONSTRUCT, evaluationType);
  }

  @Override
  public TupleOType evaluationType() {
    return (TupleOType) super.evaluationType();
  }

  @Override
  public Construct newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Construct(merkleRoot, objectDb);
  }
}
