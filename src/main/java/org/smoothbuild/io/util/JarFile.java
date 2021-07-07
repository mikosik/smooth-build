package org.smoothbuild.io.util;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.FilePath;

public record JarFile(FilePath filePath, Hash hash) {
}
