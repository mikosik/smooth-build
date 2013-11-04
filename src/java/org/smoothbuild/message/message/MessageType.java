package org.smoothbuild.message.message;

import com.google.common.base.Ascii;

public enum MessageType {
  FATAL, ERROR, WARNING, SUGGESTION, INFO;

  public String namePlural() {
    return Ascii.toLowerCase(name()) + "s";
  }
}
