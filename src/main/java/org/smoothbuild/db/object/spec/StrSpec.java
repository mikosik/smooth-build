package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class StrSpec extends ValSpec {
  public StrSpec(Hash hash, ObjectDb objectDb) {
    super(hash, STRING, objectDb);
  }

  @Override
  public Str newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Str(merkleRoot, objectDb());
  }
}
