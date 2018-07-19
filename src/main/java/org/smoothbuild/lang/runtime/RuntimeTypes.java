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
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

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
    return map;
  }

  private static void putType(HashMap<String, Type> map, Type type) {
    map.put(type.name(), type);
  }

  public Set<String> names() {
    return unmodifiableSet(cache.keySet());
  }

  public Map<String, Type> nameToTypeMap() {
    return unmodifiableMap(cache);
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
  public Type generic(String name) {
    return typesDb.generic(name);
  }

  @Override
  public StructType file() {
    return (StructType) getType("File");
  }

  @Override
  public ArrayType array(Type elementType) {
    return typesDb.array(elementType);
  }

  public boolean hasType(String name) {
    return cache.containsKey(name);
  }

  @Override
  public Type getType(String name) {
    Type type = cache.get(name);
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

  public Type replaceCoreType(Type type, Type newCoreType) {
    if (type.isArray()) {
      return array(replaceCoreType(((ArrayType) type).elemType(), newCoreType));
    }
    return newCoreType;
  }

  @Override
  public Type fixNameClashIfExists(Type type, Type typeToFix) {
    if (typeToFix.coreType().isGeneric() && typeToFix.coreType().equals(type.coreType())) {
      return renameGeneric(typeToFix);
    }
    return typeToFix;
  }

  private Type renameGeneric(Type type) {
    if (type.isArray()) {
      return array(renameGeneric(((ArrayType) type).elemType()));
    } else {
      return generic(type.name() + "'");
    }
  }
}
