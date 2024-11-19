package org.smoothbuild.virtualmachine.wire;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDbInitializer;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCacheInitializer;

public class VmWiring extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<Initializable> setBinder = newSetBinder(binder(), Initializable.class);
    setBinder.addBinding().to(HashedDbInitializer.class);
    setBinder.addBinding().to(ComputationCacheInitializer.class);
  }

  @Provides
  @Singleton
  public BExprDb provideExprDb(HashedDb hashedDb, BKindDb kindDb) {
    return new BExprDb(hashedDb, kindDb);
  }

  @Provides
  @Singleton
  public BKindDb provideKindDb(HashedDb hashedDb) {
    return new BKindDb(hashedDb);
  }

  @Provides
  @BytecodeDb
  public FileSystem<Path> provideBytecodeDbDir(
      FileSystem<FullPath> filesystem, @BytecodeDb FullPath path) throws IOException {
    return fileSystemPart(filesystem, path);
  }

  @Provides
  @ComputationDb
  public FileSystem<Path> provideComputationDbDir(
      FileSystem<FullPath> filesystem, @ComputationDb FullPath path) throws IOException {
    return fileSystemPart(filesystem, path);
  }
}
