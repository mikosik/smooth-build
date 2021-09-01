package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class ArraySpec extends ValSpec {
  private final ValSpec elemSpec;

  public ArraySpec(Hash hash, ValSpec elemSpec, ObjectDb objectDb) {
    super(hash, ARRAY, objectDb);
    this.elemSpec = requireNonNull(elemSpec);
  }

  @Override
  public Array newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Array(merkleRoot, objectDb());
  }

  @Override
  public String name() {
    return "[" + elemSpec.name() + "]";
  }

  public ValSpec elemSpec() {
    return elemSpec;
  }

  @Override
  public String toString() {
    return  name() + ":" + hash();
  }
}
