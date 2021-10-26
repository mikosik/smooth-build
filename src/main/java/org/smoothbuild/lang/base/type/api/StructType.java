package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.NamedList;

public interface StructType extends Type {
  public NamedList<? extends Type> fields();
}
