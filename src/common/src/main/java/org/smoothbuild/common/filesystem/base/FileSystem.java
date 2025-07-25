package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

public interface FileSystem<P extends PathI<P>> {
  public PathState pathState(P path) throws IOException;

  public PathIterator filesRecursively(P dir);

  public Iterable<Path> files(P dir) throws IOException;

  public void move(P source, P target) throws IOException;

  public void deleteRecursively(P path) throws IOException;

  public long size(P path) throws IOException;

  public Source source(P path) throws IOException;

  public Sink sink(P path) throws IOException;

  public void createLink(P link, P target) throws IOException;

  public void createDir(P path) throws IOException;
}
