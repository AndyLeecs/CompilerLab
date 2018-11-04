package Lexical;

/**
 * 词法单元
 */
public class Token {
    private String token_name;
    private String attribute_value;

    Token(String error)
    {
        this.attribute_value = error;
    }
    Token(String token_name, String attribute_value) {
        this.token_name = token_name;
        this.attribute_value = attribute_value;
    }

    @Override
    public String toString() {
        if(token_name != null && token_name.length() > 0)
        return "<" + token_name + "," + attribute_value + ">";
        else
            return attribute_value;
    }
}
