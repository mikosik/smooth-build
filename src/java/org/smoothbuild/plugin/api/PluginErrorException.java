package org.smoothbuild.plugin.api;

import org.smoothbuild.message.Message;

@SuppressWarnings("serial")
public class PluginErrorException extends RuntimeException {
  private final Message error;

  public PluginErrorException(Message error) {
    this.error = error;
  }

  @Override
  public String getMessage() {
    return error.message();
  }

  public Message error() {
    return error;
  }
}
