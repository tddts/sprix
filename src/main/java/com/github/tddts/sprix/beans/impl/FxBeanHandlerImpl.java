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

package com.github.tddts.sprix.beans.impl;

import com.github.tddts.sprix.beans.FxBeanHandler;
import com.github.tddts.sprix.exception.FxBeanException;
import javafx.fxml.FXML;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class FxBeanHandlerImpl implements FxBeanHandler, BeanFactoryAware {

  private String controllerPattern = "\\w+Controller";
  private AutowireCapableBeanFactory beanFactory;

  public FxBeanHandlerImpl() {
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (!(beanFactory instanceof AutowireCapableBeanFactory)) {
      throw new BeanInitializationException("Bean factory should be of type [" + AutowireCapableBeanFactory.class.getCanonicalName() + "] !");
    }
    this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
  }


  public void wireController(Object controller) {
    if (controller == null) return;
    initBean(controller);
    wireNestedControllers(controller);
  }

  public void initBean(Object object) {
    beanFactory.autowireBean(object);
    beanFactory.initializeBean(object, object.getClass().getSimpleName());
  }

  private void wireNestedControllers(Object controller) {
    Class<?> type = controller.getClass();
    List<Object> controllers = new ArrayList<>();
    // Find injected controller fields
    try {
      for (Field field : type.getDeclaredFields()) {
        if (field.isAnnotationPresent(FXML.class) && field.getName().matches(controllerPattern)) {
          field.setAccessible(true);
          controllers.add(field.get(controller));
        }
      }
    } catch (IllegalAccessException e) {
      throw new FxBeanException(e);
    }
    // Wire nested controllers
    for (Object nestedController : controllers) {
      initBean(nestedController);
      wireNestedControllers(nestedController);
    }
  }

  public void setControllerPattern(String controllerPattern) {
    this.controllerPattern = controllerPattern;
  }
}
