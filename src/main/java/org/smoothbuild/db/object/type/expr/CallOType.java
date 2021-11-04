package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.base.TypeV;

/**
 * This class is immutable.
 */
public class CallOType extends TypeE {
  public CallOType(Hash hash, TypeV evaluationType) {
    super("CALL", hash, CALL, evaluationType);
  }

  @Override
  public Call newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Call(merkleRoot, objDb);
  }
}
