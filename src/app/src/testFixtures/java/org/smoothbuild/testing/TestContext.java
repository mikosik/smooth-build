package org.smoothbuild.testing;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import java.io.PrintWriter;
import org.smoothbuild.common.Hash;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.compile.backend.BackendCompile;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.compile.backend.SbTranslator;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.out.report.SystemOutReporter;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class TestContext extends TestVirtualMachine {
  public BackendCompile sbTranslatorFacade(
      FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    return new BackendCompile(bytecodeF(), filePersister, bytecodeLoader);
  }

  public SbTranslator sbTranslator(ImmutableBindings<NamedEvaluableS> evaluables) {
    return sbTranslator(filePersister(), evaluables);
  }

  public SbTranslator sbTranslator(
      FilePersister filePersister, ImmutableBindings<NamedEvaluableS> evaluables) {
    return sbTranslator(filePersister, bytecodeLoader(), evaluables);
  }

  private SbTranslator sbTranslator(
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables) {
    return new SbTranslator(bytecodeF(), filePersister, bytecodeLoader, evaluables);
  }

  @Override
  public TaskReporterImpl taskReporter() {
    return taskReporter(reporter());
  }

  public TaskReporterImpl taskReporter(Reporter reporter) {
    return new TaskReporterImpl(ALL, reporter, bsMapping());
  }

  public SystemOutReporter reporter() {
    return new SystemOutReporter(new PrintWriter(systemOut(), true), Level.INFO);
  }

  public static BsMapping bsMapping() {
    return new BsMapping(map(), map());
  }

  public static BsMapping bsMapping(Hash hash, String name) {
    return new BsMapping(map(hash, name), map());
  }

  public static BsMapping bsMapping(Hash hash, Location location) {
    return new BsMapping(map(), map(hash, location));
  }
}
