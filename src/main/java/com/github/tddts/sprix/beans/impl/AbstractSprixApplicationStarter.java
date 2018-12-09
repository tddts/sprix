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

import com.github.tddts.sprix.beans.CommandLineArgsSource;
import com.github.tddts.sprix.beans.SprixApplicationStarter;
import com.github.tddts.sprix.beans.SprixViewHandler;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Basic abstract implementation of {@link SprixApplicationStarter} that can be inherited for extension.<br>
 * Using static fields as JavaFX creates new instance of {@link Application} on launch using {@link java.lang.reflect.Constructor#newInstance(Object...)}
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public abstract class AbstractSprixApplicationStarter extends Application implements SprixApplicationStarter {

  private final Log logger = LogFactory.getLog(getClass());

  private static volatile BeanFactory beanFactory;
  private static volatile boolean applicationStarted;

  @Override
  public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
    if (!applicationStarted) {
      launchProxy(getArgs());
      applicationStarted = true;
    }
  }

  private String[] getArgs() {
    String[] args = new String[]{};
    try {
      CommandLineArgsSource argsSource = beanFactory.getBean(CommandLineArgsSource.class);
      args = argsSource.getRawArguments();
    } catch (BeansException e) {
      if (logger.isWarnEnabled()) {
        logger.warn(e.getMessage());
        logger.warn("CommandLineArgsSource was not found. Returning empty arguments.");
      }
    }
    return args;
  }

  @Override
  public void start(Stage primaryStage) {
    setUp(primaryStage);
    SprixViewHandler viewHandler = beanFactory.getBean(SprixViewHandler.class);
    viewHandler.showView(primaryStage);
  }


  @Override
  public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    AbstractSprixApplicationStarter.beanFactory = beanFactory;
  }

  protected static BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public abstract void setUp(Stage primaryStage);

  public abstract void launchProxy(String[] args);
}
