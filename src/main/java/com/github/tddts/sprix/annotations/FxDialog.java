/*
 * Copyright 2018 Tigran Dadaiants
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

package com.github.tddts.sprix.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking JavaFX dialog classes.
 * Contains path to corresponding FXML file.
 * Used to load JavaFX nodes from FXML and create dialog object.
 *
 * @author Tigran_Dadaiants@epam.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxDialog {

  /**
   * Path to FXML file.
   */
  String value();

  /**
   * Determines if loaded JavaFX nodes will be put under "expandable content" of dialog pane or not.
   */
  boolean expandable() default false;
}
