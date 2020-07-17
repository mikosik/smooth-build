package org.smoothbuild.record.spec;

import static org.smoothbuild.record.spec.SpecKind.SPEC;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.db.RecordDb;

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
