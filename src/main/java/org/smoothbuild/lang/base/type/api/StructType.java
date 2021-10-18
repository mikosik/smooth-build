package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface StructType extends Type {
  public ImmutableList<? extends Type> fields();

  public ImmutableList<String> names();

  public ImmutableMap<String, Integer> nameToIndex();
}
