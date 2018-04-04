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

package com.github.tddts.springfx.beans.impl;

import com.github.tddts.springfx.annotations.FxDialog;
import com.github.tddts.springfx.annotations.FxDialogInit;
import com.github.tddts.springfx.beans.FxDialogProvider;
import com.github.tddts.springfx.beans.FxBeanHandler;
import com.github.tddts.springfx.beans.ResourceBundleProvider;
import com.github.tddts.springfx.exception.FxDialogException;
import com.github.tddts.tools.core.util.ResourceUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FxDialogProviderImpl implements FxDialogProvider {

  private static final Object[] EMPTY_ARGS = new Object[]{};

  private Map<Class<?>, Dialog<?>> dialogCache = new HashMap<>();
  private Map<Class<?>, List<Method>> dialogInitCache = new HashMap<>();

  private FxBeanHandler beanHandler;
  private ResourceBundleProvider resourceBundleProvider;

  public FxDialogProviderImpl() {
  }

  @Override
  public <T extends Dialog<?>> T getDialog(Class<T> type) {
    return getDialog(type, EMPTY_ARGS);
  }

  @Override
  public <T extends Dialog<?>> T getDialog(Class<T> type, Object... args) {
    Dialog<?> dialog = dialogCache.get(type);
    dialog = dialog == null ? createDialog(type) : dialog;
    processInitMethods(type, dialog, args);
    return (T) dialog;
  }

  private <T extends Dialog<?>> T createDialog(Class<T> type) {

    if (!type.isAnnotationPresent(FxDialog.class)) {
      throw new FxDialogException("Dialog class should have a @FxDialog annotation!");
    }

    FxDialog dialogAnnotation = type.getDeclaredAnnotation(FxDialog.class);
    FXMLLoader loader = loadDialogView(dialogAnnotation);
    T dialog = loader.getController();
    setDialogContent(dialog, loader.getRoot(), dialogAnnotation);
    beanHandler.initBean(dialog);
    dialogCache.put(type, dialog);
    return dialog;

  }

  private void processInitMethods(Class<?> type, Dialog<?> dialog, Object[] args) {

    List<Method> initMethods = dialogInitCache.get(type);

    // Add dialog init methods to cache
    if (initMethods == null) {
      initMethods = new ArrayList<>();
      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(FxDialogInit.class)) initMethods.add(method);
      }
      dialogInitCache.put(type, initMethods);
    }

    // Invoke init methods
    try {
      for (Method method : initMethods) {
        method.setAccessible(true);
        if (method.getParameterCount() == 0) {
          method.invoke(dialog, EMPTY_ARGS);
        }
        else {
          method.invoke(dialog, args);
        }
      }

    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new FxDialogException("Could not initialize dialog!", e);
    }
  }

  private void setDialogContent(Dialog<?> dialog, Node root, FxDialog dialogAnnotation) {
    boolean expandable = dialogAnnotation.expandable();
    if (expandable) {
      dialog.getDialogPane().setExpandableContent(root);
    }
    else {
      dialog.getDialogPane().setContent(root);
    }
  }

  private FXMLLoader loadDialogView(FxDialog dialogAnnotation) {

    String filePath = dialogAnnotation.value();

    if (filePath.isEmpty()) {
      throw new FxDialogException("@FxDialog should contain path to FXML file!");
    }

    FXMLLoader loader = new FXMLLoader(ResourceUtil.getClasspathResourceURL(filePath), resourceBundleProvider.getResourceBundle());

    try {
      loader.load();
    } catch (IOException e) {
      throw new FxDialogException(e.getMessage(), e);
    }

    return loader;
  }

  public void setBeanHandler(FxBeanHandler beanHandler) {
    this.beanHandler = beanHandler;
  }

  public void setResourceBundleProvider(ResourceBundleProvider resourceBundleProvider) {
    this.resourceBundleProvider = resourceBundleProvider;
  }
}
