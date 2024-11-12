package org.smoothbuild.virtualmachine.wire;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import jakarta.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCacheInitializer;

public class VmWiring extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<Initializable> setBinder = newSetBinder(binder(), Initializable.class);
    setBinder.addBinding().to(HashedDb.class);
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
  public Bucket provideBytecodeDbBucket(Filesystem filesystem, @BytecodeDb FullPath path) {
    return filesystem.bucketFor(path);
  }

  @Provides
  @ComputationDb
  public Bucket provideComputationDbBucket(Filesystem filesystem, @ComputationDb FullPath path) {
    return filesystem.bucketFor(path);
  }
}
