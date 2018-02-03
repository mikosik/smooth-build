package org.smoothbuild.lang.type;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.Values;
import org.smoothbuild.lang.plugin.Types;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TypesDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public Types provideTypes(TypesDb typesDb) {
    return typesDb;
  }

  @Provides
  @Singleton
  public TypesDb provideTypesDb(@Values HashedDb hashedDb) {
    return new TypesDb(hashedDb);
  }
}
