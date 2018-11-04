package Lexical;

public class TokenAndPos {
    private Token token;
    private int end;

    public TokenAndPos(String token_name, String attribute_value, int end)
    {
        this.token = new Token(token_name, attribute_value);
        this.end = end;
    }
    public Token getToken() {
        return token;
    }

    public int getEnd() {
        return end;
    }
}
