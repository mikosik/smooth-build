package org.smoothbuild.db.object;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.db.object.type.TypeFactoryO;
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
  public ObjDb provideObjectDb(HashedDb hashedDb, ObjTypeDb objTypeDb) {
    return new ObjDb(hashedDb, objTypeDb);
  }

  @Provides
  public TypeFactoryO provideTypeFactoryO(ObjTypeDb objTypeDb) {
    return objTypeDb;
  }

  @Provides
  @Singleton
  public ObjTypeDb provideObjTypeDb(HashedDb hashedDb) {
    return new ObjTypeDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, OBJECT_DB_PATH, tempManager);
  }
}
