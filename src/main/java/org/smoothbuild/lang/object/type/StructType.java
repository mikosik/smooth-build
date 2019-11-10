package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectsDb;

import com.google.common.collect.ImmutableMap;

public class StructType extends ConcreteType {
  private final ImmutableMap<String, Field> fields;
  private final ObjectsDb objectsDb;

  public StructType(Hash dataHash, TypeType type, String name, Iterable<Field> fields,
      HashedDb hashedDb, ObjectsDb objectsDb) {
    this(dataHash, type, name, fieldsMap(fields), hashedDb, objectsDb);
  }

  private static ImmutableMap<String, Field> fieldsMap(Iterable<Field> fields) {
    return stream(fields).collect(toImmutableMap(Field::name, f -> f));
  }

  private StructType(Hash dataHash, TypeType type, String name,
      ImmutableMap<String, Field> fields, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(dataHash, type, calculateSuperType(fields), name, Struct.class, hashedDb, objectsDb);
    this.fields = checkNotNull(fields);
    this.objectsDb = checkNotNull(objectsDb);
  }

  private static ConcreteType calculateSuperType(ImmutableMap<String, Field> fields) {
    if (fields.size() == 0) {
      return null;
    } else {
      ConcreteType superType = fields.values().iterator().next().type();
      if (superType.isArray() || superType.isNothing()) {
        throw new IllegalArgumentException();
      }
      return superType;
    }
  }

  @Override
  public Struct newSObject(Hash dataHash) {
    return new Struct(dataHash, this, objectsDb, hashedDb);
  }

  public ImmutableMap<String, Field> fields() {
    return fields;
  }

  public Accessor accessor(String fieldName) {
    Field field = fields.get(fieldName);
    if (field == null) {
      throw new IllegalArgumentException("Struct " + name() + " doesn't have field " + fieldName);
    }
    return new Accessor(
        new Signature(field.type(), field.name(), list(new Parameter(0, this, "object", null))),
        field.name(),
        field.location());
  }
}
