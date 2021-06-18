package org.smoothbuild.io.util;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.FilePath;

public record JarFile(FilePath location, Path resolvedPath, Hash hash) {
}
