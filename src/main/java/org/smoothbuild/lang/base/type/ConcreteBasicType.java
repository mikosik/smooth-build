package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.type.property.BasicProperties;
import org.smoothbuild.record.base.Record;

public abstract class ConcreteBasicType extends ConcreteType {
  public ConcreteBasicType(String name, Class<? extends Record> jType) {
    super(name, internal(), null, new BasicProperties(jType));
  }
}
