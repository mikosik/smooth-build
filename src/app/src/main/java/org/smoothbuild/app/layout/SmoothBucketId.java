package org.smoothbuild.app.layout;

import org.smoothbuild.common.bucket.base.BucketId;

public enum SmoothBucketId implements BucketId {
  PROJECT("project"),
  LIBRARY("library"),
  INSTALL("install"),
  ;

  private final String id;

  SmoothBucketId(String id) {
    this.id = id;
  }

  @Override
  public String get() {
    return id;
  }
}
