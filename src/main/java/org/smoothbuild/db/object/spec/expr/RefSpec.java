package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.spec.base.ExprSpec;

public class RefSpec extends ExprSpec {
  public RefSpec(Hash hash, ObjectDb objectDb) {
    super(hash, REF, objectDb);
  }

  @Override
  public Ref newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Ref(merkleRoot, objectDb());
  }
}
