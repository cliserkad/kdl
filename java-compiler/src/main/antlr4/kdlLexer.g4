lexer grammar kdlLexer;

@header {
  package com.xarql.kdl.antlr;
}

// skip over comments in lexer {
COMMENT: '//' .*? '\n' -> channel(HIDDEN);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
// }

// skip over whitespace
WS: [ \t\r\n]+ -> skip;

// quotes {
QUOTE: '"';
STRING_LIT: QUOTE (~["\\] | '\\' .)* QUOTE;
// }

// keywords {
TYPE: 'type';
CONST: 'const';
MAIN: 'main';
RETURN: 'return';
USE: 'use';
PATH: 'path';
IF: 'if';
ELSE: 'else';
NULL: 'null';
ASSERT: 'assert';
WHILE: 'while';
FOR: 'for';
THIS: 'this';

// base types {
BOOLEAN: 'boolean';
BYTE: 'byte';
SHORT: 'short';
CHAR: 'char';
INT: 'int';
FLOAT: 'float';
LONG: 'long';
DOUBLE: 'double';
STRING: 'string';
// }

// boolean values {
TRUE: 'true';
FALSE: 'false';
// }
// }

// syntax {
CURL_L: '{';
CURL_R: '}';
PAREN_L: '(';
PAREN_R: ')';
BRACE_L: '[';
BRACE_R: ']';
DOT: '.';
COMMA: ',';
SEMICOLON: ';';
COLON: ':';
TILDE: '~';

// comparators {
NOT_EQUAL: '!=';
EQUAL: '=';
LESS_THAN: '<';
MORE_THAN: '>';
LESS_OR_EQUAL: '<=';
MORE_OR_EQUAL: '>=';
// }

// operators {
PLUS: '+';
MINUS: '-';
SLASH: '/';
MULTIPLY: '*';
MODULUS: '%';
// }

// bitwise {
BIT_AND: '&';
BIT_OR: '|';
// }

// appenders {
AND: '&&';
OR: '||';
// }

// }

DIGIT: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
UNDERSCORE: '_';

CHAR_LIT: '\'' . '\'';

// match anything that is unmatched and has no syntax characters
IDENTIFIER: ~([\r\t\n &|+<>=?!*.~:;,(){}'"/%]|'['|']')+;
