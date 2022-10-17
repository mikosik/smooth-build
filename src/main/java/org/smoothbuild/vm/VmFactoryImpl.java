package org.smoothbuild.vm;

import javax.inject.Inject;

import org.smoothbuild.compile.sb.BsMapping;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public class VmFactoryImpl implements VmFactory {
  private final Injector injector;

  @Inject
  public VmFactoryImpl(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Vm newVm(BsMapping bsMapping) {
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(BsMapping.class).toInstance(bsMapping);
      }
    });
    return childInjector.getInstance(Vm.class);
  }
}
