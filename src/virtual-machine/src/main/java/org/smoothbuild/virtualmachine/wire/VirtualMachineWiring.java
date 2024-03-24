package org.smoothbuild.virtualmachine.wire;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.type.BKindDb;

public class VirtualMachineWiring extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public BExprDb provideExprDb(@BytecodeDb HashedDb hashedDb, BKindDb kindDb) {
    return new BExprDb(hashedDb, kindDb);
  }

  @Provides
  @Singleton
  public BKindDb provideKindDb(@BytecodeDb HashedDb hashedDb) {
    return new BKindDb(hashedDb);
  }

  @Provides
  @Singleton
  @BytecodeDb
  private HashedDb provideHashedDb(@BytecodeDb Bucket bucket) {
    return new HashedDb(bucket);
  }
}
