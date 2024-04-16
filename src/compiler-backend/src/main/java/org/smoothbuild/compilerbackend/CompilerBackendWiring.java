package org.smoothbuild.compilerbackend;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CompilerBackendWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(SbTranslator.class, SbTranslator.class)
        .build(SbTranslatorFactory.class));
  }
}
