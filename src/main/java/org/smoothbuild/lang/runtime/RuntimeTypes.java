package org.smoothbuild.lang.runtime;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.smoothbuild.lang.type.TypeNames.isGenericTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.smoothbuild.lang.type.TypesDb;

@Singleton
public class RuntimeTypes implements Types {
  private final TypesDb typesDb;
  private final Map<String, ConcreteType> cache;

  @Inject
  public RuntimeTypes(TypesDb typesDb) {
    this.typesDb = typesDb;
    this.cache = createInitializedCache(typesDb);
  }

  private static HashMap<String, ConcreteType> createInitializedCache(TypesDb typesDb) {
    HashMap<String, ConcreteType> map = new HashMap<>();
    putType(map, typesDb.bool());
    putType(map, typesDb.string());
    putType(map, typesDb.blob());
    putType(map, typesDb.nothing());
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
    return typesDb.bool();
  }

  @Override
  public ConcreteType string() {
    return typesDb.string();
  }

  @Override
  public ConcreteType blob() {
    return typesDb.blob();
  }

  @Override
  public ConcreteType nothing() {
    return typesDb.nothing();
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
    return typesDb.array(elementType);
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
    StructType type = typesDb.struct(name, fields);
    cache.put(name, type);
    return type;
  }
}
