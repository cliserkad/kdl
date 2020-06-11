grammar kdl;

@header {
package com.xarql.kdl.antlr4;
}

// skip over comments in lexer
COMMENT: '//' .*? '\n' -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

// skip over whitespace
WS : [ \t\r\n]+ -> skip;

// keywords
CLASS: 'class';
CONST: 'const';
RUN: 'run';
METHOD: 'mtd';
FUNCTION: 'fnc';
TRUE: 'true';
FALSE: 'false';
RETURN: 'return';
SEE: 'see';
PKG: 'pkg';
R_IF: 'if';

// base types
INT: 'int';
BOOLEAN: 'boolean';
STRING: 'string';

// syntax
BODY_OPEN: '{'; // opening bracket
BODY_CLOSE: '}'; // closing bracket
PARAM_OPEN: '('; // opening paren
PARAM_CLOSE: ')'; // closing paren
BRACE_OPEN: '[';
BRACE_CLOSE: ']';
DOT: '.';
SEPARATOR: ',';
STATEMENT_END: ';';
EQUALS: '=';
ASSIGN: ':';
COMPARE: '?';
PLUS: '+';
MINUS: '-';
DIVIDE: '/';
MULTIPLY: '*';
MODULUS: '%';

DIGIT               : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
fragment UNDERSCORE : '_';
fragment DNTEXT     : DNLETTER+;

CONSTNAME : UPLETTER (UPLETTER | DIGIT | UNDERSCORE)+;
CLASSNAME : UPLETTER DNLETTER (LETTER | DIGIT)+;
VARNAME   : DNLETTER (LETTER | DIGIT)*;

QUALIFIED_NAME: (DNTEXT '.')+ CLASSNAME;
PKG_NAME: DNTEXT ('.' DNTEXT)*;

ESCAPED_QUOTE : '\\"';

// literals
bool: TRUE | FALSE;
number: MINUS? DIGIT+ ('B' | 'H')?;
STRING_LIT: '"' (ESCAPED_QUOTE | ~'"')* '"';
literal: bool | STRING_LIT | number;

block: BODY_OPEN statement* BODY_CLOSE | statement;
statement: methodCall | variableDeclaration | variableAssignment | returnStatement | conditional;

// conditionals
conditional: r_if block;
r_if: R_IF PARAM_OPEN expression PARAM_CLOSE;

compileTimeExpression: (literal | CONSTNAME) (operator (literal | CONSTNAME))?;
expression: value (operator value)?;
value: literal | VARNAME | CONSTNAME | arrayAccess;
operator: PLUS | MINUS | DIVIDE | MULTIPLY | MODULUS | EQUALS;
operatorAssign: operator ASSIGN value;

variableDeclaration: typedVariable (SEPARATOR VARNAME)* (ASSIGN expression)? STATEMENT_END;
variableAssignment: VARNAME assignment STATEMENT_END;
assignment: (ASSIGN expression) | operatorAssign;
typedVariable: type VARNAME;
arrayAccess: VARNAME BRACE_OPEN expression BRACE_CLOSE;

// method calls
methodCall: VARNAME parameterSet STATEMENT_END;
parameterSet: PARAM_OPEN expression? (SEPARATOR expression)* PARAM_CLOSE;

// method definitions
methodDefinition: methodType type VARNAME parameterDefinition block;
methodType: (METHOD | FUNCTION)?;
parameterDefinition: PARAM_OPEN typedVariable? (SEPARATOR typedVariable)* PARAM_CLOSE;

returnStatement: RETURN expression STATEMENT_END;

type: basetype | CLASSNAME;
basetype: BOOLEAN | INT | STRING;

source: pkg? see* clazz;
pkg: PKG PKG_NAME STATEMENT_END;
see: SEE QUALIFIED_NAME STATEMENT_END;
clazz: CLASS CLASSNAME BODY_OPEN (constant | run | variableDeclaration | methodDefinition)* BODY_CLOSE;
constant: CONST CONSTNAME ASSIGN compileTimeExpression STATEMENT_END;
run: RUN block;
