# Compiladores # 

## Gramática #
```
D -> DECLARACAO
B -> BLOCO DE COMANDOS
C -> COMANDO
MAIN -> MAIN
T -> TIPO
V -> CONSTANTE
EXP -> EXPRESSAO

S -> {D} main B

B ->  "{" {C} "}"

D -> T id({,id} | = V | ["[const | const_hexa]"]); | final id = V;
T -> int | char | boolean
V -> [-] const | const_hexa | (TRUE | FALSE)
C -> ; |
     id["["EXP"]"] := EXP |
     for "(" {F} ; EXP ; {F} ")" ( C | B ) |
     if "(" EXP ")" then (C|B) [else (C|B)] |
     readln"("id[EXP]"]")" |
     write"("EXP{,EXP}")" |
     writeln"("EXP{,EXP}")"

F -> C[,C]
EXP -> EXPS [(= | <> | < | > | <= | >= ) EXPS]
EXPS -> [+|-] TS { + | - | or ) TS}
TS -> FS {(* | / | % | and ) FS}
FS -> not FS | "("EXP")" | V | id["["EXP"]"]
```



