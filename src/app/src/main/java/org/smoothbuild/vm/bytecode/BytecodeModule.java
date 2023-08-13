package org.smoothbuild.vm.bytecode;

import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.space.Space.PROJECT;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SubFileSystem;
import org.smoothbuild.filesystem.space.ForSpace;
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
  private HashedDb provideHashedDb(@ForSpace(PROJECT) FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, HASHED_DB_PATH));
  }
}
