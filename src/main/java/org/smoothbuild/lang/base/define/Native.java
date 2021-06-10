package org.smoothbuild.lang.base.define;

public record Native(String path, boolean isPure, Location location) implements Body {
}
