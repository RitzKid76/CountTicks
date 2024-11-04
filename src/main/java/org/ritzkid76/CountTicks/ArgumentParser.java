package org.ritzkid76.CountTicks;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.ritzkid76.CountTicks.TabCompletion.SyntaxHandler;

// @FunctionalInterface
// interface Executor {
//     boolean execute(String argumentString);
// }
public class ArgumentParser {
    private SyntaxHandler syntaxHandler;
    public ArgumentParser(SyntaxHandler handler) { syntaxHandler = handler; }

    public boolean run(String[] args) {
        if(!syntaxHandler.isValidSyntax(args)) return false;

        String methodName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        try {
            Method method = getClass().getDeclaredMethod(methodName, String[].class);
            return (boolean) method.invoke(this, (Object) args);
        } catch(Exception e) { 
            Debug.log(methodName, "command missing from " + getClass());
            return false; 
        }
    }

    private boolean scan(String[] args) {
        Debug.log("scan");
        return true;
    }
    
    private boolean inspector(String[] args) {
        Debug.log("inspector");
        return true;
    }
    
    private boolean set_region(String[] args) {
        Debug.log("region");
        return true;
    }
}