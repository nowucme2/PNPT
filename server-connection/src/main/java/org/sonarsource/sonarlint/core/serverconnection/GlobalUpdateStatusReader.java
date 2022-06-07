/*
 * SonarLint Core - Server Connection
 * Copyright (C) 2016-2022 SonarSource SA
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
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Date;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths;
import org.sonarsource.sonarlint.core.serverconnection.storage.ServerInfoStore;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageStatusStore;

public class GlobalUpdateStatusReader {
  private final ServerInfoStore serverInfoStore;
  private final StorageStatusStore storageStatusStore;

  public GlobalUpdateStatusReader(ServerInfoStore serverInfoStore, StorageStatusStore storageStatusStore) {
    this.serverInfoStore = serverInfoStore;
    this.storageStatusStore = storageStatusStore;
  }

  public GlobalStorageStatus read() {
    if (storageStatusStore.exists()) {
      final var currentStorageStatus = storageStatusStore.getAll();
      final var stale = !currentStorageStatus.getStorageVersion().equals(ProjectStoragePaths.STORAGE_VERSION);

      String version = null;
      if (!stale) {
        final var serverInfoFromStorage = serverInfoStore.getAll();
        version = serverInfoFromStorage.getVersion();
      }

      return new GlobalStorageStatus(version, new Date(currentStorageStatus.getUpdateTimestamp()), stale);
    }
    return null;
  }
}