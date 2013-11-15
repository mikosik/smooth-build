package org.smoothbuild.message.base;

import com.google.common.base.Ascii;

public enum MessageType {
  FATAL(true), ERROR(true), WARNING(false), SUGGESTION(false), INFO(false);

  private final boolean isProblem;

  private MessageType(boolean isProblem) {
    this.isProblem = isProblem;
  }

  public boolean isProblem() {
    return isProblem;
  }

  public String namePlural() {
    return Ascii.toLowerCase(name()) + "s";
  }
}
