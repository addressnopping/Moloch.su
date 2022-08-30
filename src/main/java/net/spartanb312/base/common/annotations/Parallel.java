package net.spartanb312.base.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parallel {
    boolean loadable() default true;

    boolean runnable() default false;
}
