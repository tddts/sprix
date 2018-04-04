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

import com.github.tddts.springfx.beans.CommandLineArgsSource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class SimpleArgsCommandLinePropertySource extends SimpleCommandLinePropertySource implements CommandLineArgsSource {

  private final String[] rawArguments;

  public SimpleArgsCommandLinePropertySource(String... args) {
    super(args);
    rawArguments = args;
  }

  public SimpleArgsCommandLinePropertySource(String name, String[] args) {
    super(name, args);
    rawArguments = args;
  }

  @Override
  public String[] getRawArguments() {
    return rawArguments;
  }
}