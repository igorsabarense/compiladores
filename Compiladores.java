import java.util.HashMap;
import java.util.Map;

public class Compiladores {
    public static void main(String []args){
       System.out.println("output");
    }
}





enum TokenID {

    CONST("const"),
    VAR("var"),
    INTEGER("int"),
    CHAR("char"),
    FOR("for"),
    IF("if"),
    ELSE("else"),
    AND("and"),
    OR("or"),
    NOT("not"),
    EQUAL ("="),
    TO ("to"),
    COLCHETE_A ("["),
    COLCHETE_F ("]"),
    PARENTESES_A ("("),
    PARENTESES_F (")"),
    CHAVE_A ("{"),
    CHAVE_F ("}"),
    MENOR ("<"),
    MAIOR (">"),
    DIFERENTE ("!="),
    VIRGULA (","),
    MAIS ("+"),
    MENOS ("-"),
    ASTERISCO ("*"),
    BARRA ("/"),
    PONTO_E_VIRGULA (";"),
    THEN ("then"),
    READLN ("readln"),
    STEP ("step"),
    WRITE ("write"),
    WRITELN ("writeln"),
    PORCENTAGEM ("%"),
    MAIOR_IGUAL (">="),
    MENOR_IGUAL ("<="),
    DO ("do"),
    ID ("id");

    private String descricao;

    TokenID(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

class Token {

}

class TSimbolos { 

    
}



