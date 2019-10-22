package org.smoothbuild.lang.type;

import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.runtime.RuntimeTypes;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TypesModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  public Types provideTypes(RuntimeTypes runtimeTypes) {
    return runtimeTypes;
  }
}
