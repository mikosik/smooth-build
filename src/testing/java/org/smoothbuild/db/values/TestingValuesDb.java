package org.smoothbuild.db.values;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;

public class TestingValuesDb extends ValuesDb {
  public TestingValuesDb() {
    this(new TestingHashedDb());
  }

  public TestingValuesDb(HashedDb hashedDb) {
    super(hashedDb);
  }
}
