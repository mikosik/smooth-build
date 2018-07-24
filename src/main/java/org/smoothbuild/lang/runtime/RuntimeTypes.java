package org.smoothbuild.lang.runtime;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
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
    return (StructType) getType("File");
  }

  @Override
  public ConcreteArrayType array(ConcreteType elementType) {
    return typesDb.array(elementType);
  }

  @Override
  public ConcreteType getType(String name) {
    ConcreteType type = cache.get(name);
    if (type == null) {
      throw new IllegalStateException("Unknown runtime type '" + name + "'.");
    }
    return type;
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
