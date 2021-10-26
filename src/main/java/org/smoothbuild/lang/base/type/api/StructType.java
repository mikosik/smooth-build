package org.smoothbuild.lang.base.type.api;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface StructType extends Type {
  public ImmutableList<? extends Type> fields();

  public ImmutableList<Optional<String>> names();

  public ImmutableMap<String, Integer> nameToIndex();
}
