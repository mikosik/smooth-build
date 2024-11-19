package org.smoothbuild.common.filesystem.base;

public interface PathI<P extends PathI<P>> {
  public boolean isRoot();

  public P parent();

  public boolean startsWith(P path);

  public P append(Path path);

  public P appendPart(String part);

  public P withExtension(String extension);

  public String q();
}
