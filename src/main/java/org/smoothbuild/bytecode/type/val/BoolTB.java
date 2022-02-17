package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.BOOL;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTB extends TypeB {
  public BoolTB(Hash hash) {
    super(hash, TypeNames.BOOL, BOOL);
  }

  @Override
  public BoolB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (BoolB) super.newObj(merkleRoot, objDb);
  }
}
