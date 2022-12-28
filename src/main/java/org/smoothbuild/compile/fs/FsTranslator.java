package org.smoothbuild.compile.fs;

import static org.smoothbuild.compile.fs.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.fs.ps.PsTranslator.translatePs;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.fp.FpTranslator;
import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

/**
 * F->S Translator.
 * Translates source code F-ile to S-objects.
 */
public class FsTranslator {
  private final FpTranslator fpTranslator;

  @Inject
  public FsTranslator(FpTranslator fpTranslator) {
    this.fpTranslator = fpTranslator;
  }

  public Maybe<DefinitionsS> translateFs(List<ModuleResources> modules) {
    return new Translator(fpTranslator, modules)
        .process();
  }

  private static class Translator extends MaybeProcessor<DefinitionsS> {
    private final FpTranslator fpTranslator;
    private final List<ModuleResources> modules;

    public Translator(FpTranslator fpTranslator, List<ModuleResources> modules) {
      this.fpTranslator = fpTranslator;
      this.modules = modules;
    }

    @Override
    protected DefinitionsS processImpl() throws FailedException {
      var internalModule = loadInternalModule();
      var definitionsS = DefinitionsS.empty().withModule(internalModule);
      for (ModuleResources moduleResources : modules) {
        var moduleP = addLogsAndGetValue(fpTranslator.translateFp(moduleResources));
        var moduleS = addLogsAndGetValue(translatePs(moduleP, definitionsS));
        definitionsS = definitionsS.withModule(moduleS);
      }
      return definitionsS;
    }
  }
}
