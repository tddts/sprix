package com.github.tddts.sprix.xsd;

import com.github.tddts.sprix.beans.impl.MessageSourceResourceBundleProvider;
import com.github.tddts.sprix.beans.impl.SprixBeanHandlerImpl;
import com.github.tddts.sprix.beans.impl.SprixDialogProviderImpl;
import com.github.tddts.sprix.beans.impl.SprixViewHandlerImpl;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * @author Tigran Dadaiants dtkcommon@gmail.com
 */
public class SprixViewBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  private static final String FILE = "file";
  private static final String TITLE = "title";
  private static final String WIDTH = "width";
  private static final String HEIGHT = "height";
  private static final String LOCALE = "locale";
  private static final String STARTER = "starter";
  private static final String BEAN_HANDLER = "beanHandler";
  private static final String MESSAGE_SOURCE = "messageSource";
  private static final String RESOURCE_BUNDLE_PROVIDER = "resourceBundleProvider";


  private static final String SPRIX_BEAN_HANDLER_BEAN_NAME = "com.github.tddts.sprix.beans.internalSprixBeanHandler";
  private static final String SPRIX_DIALOG_PROVIDER_BEAN_NAME = "com.github.tddts.sprix.beans.impl.internalSprixDialogProviderImpl";
  private static final String RESOURCE_BUNDLE_PROVIDER_BEAN_NAME = "com.github.tddts.sprix.beans.impl.internalMessageSourceResourceBundleProvider";


  @Override
  protected Class<?> getBeanClass(Element element) {
    return SprixViewHandlerImpl.class;
  }


  @Override
  protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
    parseAttributes(element, builder);
    defineConnectedBeans(element, parserContext);
    addBeanReferences(builder);
  }

  private void addBeanReferences(BeanDefinitionBuilder builder) {
    builder.addPropertyReference(BEAN_HANDLER, SPRIX_BEAN_HANDLER_BEAN_NAME);
    builder.addPropertyReference(RESOURCE_BUNDLE_PROVIDER, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);
  }

  private void defineConnectedBeans(Element element, ParserContext parserContext) {
    BeanDefinitionRegistry registry = parserContext.getRegistry();

    registerBean(registry, SprixBeanHandlerImpl.class, SPRIX_BEAN_HANDLER_BEAN_NAME);
    registerBean(registry, SprixDialogProviderImpl.class, SPRIX_DIALOG_PROVIDER_BEAN_NAME);

    registerResourceBundleProvider(element, registry);
  }

  private void registerResourceBundleProvider(Element element, BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder defBuilder = genericBeanDefinition(MessageSourceResourceBundleProvider.class);

    String messageSource = element.getAttribute(MESSAGE_SOURCE);
    if (StringUtils.hasText(messageSource)) {
      defBuilder.addPropertyReference(MESSAGE_SOURCE, messageSource);
    }

    String locale = element.getAttribute(LOCALE);
    if (StringUtils.hasText(locale)) {
      defBuilder.addPropertyValue(LOCALE, locale);
    }

    registerBeanDef(registry, defBuilder, RESOURCE_BUNDLE_PROVIDER_BEAN_NAME);
  }

  private void registerBean(BeanDefinitionRegistry registry, Class<?> type, String name) {
    if (!registry.containsBeanDefinition(name)) {
      BeanDefinitionBuilder defBuilder = genericBeanDefinition(type);
      registerBeanDef(registry, defBuilder, name);
    }
  }

  private void registerBeanDef(BeanDefinitionRegistry registry, BeanDefinitionBuilder definitionBuilder, String beanName) {
    definitionBuilder.setRole(BeanDefinition.ROLE_APPLICATION);
    BeanDefinition definition = definitionBuilder.getBeanDefinition();
    registry.registerBeanDefinition(beanName, definition);
  }

  private void parseAttributes(Element element, BeanDefinitionBuilder builder) {
    String file = element.getAttribute(FILE);
    builder.addPropertyValue(FILE, file);

    String starter = element.getAttribute(STARTER);
    builder.addPropertyReference(STARTER, starter);

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
