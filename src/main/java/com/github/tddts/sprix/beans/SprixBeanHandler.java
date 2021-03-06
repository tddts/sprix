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

package com.github.tddts.sprix.beans;

/**
 * {@code SprixBeanHandler} provides functionality for wiring JavaFX objects to Spring context.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public interface SprixBeanHandler {

  /**
   * Wire given view's controller to Spring context.
   *
   * @param controller FXMl view controller
   */
  void wireController(Object controller);

  /**
   * Wire object to Spring context using class simple name as a bean's name.
   *
   * @param object object
   */
  void initBean(Object object);
}
