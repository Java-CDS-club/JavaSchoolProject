package cz.muni.fi.pv168.project.data.validation;

import java.util.regex.Pattern;

public class CustomerValidator {

    private static final Pattern dicPattern = Pattern.compile("\\d+");
    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern phonePattern = Pattern.compile("^\\s?((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?\\s?");

    public static boolean validate(
            String dic,
            String name,
            String phone,
            String mail
    ) {
        if (name.isEmpty())
            return false;
        if (dic.isEmpty() || !dicPattern.matcher(dic).matches())
            return false;
        return (
                (phone.isEmpty() && emailPattern.matcher(mail).matches()) ||
                (mail.isEmpty() && phonePattern.matcher(phone).matches()) ||
                (phonePattern.matcher(phone).matches() && emailPattern.matcher(mail).matches())
        );
    }
}
