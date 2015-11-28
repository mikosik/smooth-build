package org.smoothbuild.lang.message;

import com.google.common.base.Ascii;

public enum MessageType {
  ERROR,
  WARNING,
  INFO;

  public String namePlural() {
    return Ascii.toLowerCase(name()) + "(s)";
  }
}
