package org.smoothbuild.lang.runtime;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.smoothbuild.lang.type.TypeNames.isGenericTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.GenericArrayType;
import org.smoothbuild.lang.type.GenericType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeNames;

@Singleton
public class RuntimeTypes implements Types {
  private final ValuesDb valuesDb;
  private final Map<String, ConcreteType> cache;

  @Inject
  public RuntimeTypes(ValuesDb valuesDb) {
    this.valuesDb = valuesDb;
    this.cache = createInitializedCache(valuesDb);
  }

  private static HashMap<String, ConcreteType> createInitializedCache(ValuesDb valuesDb) {
    HashMap<String, ConcreteType> map = new HashMap<>();
    putType(map, valuesDb.boolType());
    putType(map, valuesDb.stringType());
    putType(map, valuesDb.blobType());
    putType(map, valuesDb.nothingType());
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
    return valuesDb.boolType();
  }

  @Override
  public ConcreteType string() {
    return valuesDb.stringType();
  }

  @Override
  public ConcreteType blob() {
    return valuesDb.blobType();
  }

  @Override
  public ConcreteType nothing() {
    return valuesDb.nothingType();
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
    return valuesDb.arrayType(elementType);
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
    StructType type = valuesDb.structType(name, fields);
    cache.put(name, type);
    return type;
  }
}
