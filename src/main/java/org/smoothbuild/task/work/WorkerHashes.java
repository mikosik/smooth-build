package org.smoothbuild.task.work;

import static org.smoothbuild.SmoothConstants.CHARSET;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class WorkerHashes {
  public static HashCode workerHash(Class<?> workerClass) {
    return workerHash(workerClass, Hash.integer(0));
  }

  public static HashCode workerHash(Class<?> workerClass, HashCode workerTypeHash) {
    Hasher hasher = Hash.newHasher();
    hasher.putString(workerClass.getCanonicalName(), CHARSET);
    hasher.putBytes(workerTypeHash.asBytes());
    return hasher.hash();
  }
}
