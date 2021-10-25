package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_METHOD;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.NativeMethod;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class NativeMethodSpec extends ValSpec {
  public NativeMethodSpec(Hash hash) {
    super("NATIVE_METHOD", hash, NATIVE_METHOD);
  }

  @Override
  public NativeMethod newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new NativeMethod(merkleRoot, objectDb);
  }
}
