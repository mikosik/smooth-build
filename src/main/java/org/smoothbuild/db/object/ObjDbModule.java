package org.smoothbuild.db.object;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ObjDb provideObjDb(HashedDb hashedDb, CatDb catDb, TypingH typing) {
    return new ObjDb(hashedDb, catDb, typing);
  }

  @Provides
  public TypeFactoryH provideTypeFactoryH(CatDb catDb) {
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
