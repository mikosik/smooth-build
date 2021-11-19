package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.NATIVE_METHOD;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public class NativeMethodTypeH extends TypeHV {
  public NativeMethodTypeH(Hash hash) {
    super("NATIVE_METHOD", hash, NATIVE_METHOD);
  }

  @Override
  public NativeMethodH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (NativeMethodH) super.newObj(merkleRoot, objectHDb);
  }
}
