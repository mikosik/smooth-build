package org.smoothbuild.bytecode;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ByteDb;
import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.TypeFactoryB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ByteDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ByteDb provideByteDb(HashedDb hashedDb, CatDb catDb, TypingB typing) {
    return new ByteDbImpl(hashedDb, catDb, typing);
  }

  @Provides
  public TypeFactoryB provideTypeFactoryH(CatDb catDb) {
    return catDb;
  }

  @Provides
  @Singleton
  public CatDb provideCatDb(HashedDb hashedDb) {
    return new CatDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, OBJECT_DB_PATH, tempManager);
  }
}
