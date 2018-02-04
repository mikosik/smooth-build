package org.smoothbuild.lang.type;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.plugin.Types;

import com.google.common.collect.ImmutableMap;

@Singleton
public class RuntimeTypes implements Types {
  private final TypesDb typesDb;
  private final Map<String, Type> cache;

  @Inject
  public RuntimeTypes(TypesDb typesDb) {
    this.typesDb = typesDb;
    this.cache = createInitializedCache(typesDb);
  }

  private static HashMap<String, Type> createInitializedCache(TypesDb typesDb) {
    HashMap<String, Type> map = new HashMap<>();
    putType(map, typesDb.string());
    putType(map, typesDb.blob());
    putType(map, typesDb.nothing());
    putType(map, typesDb.file());
    return map;
  }

  private static void putType(HashMap<String, Type> map, Type type) {
    map.put(type.name(), type);
  }

  @Override
  public Type string() {
    return typesDb.string();
  }

  @Override
  public Type blob() {
    return typesDb.blob();
  }

  @Override
  public Type nothing() {
    return typesDb.nothing();
  }

  @Override
  public Type file() {
    return typesDb.file();
  }

  @Override
  public ArrayType array(Type elementType) {
    return typesDb.array(elementType);
  }

  public Type withName(String name) {
    Type type = cache.get(name);
    if (type == null) {
      throw new IllegalStateException("Unknown runtime type '" + name + "'.");
    }
    return type;
  }

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    if (cache.containsKey(name)) {
      throw new IllegalStateException("Type '" + name + "' is already added to runtime types.");
    }
    StructType type = typesDb.struct(name, fields);
    cache.put(name, type);
    return type;
  }
}
