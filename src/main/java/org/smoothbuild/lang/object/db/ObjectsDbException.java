package org.smoothbuild.lang.object.db;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;

public class ObjectsDbException extends RuntimeException {
  public static ObjectsDbException corruptedObjectException(Hash hash, String message) {
    return new ObjectsDbException(hash.toString() + " object in ObjectsDb is corrupted. " + message);
  }

  public static ObjectsDbException objectsDbException(IOException e) {
    return new ObjectsDbException("IOException when accessing ObjectsDb", e);
  }

  public ObjectsDbException(String message, Throwable e) {
    super(message, e);
  }

  public ObjectsDbException(String message) {
    super(message);
  }
}
