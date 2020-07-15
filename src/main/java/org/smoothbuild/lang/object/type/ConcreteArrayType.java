package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class ConcreteArrayType extends ConcreteType implements ArrayType {
  private final ConcreteType elemType;

  public ConcreteArrayType(MerkleRoot merkleRoot, ConcreteType elemType, HashedDb hashedDb,
      ObjectDb objectDb) {
    super(merkleRoot, "[" + elemType.name() + "]", Array.class, hashedDb, objectDb);
    this.elemType = checkNotNull(elemType);
  }

  @Override
  public Array newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Array(merkleRoot, objectDb, hashedDb);
  }

  @Override
  public ConcreteType elemType() {
    return elemType;
  }
}
