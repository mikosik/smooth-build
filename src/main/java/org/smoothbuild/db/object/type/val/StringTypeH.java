package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTypeH extends TypeHV {
  public StringTypeH(Hash hash) {
    super(TypeNames.STRING, hash, STRING);
  }

  @Override
  public StringH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (StringH) super.newObj(merkleRoot, objectHDb);
  }
}
