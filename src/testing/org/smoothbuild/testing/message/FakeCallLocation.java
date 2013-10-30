package org.smoothbuild.testing.message;

import org.smoothbuild.message.message.CallLocation;

public class FakeCallLocation extends CallLocation {
  public FakeCallLocation() {
    super("name", new FakeCodeLocation());
  }
}
