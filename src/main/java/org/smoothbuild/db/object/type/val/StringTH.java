package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTH extends TypeH {
  public StringTH(Hash hash) {
    super(TypeNames.STRING, hash, STRING);
  }

  @Override
  public StringH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (StringH) super.newObj(merkleRoot, objDb);
  }
}
