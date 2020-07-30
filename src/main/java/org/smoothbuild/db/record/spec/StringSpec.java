package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.record.spec.SpecKind.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class StringSpec extends Spec {
  public StringSpec(Hash hash, HashedDb hashedDb, RecordDb recordDb) {
    super(hash, STRING, hashedDb, recordDb);
  }

  @Override
  public RString newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new RString(merkleRoot, hashedDb);
  }
}
