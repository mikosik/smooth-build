package org.smoothbuild.type.api;

public interface StringSet extends Iterable<String> {
  boolean contains(String string);
}
