package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructType extends ConcreteType {
  private final ImmutableMap<String, Field> fields;

  public StructType(MerkleRoot merkleRoot, String name, Iterable<Field> fields,
      HashedDb hashedDb, ObjectDb objectDb) {
    this(merkleRoot, name, fieldsMap(fields), hashedDb, objectDb);
  }

  private static ImmutableMap<String, Field> fieldsMap(Iterable<Field> fields) {
    return stream(fields).collect(toImmutableMap(Field::name, f -> f));
  }

  private StructType(MerkleRoot merkleRoot, String name, ImmutableMap<String, Field> fields,
      HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, name, Struct.class, hashedDb, objectDb);
    this.fields = checkNotNull(fields);
  }

  @Override
  public Struct newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Struct(merkleRoot, objectDb, hashedDb);
  }

  public ImmutableMap<String, Field> fields() {
    return fields;
  }
}
