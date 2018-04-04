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

package com.github.tddts.sprix.postprocessor;

import com.github.tddts.sprix.annotations.Message;
import com.github.tddts.sprix.util.SpringUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * {@code MessageAnnotationBeanPostProcessor} is a {@link BeanPostProcessor} that inserts messages to fields and methods
 * of processed beans which are marked by {@link Message} annotation.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class MessageAnnotationBeanPostProcessor implements BeanPostProcessor, MessageSourceAware {

  private static final Object[] EMPTY_ARGS = new Object[]{};

  private final Logger logger = LogManager.getLogger(MessageAnnotationBeanPostProcessor.class);

  private MessageSource messageSource;
  private Locale locale = Locale.getDefault();

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

    Pair<Class<?>, Object> typeObjectPair = SpringUtil.checkForDinamicProxy(bean);
    Class<?> type = typeObjectPair.getLeft();
    Object target = typeObjectPair.getRight();

    Method[] methods = type.getDeclaredMethods();
    Field[] fields = type.getDeclaredFields();

    for (Method method : methods) {
      if (method.isAnnotationPresent(Message.class) && checkMethod(method, type)) {
        processMethod(target, method);
      }
    }

    for (Field field : fields) {
      if (field.isAnnotationPresent(Message.class) && checkField(field, type)) {
        processField(target, field);
      }
    }

    return bean;
  }

  private boolean checkMethod(Method method, Class<?> type) {
    if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String.class)) {
      return true;
    }
    logger.warn("Method [" + type + "." + method.getName() + "] is not populated with message. Method should have a single String parameter.");
    return false;
  }

  private boolean checkField(Field field, Class<?> type) {
    if (field.getType().equals(String.class)) {
      return true;
    }
    logger.warn("Field [" + type + "." + field.getName() + "] is not populated with message. Should be a String field.");
    return false;
  }

  private String preprocess(AccessibleObject accessibleObject) throws NoSuchMessageException {
    Message messageAnnotation = accessibleObject.getAnnotation(Message.class);
    String messageKey = messageAnnotation.value();
    String message = messageSource.getMessage(messageKey, EMPTY_ARGS, locale);
    accessibleObject.setAccessible(true);
    return message;
  }

  private void processMethod(Object bean, Method method) {
    try {
      String message = preprocess(method);
      method.invoke(bean, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new BeanInitializationException("Injection of message failed for method [" + bean.getClass() + "." + method.getName() + "]", e);
    } catch (NoSuchMessageException e) {
      throw new BeanInitializationException("Failed to find message for method [" + bean.getClass() + "." + method.getName() + "]", e);
    }
  }

  private void processField(Object bean, Field field) {
    try {
      String message = preprocess(field);
      field.set(bean, message);
    } catch (IllegalAccessException e) {
      throw new BeanInitializationException("Injection of message failed for field [" + bean.getClass() + "." + field.getName() + "]", e);
    } catch (NoSuchMessageException e) {
      throw new BeanInitializationException("Failed to find message for field [" + bean.getClass() + "." + field.getName() + "]", e);
    }
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public void setLocale(String tag) {
    Locale locale = Locale.forLanguageTag(tag);
    this.locale = locale == null ? Locale.getDefault() : locale;
  }
}
