package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.STRING;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTB extends TypeB {
  public StringTB(Hash hash) {
    super(hash, TypeNames.STRING, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (StringB) super.newObj(merkleRoot, byteDb);
  }
}
