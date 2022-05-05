package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.STRING;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class StringTB extends BaseTB {
  public StringTB(Hash hash) {
    super(hash, TypeNamesB.STRING, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (StringB) super.newObj(merkleRoot, objDb);
  }
}
