A Resolvable thing has a definition and does not require any calculations in order to be loaded on the stack. Variables,
Constants, Literals and Classes are all Resolvable. Variables are loaded with a `load var` opcode, which makes sense.
Literals are loaded with a `load primitive` opcode, of which there are many. Constants are remembered and replaced with
Literals as the compiler progress through the file.

A Calculable thing consists of Resolvable things and includes either an operator or comparator. Calculables with a
comparator will always result in a boolean. Calculables with an operator will result in the same type as their first
argument. Calculables can use function calls or any other opcode in order to reach their result.
