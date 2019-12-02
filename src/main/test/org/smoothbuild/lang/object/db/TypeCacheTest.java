package org.smoothbuild.lang.object.db;

import static org.hamcrest.Matchers.sameInstance;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;

public class TypeCacheTest {
  private TypeCache cache;
  private ConcreteType type;
  private Hash hash1;

  @Test
  public void getting_object_from_empty_cache_returns_null() {
    given(cache = new TypeCache());
    when(() -> cache.get(Hash.of(123)));
    thenReturned(null);
  }

  @Test
  public void cached_object_can_be_fetched() {
    given(cache = new TypeCache());
    given(hash1 = Hash.of(123));
    given(type = mockType(hash1));
    given(cache.cache(type));
    when(() -> cache.get(hash1));
    thenReturned(sameInstance(type));
  }

  @Test
  public void cache_method_returns_object_being_added() {
    given(cache = new TypeCache());
    given(hash1 = Hash.of(123));
    given(type = mockType(hash1));
    when(() -> cache.cache(type));
    thenReturned(sameInstance(type));
  }

  @Test
  public void cache_method_returns_already_cached_object_when_hash_is_equal() {
    given(cache = new TypeCache());
    given(hash1 = Hash.of(123));
    given(type = mockType(hash1));
    given(() -> cache.cache(type));
    when(() -> cache.cache(mockType(hash1)));
    thenReturned(sameInstance(type));
  }

  @Test
  public void cache_method_ignores_object_when_other_with_same_hash_is_cached() {
    given(cache = new TypeCache());
    given(hash1 = Hash.of(123));
    given(type = mockType(hash1));
    given(() -> cache.cache(type));
    given(() -> cache.cache(mockType(hash1)));
    when(() -> cache.get(hash1));
    thenReturned(sameInstance(type));
  }

  private ConcreteType mockType(Hash hash) {
    ConcreteType mock = mock(ConcreteType.class);
    given(willReturn(hash), mock).hash();
    return mock;
  }
}