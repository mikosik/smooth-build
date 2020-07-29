package org.smoothbuild.db.record.spec;

import static org.smoothbuild.db.record.spec.SpecKind.SPEC;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class SpecSpec extends Spec {
  public SpecSpec(MerkleRoot merkleRoot, HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, SPEC, hashedDb, recordDb);
  }

  @Override
  public Spec spec() {
    return this;
  }

  @Override
  public Spec newJObject(MerkleRoot merkleRoot) {
    return recordDb.getSpec(merkleRoot);
  }
}
