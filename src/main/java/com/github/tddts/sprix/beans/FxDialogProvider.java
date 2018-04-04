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

package com.github.tddts.sprix.beans;

import javafx.scene.control.Dialog;

import javax.annotation.PostConstruct;

import com.github.tddts.sprix.annotations.FxDialogInit;
import com.github.tddts.sprix.annotations.FxDialog;

/**
 * {@code FxDialogProvider} creates {@link Dialog} objects by processing classes with special annotations.
 * {@code FxDialogProvider} is capable of injecting dialog with FXML nodes similar to JavaFX controllers,
 * and also capable processing Dialog as a Spring bean (including injection of dependencies).
 * <p>
 * To create a Dialog via {@code FxDialogProvider} you should mark corresponding Dialog implementation with
 * {@link FxDialog} annotation and describe path to FXML file with dialog content.
 * FXMl file should have {@code fx:controller} property set to dialog class.
 * <p>
 * To initialize dialog crate a method with required parameters and mark by {@link FxDialogInit} annotation.
 * This initialization method will be invoked every time this dialog is called.
 * <p>
 * Such dialog would also support {@link PostConstruct} annotation as a Spring-processed bean.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public interface FxDialogProvider {

  /**
   * Create dialog of given type.
   *
   * @param type dialog class
   * @param <T>  dialog generic type
   * @return dialog of given type.
   */
  <T extends Dialog<?>> T getDialog(Class<T> type);

  /**
   * Create dialog of given type using given arguments for initialization.
   *
   * @param type dialog class
   * @param <T>  dialog generic type
   * @param args dialog initialization arguments
   * @return dialog of given type.
   */
  public <T extends Dialog<?>> T getDialog(Class<T> type, Object... args);
}
