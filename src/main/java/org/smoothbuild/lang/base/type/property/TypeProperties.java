package org.smoothbuild.lang.base.type.property;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.record.base.Record;

public interface TypeProperties {
  public boolean isArray();

  public Type coreType(Type type);

  public int coreDepth(Type type);

  public Type changeCoreDepthBy(Type type, int delta);

  public <T extends Type> T actualCoreTypeWhenAssignedFrom(Type destination, T source);

  public boolean areEqual(Type type, Object object);

  public int hashCode(Type type);

  public Class<? extends Record> jType();
}
