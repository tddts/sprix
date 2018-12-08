package com.github.tddts.sprix.xsd;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Tigran Dadaiants dtkcommon@gmail.com
 */
public class SprixNamespaceHandler extends NamespaceHandlerSupport {


  @Override
  public void init() {
    registerBeanDefinitionParser("view", new SprixViewBeanDefinitionParser());
  }
}
