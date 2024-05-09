package dev.mayuna.sakuyabridge.commons.v2.networking.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IgnoreNetworkRegistration {

    boolean ignoreInnerClasses() default true;
}
