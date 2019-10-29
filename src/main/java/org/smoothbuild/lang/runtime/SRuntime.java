package org.smoothbuild.lang.runtime;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.object.db.ObjectFactory;

@Singleton
public class SRuntime {
  private final ObjectFactory objectFactory;
  private final Functions functions;

  @Inject
  public SRuntime(ObjectFactory objectFactory, Functions functions) {
    this.objectFactory = objectFactory;
    this.functions = functions;
  }

  public ObjectFactory objectFactory() {
    return objectFactory;
  }

  public Functions functions() {
    return functions;
  }
}
