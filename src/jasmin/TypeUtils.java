package jasmin;

import org.specs.comp.ollir.*;

import java.util.ArrayList;
import java.util.List;

public final class TypeUtils {
    private TypeUtils(){}

    public static String parseField(Field field) {
        var fieldType = parseType(field.getFieldType());

        var name = field.getFieldName().equals("field") ? "'field'" : field.getFieldName();

        return String.format("\n.field private %s %s\n", name, fieldType);
    }

    public static String parseType(Type type) {
        var resType = "";

        if (type instanceof ClassType)
            resType = String.format("L%s;", ((ClassType) type).getName());
        else if (type instanceof ArrayType)
            resType = String.format("[%s", parseElementType(((ArrayType) type).getTypeOfElements()));
        else
            resType = parseElementType(type.getTypeOfElement());

        return resType;
    }

    public static String parseElementType(ElementType type) {
        switch (type) {
            case INT32:
                return "I";
            case BOOLEAN:
                return "Z";
            case STRING:
                return "Ljava/lang/String;";
            case VOID:
                return "V";
            case OBJECTREF:
                return "";
            default:
                return type + "NOT IMPLEMENTED";
        }
    }


}
