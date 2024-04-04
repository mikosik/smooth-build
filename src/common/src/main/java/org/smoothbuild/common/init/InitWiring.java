package org.smoothbuild.common.init;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;

public class InitWiring extends AbstractModule {
  @Override
  protected void configure() {
    newSetBinder(binder(), Initializable.class);
  }
}
