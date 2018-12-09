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

package com.github.tddts.sprix.xsd;

import com.github.tddts.sprix.beans.SprixApplicationStarter;
import com.github.tddts.sprix.beans.impl.*;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * @author Tigran Dadaiants dtkcommon@gmail.com
 */
public class SprixViewBeanDefinitionParser implements BeanDefinitionParser {

  private static final String FILE = "file";
  private static final String TITLE = "title";
  private static final String WIDTH = "width";
  private static final String HEIGHT = "height";
  private static final String LOCALE = "locale";
  private static final String STARTER_CLASS = "starterClass";
  private static final String BEAN_HANDLER = "beanHandler";
  private static final String MESSAGE_SOURCE = "messageSource";
  private static final String RESOURCE_BUNDLE_PROVIDER = "resourceBundleProvider";

  private static final String SPRIX_VIEW_HANDLER_BEAN_NAME = "com.github.tddts.sprix.beans.internalSprixViewHandler";
  private static final String SPRIX_APP_STARTER_BEAN_NAME = "com.github.tddts.sprix.beans.internalSprixApplicationStarter";
  private static final String SPRIX_BEAN_HANDLER_BEAN_NAME = "com.github.tddts.sprix.beans.internalSprixBeanHandler";
  private static final String SPRIX_DIALOG_PROVIDER_BEAN_NAME = "com.github.tddts.sprix.beans.internalSprixDialogProvider";
  private static final String RESOURCE_BUNDLE_PROVIDER_BEAN_NAME = "com.github.tddts.sprix.beans.internalResourceBundleProvider";

  @Override
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    Object source = parserContext.extractSource(element);
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SprixViewHandlerImpl.class);
    builder.setRole(BeanDefinition.ROLE_APPLICATION);
    builder.getRawBeanDefinition().setSource(source);

    doParse(element, parserContext, source, builder);

    BeanDefinition definition = builder.getBeanDefinition();
    BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, SPRIX_VIEW_HANDLER_BEAN_NAME);
    BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

    return definition;
  }

  protected void doParse(Element element, ParserContext parserContext, Object source, BeanDefinitionBuilder builder) {
    parseAttributes(element, builder);
    defineConnectedBeans(element, parserContext, source);
    addBeanReferences(builder);
  }

  private void addBeanReferences(BeanDefinitionBuilder builder) {
    builder.addPropertyReference(BEAN_HANDLER, SPRIX_BEAN_HANDLER_BEAN_NAME);
    builder.addPropertyReference(RESOURCE_BUNDLE_PROVIDER, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);
  }

  private void defineConnectedBeans(Element element, ParserContext parserContext,  Object source) {
    BeanDefinitionRegistry registry = parserContext.getRegistry();

    registerBean(registry, source, SprixBeanHandlerImpl.class, SPRIX_BEAN_HANDLER_BEAN_NAME);
    registerBean(registry, source, SprixDialogProviderImpl.class, SPRIX_DIALOG_PROVIDER_BEAN_NAME);

    registerResourceBundleProvider(element, registry, source);
    registerAppStarter(element, registry, source);
  }

  private void registerAppStarter(Element element, BeanDefinitionRegistry registry, Object source) {
    Class<?> starterClass;

    try {
      String starter = element.getAttribute(STARTER_CLASS);
      if(StringUtils.hasText(starter)){
        starterClass = getStarterClass(starter);
      } else{
        starterClass = SimpleSprixApplicationStarter.class;
      }

      BeanDefinitionBuilder defBuilder = genericBeanDefinition(starterClass);
      registerBeanDef(registry, source, defBuilder, SPRIX_APP_STARTER_BEAN_NAME);
    } catch (ClassNotFoundException e) {
      throw new BeanDefinitionStoreException(e.getMessage(), e);
    }
  }

  private Class<?> getStarterClass(String name) throws ClassNotFoundException {
    Class<?> starterClass = Class.forName(name);

    if (!SprixApplicationStarter.class.isAssignableFrom(starterClass)) {
      throw new BeanDefinitionStoreException("Given '" + STARTER_CLASS
              + "' is not a subtype of " + SprixApplicationStarter.class.getCanonicalName());
    }

    return starterClass;
  }

  private void registerResourceBundleProvider(Element element, BeanDefinitionRegistry registry, Object source) {
    BeanDefinitionBuilder defBuilder = genericBeanDefinition(MessageSourceResourceBundleProvider.class);

    String messageSource = element.getAttribute(MESSAGE_SOURCE);
    if (StringUtils.hasText(messageSource)) {
      defBuilder.addPropertyReference(MESSAGE_SOURCE, messageSource);
    }

    String locale = element.getAttribute(LOCALE);
    if (StringUtils.hasText(locale)) {
      defBuilder.addPropertyValue(LOCALE, locale);
    }

    registerBeanDef(registry,source,  defBuilder, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);
  }

  private void registerBean(BeanDefinitionRegistry registry, Object source, Class<?> type, String name) {
    if (!registry.containsBeanDefinition(name)) {
      BeanDefinitionBuilder defBuilder = genericBeanDefinition(type);
      registerBeanDef(registry, source, defBuilder, name);
    }
  }

  private void registerBeanDef(BeanDefinitionRegistry registry, Object source,
                               BeanDefinitionBuilder definitionBuilder, String beanName) {
    definitionBuilder.setRole(BeanDefinition.ROLE_APPLICATION);
    definitionBuilder.getRawBeanDefinition().setSource(source);
    BeanDefinition definition = definitionBuilder.getBeanDefinition();
    registry.registerBeanDefinition(beanName, definition);
  }

  private void parseAttributes(Element element, BeanDefinitionBuilder builder) {
    String file = element.getAttribute(FILE);
    builder.addPropertyValue(FILE, file);

    String title = element.getAttribute(TITLE);
    if (title != null) {
      builder.addPropertyValue(TITLE, title);
    }

    String width = element.getAttribute(WIDTH);
    if (StringUtils.hasText(width)) {
      builder.addPropertyValue(WIDTH, Integer.parseInt(width));
    }

    String height = element.getAttribute(HEIGHT);
    if (StringUtils.hasText(height)) {
      builder.addPropertyValue(HEIGHT, Integer.parseInt(height));
    }
  }
}
