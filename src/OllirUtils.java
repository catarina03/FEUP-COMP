public final class OllirUtils {

    private OllirUtils(){};

    public static String getType(String type) {
        switch (type) {
            case "int":
                return "i32";
            case "boolean":
                return "bool";
            case "void":
                return "V";
            case "int[]":
                return "array.i32";
            case "String[]":
                return "array.String";
            default:
                return type;
        }
    }
}
