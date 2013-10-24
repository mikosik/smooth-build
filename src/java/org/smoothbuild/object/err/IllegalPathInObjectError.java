package org.smoothbuild.object.err;


public class IllegalPathInObjectError extends ObjectDbError {
  public IllegalPathInObjectError(String message) {
    super("Objects database corrupted: reading path failed with: " + message);
  }
}
