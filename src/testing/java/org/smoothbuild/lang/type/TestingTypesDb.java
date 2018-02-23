package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.TestingHashedDb;

public class TestingTypesDb extends TypesDb {
  public TestingTypesDb() {
    super(new TestingHashedDb());
  }
}
