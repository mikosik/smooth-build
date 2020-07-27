package org.smoothbuild.record.spec;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class StringSpec extends Spec {
  public StringSpec(MerkleRoot merkleRoot, HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, SpecKind.STRING, hashedDb, recordDb);
  }

  @Override
  public RString newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new RString(merkleRoot, hashedDb);
  }
}
