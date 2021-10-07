package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public interface StructType extends Type {
  public ImmutableList<ItemSignature> fields();

  public boolean containsFieldWithName(String name);

  public ItemSignature fieldWithName(String name);

  public int fieldIndex(String name);
}
