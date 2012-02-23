package com.kaixindev.android.service;

import java.lang.annotation.*;

/**
 * This is the IntentHandler annotation, useful for register handlers.
 * @author Wilbur Luo
 * {@code
 * Class MyIntentHandler {
 * @IntentHandler(action="my_intent")
 * public void testIntent() {
 * }
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntentHandler {
    String action();
}

