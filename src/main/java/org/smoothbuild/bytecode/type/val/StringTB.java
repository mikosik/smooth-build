package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTB extends TypeB {
  public StringTB(Hash hash) {
    super(TypeNames.STRING, hash, CatKindB.STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (StringB) super.newObj(merkleRoot, byteDb);
  }
}
