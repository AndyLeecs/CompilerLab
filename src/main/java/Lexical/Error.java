package Lexical;

/**
 * 错误信息
 */
public class Error {
    private String error;
    public Error(String error)
    {
        this.error = error;
    }

    @Override
    public String toString() {
        return error;
    }
}
