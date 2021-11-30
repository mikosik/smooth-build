package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.exec.java.FileLoader;
import org.smoothbuild.lang.base.define.DefsS;

public class ShConverterProvider {
  private final TypeShConverter typeShConverter;
  private final ObjFactory objectFactory;
  private final FileLoader fileLoader;

  @Inject
  public ShConverterProvider(TypeShConverter typeShConverter,
      ObjFactory objectFactory, FileLoader fileLoader) {
    this.typeShConverter = typeShConverter;
    this.objectFactory = objectFactory;
    this.fileLoader = fileLoader;
  }

  public ShConverter get(DefsS defsS) {
    return new ShConverter(objectFactory, defsS, typeShConverter, fileLoader);
  }
}
