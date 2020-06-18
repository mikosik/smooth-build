package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Location;

public record Field(ConcreteType type, String name, Location location) {
}
