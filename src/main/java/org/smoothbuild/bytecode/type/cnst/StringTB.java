package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.STRING;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class StringTB extends BaseTB {
  public StringTB(Hash hash) {
    super(hash, TNamesB.STRING, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (StringB) super.newObj(merkleRoot, objDb);
  }
}
