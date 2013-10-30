package org.smoothbuild.db;

import static com.google.common.collect.Lists.newArrayList;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.plugin.Value;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class HashedSorterTest {
  FakeHashed hashed1;
  FakeHashed hashed2;
  FakeHashed hashed3;
  FakeHashed hashed4;

  @Test
  public void hashed_objects_are_sorted_by_their_hashes() {
    given(hashed1 = new FakeHashed(new byte[] { 0, 0 }));
    given(hashed2 = new FakeHashed(new byte[] { 0, 1 }));
    given(hashed3 = new FakeHashed(new byte[] { 1, 0 }));
    given(hashed4 = new FakeHashed(new byte[] { 1, 1 }));
    when(HashedSorter.sort(newArrayList(hashed4, hashed3, hashed2, hashed1)));
    thenReturned(newArrayList(hashed1, hashed2, hashed3, hashed4));
  }

  @Test
  public void sorting_empty_list_returns_empty_list() throws Exception {
    when(HashedSorter.sort(Lists.<Value> newArrayList()));
    thenReturned(Lists.<Value> newArrayList());
  }

  private static class FakeHashed implements Value {
    private final HashCode hash;

    public FakeHashed(byte[] bytes) {
      this.hash = HashCode.fromBytes(bytes);
    }

    @Override
    public HashCode hash() {
      return hash;
    }

  }
}
