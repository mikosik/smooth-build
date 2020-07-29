package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.db.record.spec.SpecKind.ARRAY;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class ArraySpec extends Spec {
  private final Spec elemSpec;

  public ArraySpec(MerkleRoot merkleRoot, Spec elemSpec, HashedDb hashedDb,
      RecordDb recordDb) {
    super(merkleRoot, ARRAY, hashedDb, recordDb);
    this.elemSpec = checkNotNull(elemSpec);
  }

  @Override
  public Array newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Array(merkleRoot, recordDb, hashedDb);
  }

  @Override
  public String name() {
    return "[" + elemSpec.name() + "]";
  }

  public Spec elemSpec() {
    return elemSpec;
  }

  @Override
  public String toString() {
    return  name() + ":" + hash();
  }
}
