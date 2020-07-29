package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class BoolSpec extends Spec {
  public BoolSpec(MerkleRoot merkleRoot, HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, SpecKind.BOOL, hashedDb, recordDb);
  }

  @Override
  public Bool newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Bool(merkleRoot, hashedDb);
  }
}
