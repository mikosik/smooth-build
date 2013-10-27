package org.smoothbuild.object;

import javax.inject.Inject;

import com.google.common.hash.HashCode;

public class ResultCache {
  private final TaskResultDb taskResultDb;
  private final ObjectDb objectDb;

  @Inject
  public ResultCache(TaskResultDb taskResultDb, ObjectDb objectDb) {
    this.taskResultDb = taskResultDb;
    this.objectDb = objectDb;
  }

  public void store(HashCode taskHash, HashCode resultObjectHash) {
    taskResultDb.store(taskHash, resultObjectHash);
  }

  public boolean contains(HashCode taskHash) {
    return taskResultDb.contains(taskHash);
  }

  public FileSetObject readFileSet(HashCode taskHash) {
    return objectDb.fileSet(resultHash(taskHash));
  }

  public StringSetObject readStringSet(HashCode taskHash) {
    return objectDb.stringSet(resultHash(taskHash));
  }

  public FileObject readFile(HashCode taskHash) {
    return objectDb.file(resultHash(taskHash));
  }

  public StringObject readString(HashCode taskHash) {
    return objectDb.string(resultHash(taskHash));
  }

  private HashCode resultHash(HashCode taskHash) {
    return taskResultDb.read(taskHash);
  }
}
