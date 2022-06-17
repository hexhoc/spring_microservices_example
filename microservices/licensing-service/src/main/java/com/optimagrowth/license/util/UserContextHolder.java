package com.optimagrowth.license.util;

import org.springframework.util.Assert;

// Stores UserContext in a static ThreadLocal variable
// When we work with threads, all the threads of a specific object share its
//variables, making these threads unsafe. The most common way to make them threadsafe in Java is to use synchronization. But if we want to avoid synchronization, we can
//also use ThreadLocal variables.
public class UserContextHolder {
    // We must be careful when we work directly with ThreadLocal. An incorrect development inside ThreadLocal
    // can lead to memory leaks in our application.
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    public static final UserContext getContext(){
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);

        }
        return userContext.get();
    }

    public static final void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static final UserContext createEmptyContext(){
        return new UserContext();
    }
}