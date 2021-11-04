package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.CONSTRUCT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.val.TupleTypeO;

/**
 * This class is immutable.
 */
public class ConstructOType extends TypeE {
  public ConstructOType(Hash hash, TupleTypeO evaluationType) {
    super("CONSTRUCT", hash, CONSTRUCT, evaluationType);
  }

  @Override
  public TupleTypeO evaluationType() {
    return (TupleTypeO) super.evaluationType();
  }

  @Override
  public Construct newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Construct(merkleRoot, objDb);
  }
}
