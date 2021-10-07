package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_LAMBDA;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.spec.base.ValSpec;

public class NativeLambdaSpec extends LambdaSpec {
  public NativeLambdaSpec(Hash hash, ValSpec result, RecSpec parameters, RecSpec defaultArguments) {
    super(hash, NATIVE_LAMBDA, result, parameters, defaultArguments);
  }

  @Override
  public NativeLambda newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new NativeLambda(merkleRoot, objectDb);
  }
}
