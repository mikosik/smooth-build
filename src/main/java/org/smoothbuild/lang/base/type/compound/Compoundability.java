package org.smoothbuild.lang.base.type.compound;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectFactory;

public interface Compoundability {
  public boolean isArray();

  public Type coreType(Type type);

  public int coreDepth(Type type);

  public Type changeCoreDepthBy(Type type, int delta);

  public <T extends Type> T actualCoreTypeWhenAssignedFrom(Type destination, T source);

  public org.smoothbuild.lang.object.type.Type toRecordType(Type type, ObjectFactory objectFactory);

  public boolean areEqual(Type type, Object object);

  public int hashCode(Type type);

  public Class<? extends SObject> jType();
}
