##### Checking whether grammar is not ambiguous:

 * Add following line to ScriptParser.parseScript()

`parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);`

 * Run all acceptance tests
