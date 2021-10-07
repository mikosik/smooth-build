package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.DEFINED_LAMBDA;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.spec.base.ValSpec;

public class DefinedLambdaSpec extends LambdaSpec {
  public DefinedLambdaSpec(Hash hash, ValSpec result, RecSpec parameters, RecSpec defaultArguments) {
    super(hash, DEFINED_LAMBDA, result, parameters, defaultArguments);
  }

  @Override
  public DefinedLambda newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new DefinedLambda(merkleRoot, objectDb);
  }
}
