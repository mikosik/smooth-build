package org.smoothbuild.plugin.api;

public interface StringSet extends Iterable<String> {
  boolean contains(String string);
}
