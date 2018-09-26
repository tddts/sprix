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

import com.github.tddts.sprix.annotations.FxDialog;
import com.github.tddts.sprix.beans.FxBeanHandler;
import com.github.tddts.sprix.beans.FxDialogProvider;
import com.github.tddts.sprix.beans.ResourceBundleProvider;
import com.github.tddts.sprix.exception.FxDialogException;
import com.github.tddts.tools.core.util.ResourceUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FxDialogProviderImpl implements FxDialogProvider {

  private Map<Class<?>, Dialog<?>> dialogCache = new HashMap<>();

  private FxBeanHandler beanHandler;
  private ResourceBundleProvider resourceBundleProvider;

  @Override
  public <T extends Dialog<?>> T getDialog(Class<T> type) {
    Dialog<?> dialog = dialogCache.get(type);
    dialog = dialog == null ? createDialog(type) : dialog;
    return (T) dialog;
  }

  private <T extends Dialog<?>> T createDialog(Class<T> type) {

    if (!type.isAnnotationPresent(FxDialog.class))
      throw new FxDialogException("Dialog class should have a @FxDialog annotation!");

    FxDialog dialogAnnotation = type.getDeclaredAnnotation(FxDialog.class);
    FXMLLoader loader = loadDialogView(dialogAnnotation);
    T dialog = loader.getController();
    setDialogContent(dialog, loader.getRoot(), dialogAnnotation);
    beanHandler.initBean(dialog);

    if (dialogAnnotation.cached())
      dialogCache.put(type, dialog);

    return dialog;
  }

  private void setDialogContent(Dialog<?> dialog, Node root, FxDialog dialogAnnotation) {
    boolean expandable = dialogAnnotation.expandable();
    DialogPane dialogPane = dialog.getDialogPane();

    if (expandable)
      dialogPane.setExpandableContent(root);
    else
      dialogPane.setContent(root);
  }

  private FXMLLoader loadDialogView(FxDialog dialogAnnotation) {

    String filePath = dialogAnnotation.value();

    if (filePath.isEmpty())
      throw new FxDialogException("@FxDialog should contain path to FXML file!");

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
