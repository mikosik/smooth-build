package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.SELECT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.base.TypeV;

/**
 * This class is immutable.
 */
public class SelectTypeO extends TypeE {
  public SelectTypeO(Hash hash, TypeV evaluationType) {
    super("SELECT", hash, SELECT, evaluationType);
  }

  @Override
  public Select newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Select(merkleRoot, objDb);
  }
}
