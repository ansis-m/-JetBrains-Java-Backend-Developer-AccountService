package account.securityConfig;

import account.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//Class with breached passwords//

public class pCheck {

    public static Set<String> breachedPasswords = new HashSet(Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));


    public static boolean isValid(String password) {
        return password.length() >= 12 && !breachedPasswords.contains(password);
    }

}
