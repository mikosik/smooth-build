package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.NATIVE_METHOD;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NativeMethod;
import org.smoothbuild.db.object.type.base.ValType;

/**
 * This class is immutable.
 */
public class NativeMethodOType extends ValType {
  public NativeMethodOType(Hash hash) {
    super("NATIVE_METHOD", hash, NATIVE_METHOD);
  }

  @Override
  public NativeMethod newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new NativeMethod(merkleRoot, objDb);
  }
}
