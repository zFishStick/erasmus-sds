package com.sds2.util;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

public class PasswordManager {

    private PasswordManager() {}

    public static String hashPassword(String password) {

        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B, 12);

        Hash hash = Password.hash(password)
                            .with(bcrypt);

        return hash.getResult();

    } 

    public static boolean verifyPassword(String password, String hashedPassword) {
        
        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B, 12);

        return Password.check(password, hashedPassword).with(bcrypt);

    }

    
}
