/*
 * Copyright 2019 Tigran Dadaiants
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tddts.sprix.config;

import com.github.tddts.sprix.beans.impl.SimpleSprixApplicationStarter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
import java.util.Locale;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({SprixConfigBeanDefinitionRegistrar.class})
public @interface SprixConfig {

  String file();

  Class<?> starterClass() default SimpleSprixApplicationStarter.class;

  String messageSource() default "messageSource";

  String locale() default "en_US";

  int width() default 800;

  int height() default 600;

  String title() default "";
}
