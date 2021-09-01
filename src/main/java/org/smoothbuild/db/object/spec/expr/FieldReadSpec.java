package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.FIELD_READ;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.spec.base.ExprSpec;

/**
 * This class is immutable.
 */
public class FieldReadSpec extends ExprSpec {
  public FieldReadSpec(Hash hash, ObjectDb objectDb) {
    super(hash, FIELD_READ, objectDb);
  }

  @Override
  public FieldRead newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new FieldRead(merkleRoot, objectDb());
  }
}
