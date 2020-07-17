package org.smoothbuild.record.spec;

import static org.smoothbuild.record.spec.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.db.RecordDb;
import org.smoothbuild.record.db.RecordDbException;

/**
 * This class is immutable.
 */
public class NothingSpec extends Spec {
  public NothingSpec(MerkleRoot merkleRoot, HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, NOTHING, hashedDb, recordDb);
  }

  @Override
  public Record newJObject(MerkleRoot merkleRoot) {
    throw new RecordDbException(merkleRoot.hash(), "Cannot create java object for 'NOTHING' spec.");
  }
}
