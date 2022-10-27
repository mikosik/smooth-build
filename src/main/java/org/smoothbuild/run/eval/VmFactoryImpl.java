package org.smoothbuild.run.eval;

import javax.inject.Inject;

import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.run.eval.report.ConsoleTaskReporter;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.execute.TaskReporter;

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
        bind(TaskReporter.class).to(ConsoleTaskReporter.class);
        bind(BsMapping.class).toInstance(bsMapping);
      }
    });
    return childInjector.getInstance(Vm.class);
  }
}
