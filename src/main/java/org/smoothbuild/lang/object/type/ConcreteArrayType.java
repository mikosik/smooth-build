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

  public ConcreteArrayType(MerkleRoot merkleRoot, ConcreteArrayType superType,
      ConcreteType elemType, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, superType, "[" + elemType.name() + "]", Array.class, hashedDb, objectDb);
    this.elemType = checkNotNull(elemType);
  }

  @Override
  public Array newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Array(merkleRoot, objectDb, hashedDb);
  }

  @Override
  public ConcreteType elemType() {
    return elemType;
  }

  @Override
  public ConcreteType coreType() {
    return elemType.coreType();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    @SuppressWarnings("unchecked")
    T result = (T) coreType.changeCoreDepthBy(coreDepth());
    return result;
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public ConcreteType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return super.changeCoreDepthBy(delta);
    }
  }
}
