package org.smoothbuild.vm.bytecode;

import static org.smoothbuild.fs.space.Space.PRJ;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.fs.project.TempManager;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.type.CategoryDb;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import jakarta.inject.Singleton;

public class BytecodeModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public BytecodeDb provideBytecodeDb(HashedDb hashedDb, CategoryDb categoryDb) {
    return new BytecodeDb(hashedDb, categoryDb);
  }

  @Provides
  @Singleton
  public CategoryDb provideCatDb(HashedDb hashedDb) {
    return new CategoryDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, tempManager);
  }
}
