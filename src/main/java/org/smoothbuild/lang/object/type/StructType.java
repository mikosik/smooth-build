package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeKind.TUPLE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class StructType extends BinaryType {
  private final ImmutableList<BinaryType> fieldTypes;

  public StructType(MerkleRoot merkleRoot, Iterable<? extends BinaryType> fieldTypes,
      HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TUPLE, hashedDb, objectDb);
    this.fieldTypes = ImmutableList.copyOf(fieldTypes);
  }

  @Override
  public Struct newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Struct(merkleRoot, objectDb, hashedDb);
  }

  public ImmutableList<BinaryType> fieldTypes() {
    return fieldTypes;
  }
}
