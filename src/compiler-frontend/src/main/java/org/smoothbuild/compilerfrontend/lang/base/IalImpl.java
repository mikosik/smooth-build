package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;

/**
 * Default Ial implementation.
 */
public class IalImpl extends HasLocationImpl implements Ial {
  private final Id id;

  public IalImpl(Id id, Location location) {
    super(location);
    this.id = requireNonNull(id);
  }

  @Override
  public Id id() {
    return id;
  }
}
