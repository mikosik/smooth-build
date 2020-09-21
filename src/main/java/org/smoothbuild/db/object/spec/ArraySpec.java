package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.spec.SpecKind.ARRAY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class ArraySpec extends Spec {
  private final Spec elemSpec;

  public ArraySpec(Hash hash, Spec elemSpec, HashedDb hashedDb, ObjectDb objectDb) {
    super(hash, ARRAY, hashedDb, objectDb);
    this.elemSpec = requireNonNull(elemSpec);
  }

  @Override
  public Array newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Array(merkleRoot, objectDb, hashedDb);
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
