package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.ByteCodeExc;

public class ObjDbExc extends ByteCodeExc {
  public ObjDbExc(String message) {
    super(message);
  }

  public ObjDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjDbExc(Throwable cause) {
    super(cause);
  }
}
