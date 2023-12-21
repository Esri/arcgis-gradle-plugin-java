/*
 * Copyright 2023 Esri.
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
package com.esri;

import org.gradle.api.provider.Property;

/**
 * Extension to {@link ArcGISJavaGradlePlugin}.
 */
public abstract class ArcGISJavaGradlePluginExtension {
  /**
   * Gets the version property for the ArcGIS Maps SDK for Java plugin. This property must be set by the user of the
   * plugin.
   *
   * @return the version property
   */
  public abstract Property<String> getVersion();
}
