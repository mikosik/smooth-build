package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.CONST;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.base.TypeV;

/**
 * This class is immutable.
 */
public class ConstOType extends TypeE {
  public ConstOType(Hash hash, TypeV evaluationType) {
    super("CONST", hash, CONST, evaluationType);
  }

  @Override
  public Const newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Const(merkleRoot, objDb);
  }
}
