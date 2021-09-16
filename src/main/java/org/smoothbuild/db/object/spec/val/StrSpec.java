package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class StrSpec extends ValSpec {
  public StrSpec(Hash hash) {
    super(hash, STRING);
  }

  @Override
  public Str newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Str(merkleRoot, objectDb);
  }
}
