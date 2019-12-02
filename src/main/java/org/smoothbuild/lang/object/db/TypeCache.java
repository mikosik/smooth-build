package org.smoothbuild.lang.object.db;

import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;

public class TypeCache {
  private final Map<Hash, ConcreteType> cache;

  public TypeCache() {
    this.cache = new HashMap<>();
  }

  public synchronized ConcreteType cache(ConcreteType type) {
    Hash hash = type.hash();
    ConcreteType cachedType = cache.get(hash);
    if (cachedType != null) {
      return cachedType;
    } else {
      cache.put(hash, type);
      return type;
    }
  }

  public synchronized ConcreteType get(Hash hash) {
    return cache.get(hash);
  }
}