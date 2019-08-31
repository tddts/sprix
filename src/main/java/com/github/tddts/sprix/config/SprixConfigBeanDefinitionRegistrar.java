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

import com.github.tddts.sprix.beans.SprixApplicationStarter;
import com.github.tddts.sprix.beans.impl.MessageSourceResourceBundleProvider;
import com.github.tddts.sprix.beans.impl.SprixBeanHandlerImpl;
import com.github.tddts.sprix.beans.impl.SprixDialogProviderImpl;
import com.github.tddts.sprix.beans.impl.SprixViewHandlerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import static com.github.tddts.sprix.util.SprixConst.*;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * @author Tigran Dadaiants
 */
public class SprixConfigBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

  private final Log logger = LogFactory.getLog(SprixConfigBeanDefinitionRegistrar.class);

  @Override
  public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
    String annotationName = SprixConfig.class.getCanonicalName();

    if (!annotationMetadata.hasAnnotation(annotationName)) {
      if (logger.isInfoEnabled())
        logger.info(annotationName + " annotation not found in configuration.");
      return;
    }

    MultiValueMap<String, Object> attributes = annotationMetadata.getAllAnnotationAttributes(annotationName);

    BeanDefinitionBuilder viewHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(SprixViewHandlerImpl.class);
    viewHandlerBuilder.setRole(BeanDefinition.ROLE_APPLICATION);
    viewHandlerBuilder.addPropertyReference(BEAN_HANDLER, SPRIX_BEAN_HANDLER_BEAN_NAME);
    viewHandlerBuilder.addPropertyReference(RESOURCE_BUNDLE_PROVIDER, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);

    processViewHandlerAttributes(attributes, viewHandlerBuilder);
    processConnectedBeans(beanDefinitionRegistry, attributes);

    BeanDefinition definition = viewHandlerBuilder.getBeanDefinition();
    BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, SPRIX_VIEW_HANDLER_BEAN_NAME);
    BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanDefinitionRegistry);
  }

  private void processViewHandlerAttributes(MultiValueMap<String, Object> attributes, BeanDefinitionBuilder builder) {
    setProperty(builder, attributes, FILE);
    setProperty(builder, attributes, TITLE);
    setProperty(builder, attributes, WIDTH);
    setProperty(builder, attributes, HEIGHT);
  }

  private void setProperty(BeanDefinitionBuilder builder, MultiValueMap<String, Object> map, String name) {
    builder.addPropertyValue(name, map.getFirst(name));
  }

  private void processConnectedBeans(BeanDefinitionRegistry registry, MultiValueMap<String, Object> attributes) {
    // Bean Handler
    BeanDefinitionBuilder beanHandlerBuilder = genericBeanDefinition(SprixBeanHandlerImpl.class);
    registerBeanDef(registry, beanHandlerBuilder, SPRIX_BEAN_HANDLER_BEAN_NAME);

    // Dialog Provider
    BeanDefinitionBuilder dialogProviderBuilder = genericBeanDefinition(SprixDialogProviderImpl.class);
    dialogProviderBuilder.addPropertyReference(BEAN_HANDLER, SPRIX_BEAN_HANDLER_BEAN_NAME);
    dialogProviderBuilder.addPropertyReference(RESOURCE_BUNDLE_PROVIDER, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);
    registerBeanDef(registry, dialogProviderBuilder, SPRIX_DIALOG_PROVIDER_BEAN_NAME);

    // Resource bundle provider
    BeanDefinitionBuilder bundleProviderBuilder = genericBeanDefinition(MessageSourceResourceBundleProvider.class);
    bundleProviderBuilder.addPropertyReference(MESSAGE_SOURCE, (String) attributes.getFirst(MESSAGE_SOURCE));
    setProperty(bundleProviderBuilder, attributes, LOCALE);
    registerBeanDef(registry, bundleProviderBuilder, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);

    // Starter bean
    Class<?> starterClass = getStarterClass(attributes);
    BeanDefinitionBuilder defBuilder = genericBeanDefinition(starterClass);
    registerBeanDef(registry, defBuilder, SPRIX_APP_STARTER_BEAN_NAME);
  }

  private Class<?> getStarterClass(MultiValueMap<String, Object> attributes) {
    Class<?> starterClass = (Class<?>) attributes.getFirst(STARTER_CLASS);
    if (!SprixApplicationStarter.class.isAssignableFrom(starterClass)) {
      throw new BeanDefinitionStoreException("Given '" + STARTER_CLASS
          + "' is not a subtype of " + SprixApplicationStarter.class.getCanonicalName());
    }
    return starterClass;
  }

  private void registerBeanDef(BeanDefinitionRegistry registry, BeanDefinitionBuilder definitionBuilder, String beanName) {
    definitionBuilder.setRole(BeanDefinition.ROLE_APPLICATION);
    BeanDefinition definition = definitionBuilder.getBeanDefinition();
    registry.registerBeanDefinition(beanName, definition);
  }
}
