package org.smoothbuild.virtualmachine.dagger;

import dagger.Component;
import org.smoothbuild.common.dagger.CommonTestComponent;
import org.smoothbuild.common.dagger.FileSystemTestModule;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.dagger.ReportTestModule;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.schedule.SchedulerModule;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.evaluate.base.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.cache.CachingOperatorEvaluator;
import org.smoothbuild.virtualmachine.evaluate.cache.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.cache.ComputationHashFactory;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

@Component(
    modules = {
      VmTestModule.class,
      FileSystemTestModule.class,
      ReportTestModule.class,
      SchedulerModule.class,
      InitializerModule.class,
    })
@PerCommand
public interface VmTestComponent extends CommonTestComponent {
  VmComponent.Builder vmComponentBuilder();

  Container container();

  ComputationCache computationCache();

  FileContentReader fileContentReader();

  CachingOperatorEvaluator cachingOperatorEvaluator();

  ComputationHashFactory computationHashFactory();

  BytecodeFactory bytecodeFactory();

  BExprDb exprDb();

  BKindDb kindDb();

  HashedDb hashedDb();

  @Project
  FileSystem<Path> projectFileSystem();

  @ComputationDb
  FileSystem<Path> computationDbFileSystem();

  FileSystem<FullPath> fileSystem();

  BReferenceInliner bReferenceInliner();

  @BytecodeDb
  FullPath bytecodeDbPath();

  @ComputationDb
  FullPath computationDbPath();

  @Project
  FullPath projectPath();
}
