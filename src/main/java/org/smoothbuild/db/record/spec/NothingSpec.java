package org.smoothbuild.db.record.spec;

import static org.smoothbuild.db.record.spec.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.db.record.db.RecordDbException;

/**
 * This class is immutable.
 */
public class NothingSpec extends Spec {
  public NothingSpec(Hash hash, HashedDb hashedDb, RecordDb recordDb) {
    super(hash, NOTHING, hashedDb, recordDb);
  }

  @Override
  public Record newJObject(MerkleRoot merkleRoot) {
    throw new RecordDbException("Cannot create java object for 'NOTHING' spec.");
  }
}
