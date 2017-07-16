package org.smoothbuild.parse.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.smoothbuild.lang.message.CodeLocation;

public class Node {
  private final CodeLocation codeLocation;
  private Map<Class<?>, Object> map;

  public Node(CodeLocation codeLocation) {
    this.codeLocation = codeLocation;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public <T> T get(Class<T> clazz) {
    Object result = map().get(clazz);
    if (result == null) {
      throw new NoSuchElementException(clazz.getName());
    }
    return (T) result;
  }

  public <T> void set(Class<T> clazz, T value) {
    map().put(clazz, value);
  }

  public boolean has(Class<?> clazz) {
    return map().containsKey(clazz);
  }

  private Map<Class<?>, Object> map() {
    if (map == null) {
      map = new HashMap<>();
    }
    return map;
  }
}
