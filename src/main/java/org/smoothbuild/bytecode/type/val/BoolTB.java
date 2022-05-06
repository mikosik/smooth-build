package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.BOOL;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class BoolTB extends BaseTB {
  public BoolTB(Hash hash) {
    super(hash, TypeNamesB.BOOL, BOOL);
  }

  @Override
  public BoolB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (BoolB) super.newObj(merkleRoot, objDb);
  }
}
