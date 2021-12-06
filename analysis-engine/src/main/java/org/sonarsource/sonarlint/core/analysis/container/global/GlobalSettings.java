/*
 * SonarLint Core - Analysis Engine
 * Copyright (C) 2016-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.sonar.api.config.PropertyDefinitions;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisEngineConfiguration;
import org.sonarsource.sonarlint.core.analysis.sonarapi.MapSettings;

public class GlobalSettings extends MapSettings {

  private static final String NODE_EXECUTABLE_PROPERTY = "sonar.nodejs.executable";

  public GlobalSettings(AnalysisEngineConfiguration config, PropertyDefinitions propertyDefinitions) {
    super(propertyDefinitions, compute(config));
  }

  private static Map<String, String> compute(AnalysisEngineConfiguration config) {
    Map<String, String> result = new HashMap<>(config.extraProperties());
    Path nodejsPath = config.getNodeJsPath();
    if (nodejsPath != null) {
      result.put(NODE_EXECUTABLE_PROPERTY, nodejsPath.toString());
    }
    return result;
  }

}