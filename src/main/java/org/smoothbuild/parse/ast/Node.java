package org.smoothbuild.parse.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.smoothbuild.lang.base.Location;

public class Node {
  private final Location location;
  private Map<Class<?>, Object> map;

  public Node(Location location) {
    this.location = location;
  }

  public Location location() {
    return location;
  }

  public <T> T get(Class<T> clazz) {
    if (!map().containsKey(clazz)) {
      throw new NoSuchElementException("Cannot find attribute of type" + clazz.getName()
          + " in AST Node " + this.getClass().getCanonicalName() + " " + toString());
    }
    @SuppressWarnings("unchecked")
    T result = (T) map().get(clazz);
    return result;
  }

  public <T> void set(Class<T> clazz, T value) {
    map().put(clazz, value);
  }

  private Map<Class<?>, Object> map() {
    if (map == null) {
      map = new HashMap<>();
    }
    return map;
  }
}
