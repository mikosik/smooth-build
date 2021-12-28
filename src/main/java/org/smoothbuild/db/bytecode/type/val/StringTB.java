package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.STRING;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTB extends TypeB {
  public StringTB(Hash hash) {
    super(TypeNames.STRING, hash, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (StringB) super.newObj(merkleRoot, byteDb);
  }
}
