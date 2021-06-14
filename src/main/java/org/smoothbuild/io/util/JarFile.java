package org.smoothbuild.io.util;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.FileLocation;

public record JarFile(FileLocation location, Path resolvedPath, Hash hash) {
}
