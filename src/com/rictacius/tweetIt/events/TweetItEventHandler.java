package com.rictacius.tweetIt.events;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TweetItEventHandler {

	TweetItEvent.EventType event();

	Priority priority() default Priority.NORMAL;
}
