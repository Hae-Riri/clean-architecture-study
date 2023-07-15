package com.example.cleanarchitecturestudy.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 어노테이션이 어디에 쓰이는가. 타입 선언 시 사용한다. 클래스...?
@Retention(RetentionPolicy.RUNTIME) // 어노테이션이 실제로 적용되고 유지되는 범위. runtime으로 하면 컴파일 이후에도 jvm에서 참조 가능함. (CLASS는 컴파일러 참조까지, SOURCE는 컴파일 전까지만 유효)
@Documented
@Component
public @interface UseCase {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
