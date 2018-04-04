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

import com.github.tddts.springfx.beans.FxBeanHandler;
import com.github.tddts.springfx.beans.FxViewHandler;
import com.github.tddts.springfx.beans.ResourceBundleProvider;
import com.github.tddts.springfx.exception.FxViewException;
import com.github.tddts.tools.core.util.ResourceUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class FxViewHandlerImpl implements FxViewHandler {

  private String file;
  private String title;

  private int width = 800;
  private int height = 600;

  private FxBeanHandler beanHandler;
  private ResourceBundleProvider resourceBundleProvider;

  @Override
  public void showView(Stage stage) {

    FXMLLoader loader = new FXMLLoader(ResourceUtil.getClasspathResourceURL(file), resourceBundleProvider.getResourceBundle());

    try {
      loader.load();
    } catch (IOException e) {
      throw new FxViewException(e.getMessage(), e);
    }

    beanHandler.wireController(loader.getController());
    Scene scene = new Scene(loader.getRoot(), width, height);
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

  public void setFile(String file) {
    this.file = file;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setBeanHandler(FxBeanHandler beanHandler) {
    this.beanHandler = beanHandler;
  }

  public void setResourceBundleProvider(ResourceBundleProvider resourceBundleProvider) {
    this.resourceBundleProvider = resourceBundleProvider;
  }
}
