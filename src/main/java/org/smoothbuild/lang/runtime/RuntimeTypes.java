package org.smoothbuild.lang.runtime;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.GenericArrayType;
import org.smoothbuild.lang.object.type.GenericType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.object.type.TypeNames;
import org.smoothbuild.lang.plugin.Types;

@Singleton
public class RuntimeTypes implements Types {
  private final ObjectsDb objectsDb;
  private final Map<String, ConcreteType> cache;

  @Inject
  public RuntimeTypes(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
    this.cache = createInitializedCache(objectsDb);
  }

  private static HashMap<String, ConcreteType> createInitializedCache(ObjectsDb objectsDb) {
    HashMap<String, ConcreteType> map = new HashMap<>();
    putType(map, objectsDb.boolType());
    putType(map, objectsDb.stringType());
    putType(map, objectsDb.blobType());
    putType(map, objectsDb.nothingType());
    return map;
  }

  private static void putType(HashMap<String, ConcreteType> map, ConcreteType type) {
    map.put(type.name(), type);
  }

  public Set<String> names() {
    return unmodifiableSet(cache.keySet());
  }

  public Map<String, ConcreteType> nameToTypeMap() {
    return unmodifiableMap(cache);
  }

  @Override
  public ConcreteType bool() {
    return objectsDb.boolType();
  }

  @Override
  public ConcreteType string() {
    return objectsDb.stringType();
  }

  @Override
  public ConcreteType blob() {
    return objectsDb.blobType();
  }

  @Override
  public ConcreteType nothing() {
    return objectsDb.nothingType();
  }

  @Override
  public StructType file() {
    return (StructType) getType(TypeNames.FILE);
  }

  @Override
  public StructType message() {
    return (StructType) getType(TypeNames.MESSAGE);
  }

  @Override
  public ArrayType array(Type elementType) {
    if (elementType.isGeneric()) {
      return array((GenericType) elementType);
    } else {
      return array((ConcreteType) elementType);
    }
  }

  public ConcreteArrayType array(ConcreteType elementType) {
    return objectsDb.arrayType(elementType);
  }

  public GenericArrayType array(GenericType elementType) {
    return new GenericArrayType(elementType);
  }

  @Override
  public Type getType(String name) {
    if (isGenericTypeName(name)) {
      return new GenericType(name);
    } else {
      ConcreteType type = cache.get(name);
      if (type == null) {
        throw new IllegalStateException("Unknown runtime type '" + name + "'.");
      }
      return type;
    }
  }

  public StructType struct(String name, Iterable<Field> fields) {
    if (cache.containsKey(name)) {
      throw new IllegalStateException("Type '" + name + "' is already added to runtime types.");
    }
    StructType type = objectsDb.structType(name, fields);
    cache.put(name, type);
    return type;
  }
}
