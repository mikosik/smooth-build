package org.smoothbuild.lang.function.nativ;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.ImmutableMap;

public class ArgumentsCreatorTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final ArgumentsCreator argumentsCreator = new ArgumentsCreator(
      MyParametersInterface.class);
  private SString sstring;
  private MyParametersInterface args;

  @Test
  public void sstring_argument_is_provided() {
    given(sstring = objectsDb.string("my string"));
    given(args = createArgs(ImmutableMap.<String, Value> of("stringParam", sstring)));
    when(args.stringParam());
    thenReturned(sstring);
  }

  @Test
  public void not_set_arguments_are_provided_as_nulls() {
    given(sstring = objectsDb.string("my string"));
    given(args = createArgs(ImmutableMap.<String, Value> of("stringParam", sstring)));
    when(args.string2());
    thenReturned(null);
  }

  private MyParametersInterface createArgs(ImmutableMap<String, Value> map) {
    return (MyParametersInterface) argumentsCreator.create(map);
  }

  public interface MyParametersInterface {
    public SString stringParam();

    public SString string2();

    public Integer integer();
  }
}
