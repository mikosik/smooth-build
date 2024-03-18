package org.smoothbuild.app.layout;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;

public class BucketIdUtils {
  public static void addMapBindingForBucket(Binder binder, SmoothBucketId id) {
    var mapBinder = MapBinder.newMapBinder(binder, BucketId.class, Bucket.class);
    mapBinder.addBinding(id).to(Key.get(Bucket.class, forBucketId(id)));
  }

  public static ForBucketImpl forBucketId(SmoothBucketId id) {
    return new ForBucketImpl(id);
  }
}
