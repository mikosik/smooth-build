package org.smoothbuild.lang.message;

import com.google.common.base.Ascii;

public enum MessageType {
  ERROR,
  WARNING,
  SUGGESTION,
  INFO;

  public String namePlural() {
    return Ascii.toLowerCase(name()) + "(s)";
  }
}
