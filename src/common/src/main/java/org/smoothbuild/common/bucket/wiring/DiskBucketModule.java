package org.smoothbuild.common.bucket.wiring;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import java.nio.file.Path;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.collect.Map;

public class DiskBucketModule extends AbstractModule {
  private final Map<BucketId, Path> bucketIdToPath;

  public DiskBucketModule(Map<BucketId, Path> bucketIdToPath) {
    this.bucketIdToPath = bucketIdToPath;
  }

  @Override
  protected void configure() {
    bind(BucketFactory.class).to(DiskBucketFactory.class);
    var mapBinder = MapBinder.newMapBinder(binder(), BucketId.class, Path.class);
    bucketIdToPath.forEach((space, path) -> mapBinder.addBinding(space).toInstance(path));
  }
}
