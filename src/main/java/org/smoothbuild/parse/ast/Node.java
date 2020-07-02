package org.smoothbuild.parse.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.Type;

public class Node {
  private final Location location;
  private Map<Class<?>, Object> map;
  private Optional<Type> type;

  public Node(Location location) {
    this.location = location;
  }

  public Location location() {
    return location;
  }

  public Optional<Type> type() {
    return type;
  }

  public void setType(Type type) {
    this.type = Optional.of(type);
  }

  public void setType(Optional<Type> type) {
    this.type = type;
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
