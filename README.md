# Compiladores # 

MicroCompilador - Semestre 01/2021

### Autores ###
 ```   
 Brenon Henrique 
 Frederico Terrinha
 Igor Sabarense
```



## Automato #

![automatofinalizado](https://user-images.githubusercontent.com/36987853/114118883-f7e3a980-98bf-11eb-9aaa-2df747cffc13.png)


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



