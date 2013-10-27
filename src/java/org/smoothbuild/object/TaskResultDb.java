package org.smoothbuild.object;

import javax.inject.Inject;

import com.google.common.hash.HashCode;

/**
 * Database that keeps mapping from task hash to task result hash. Task result
 * hash can be used to retrieve actual result from ObjectDb.
 */
public class TaskResultDb {
  private final HashedDb hashedDb;

  @Inject
  public TaskResultDb(@TaskResults HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public void store(HashCode taskHash, HashCode resultObjectHash) {
    hashedDb.store(taskHash, Marshaller.marshallHash(resultObjectHash));
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public HashCode read(HashCode taskHash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, taskHash);) {
      return unmarshaller.readHash();
    }
  }
}
