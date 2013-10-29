package org.smoothbuild.testing.message;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CallLocation;

public class FakeCallLocation extends CallLocation {
  public FakeCallLocation() {
    super(Name.simpleName("name"), new FakeCodeLocation());
  }
}
