package org.smoothbuild.vm.bytecode.hashed;

import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.vm.bytecode.hashed.HashedDb.dbPathTo;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.function.Function0;

public class HashingSink extends ForwardingSink {
  private final FileSystem fileSystem;
  private final PathS tempPath;
  private final Function0<Hash, IOException> hashMemoizer;

  HashingSink(FileSystem fileSystem, PathS tempPath) throws IOException {
    super(Hash.hashingSink(fileSystem.sinkWithoutBuffer(tempPath)));
    this.fileSystem = fileSystem;
    this.tempPath = tempPath;
    this.hashMemoizer = memoizer(this::calculateHash);
  }

  public Hash hash() throws IOException {
    return hashMemoizer.apply();
  }

  @Override
  public void close() throws IOException {
    hashMemoizer.apply();
  }

  private okio.HashingSink wrappedSink() {
    return (okio.HashingSink) delegate();
  }

  private Hash calculateHash() throws IOException {
    var hashingSink = wrappedSink();
    hashingSink.close();
    var localHash = new Hash(hashingSink.hash());
    var path = dbPathTo(localHash);
    var pathState = fileSystem.pathState(path);
    switch (pathState) {
      case NOTHING -> fileSystem.move(tempPath, path);
      case FILE -> fileSystem.delete(tempPath);
      case DIR -> throw new IOException(
          "Corrupted HashedDb. Cannot store data at " + path.q() + " as it is a directory.");
    }
    return localHash;
  }
}
