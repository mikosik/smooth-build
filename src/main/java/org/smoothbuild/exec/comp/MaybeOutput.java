package org.smoothbuild.exec.comp;

public record MaybeOutput(Output output, Exception exception) {

  public MaybeOutput(Output output) {
    this(output, null);
  }

  public MaybeOutput(Exception exception) {
    this(null, exception);
  }

  public boolean hasOutput() {
    return output != null;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }
}
