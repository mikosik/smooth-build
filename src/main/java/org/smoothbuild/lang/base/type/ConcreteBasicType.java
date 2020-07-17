package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.type.compound.BasicProperties;
import org.smoothbuild.lang.object.base.SObject;

public abstract class ConcreteBasicType extends ConcreteType {
  public ConcreteBasicType(String name, Class<? extends SObject> jType) {
    super(name, internal(), null, new BasicProperties(jType));
  }
}
