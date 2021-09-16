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
  private final ValSpec elements;

  public ArraySpec(Hash hash, ValSpec elements) {
    super(hash, ARRAY);
    this.elements = requireNonNull(elements);
  }

  @Override
  public Array newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Array(merkleRoot, objectDb);
  }

  @Override
  public String name() {
    return "[" + elements.name() + "]";
  }

  public ValSpec element() {
    return elements;
  }

  @Override
  public String toString() {
    return  name() + ":" + hash();
  }
}
