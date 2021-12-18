package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTB extends TypeB {
  public StringTB(Hash hash) {
    super(TypeNames.STRING, hash, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (StringB) super.newObj(merkleRoot, byteDb);
  }
}
