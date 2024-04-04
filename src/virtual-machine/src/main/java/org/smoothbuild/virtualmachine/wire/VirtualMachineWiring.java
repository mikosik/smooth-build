package org.smoothbuild.virtualmachine.wire;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import jakarta.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;

public class VirtualMachineWiring extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<Initializable> uriBinder = newSetBinder(binder(), Initializable.class);
    uriBinder.addBinding().to(HashedDb.class);
    uriBinder.addBinding().to(ComputationCache.class);
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
  @Singleton
  private HashedDb provideHashedDb(@BytecodeDb Bucket bucket) {
    return new HashedDb(bucket);
  }
}
