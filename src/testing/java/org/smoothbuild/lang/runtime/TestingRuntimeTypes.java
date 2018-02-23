package org.smoothbuild.lang.runtime;

import org.smoothbuild.lang.type.TypesDb;

import com.google.common.collect.ImmutableMap;

public class TestingRuntimeTypes extends RuntimeTypes {
  public TestingRuntimeTypes(TypesDb typesDb) {
    super(typesDb);
    struct("File", ImmutableMap.of("content", blob(), "path", string()));
  }
}
