package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeKind.TUPLE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Tuple;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleType extends BinaryType {
  private final ImmutableList<BinaryType> elementTypes;

  public TupleType(MerkleRoot merkleRoot, Iterable<? extends BinaryType> elementTypes,
      HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TUPLE, hashedDb, objectDb);
    this.elementTypes = ImmutableList.copyOf(elementTypes);
  }

  @Override
  public Tuple newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Tuple(merkleRoot, objectDb, hashedDb);
  }

  public ImmutableList<BinaryType> elementTypes() {
    return elementTypes;
  }
}
