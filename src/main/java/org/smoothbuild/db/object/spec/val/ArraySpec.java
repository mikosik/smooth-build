package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;

/**
 * This class is immutable.
 */
public class ArraySpec extends ValSpec implements ArrayType {
  private final ValSpec elemType;

  public ArraySpec(Hash hash, ValSpec elemType) {
    super(hash, ARRAY);
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public Type elemType() {
    return elemType;
  }

  @Override
  public Array newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Array(merkleRoot, objectDb);
  }

  @Override
  public String name() {
    return "[" + elemType.name() + "]";
  }

  public ValSpec element() {
    return elemType;
  }
}
