/*
 * SonarLint Core - Implementation
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonarsource.sonarlint.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.SonarLintWrappedException;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;
import org.sonarsource.sonarlint.core.container.standalone.StandaloneGlobalContainer;
import org.sonarsource.sonarlint.core.log.LoggingConfigurator;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StandaloneSonarLintEngineImpl implements StandaloneSonarLintEngine {

  private final StandaloneGlobalConfiguration globalConfig;
  private StandaloneGlobalContainer globalContainer;
  private final ReadWriteLock rwl = new ReentrantReadWriteLock();

  public StandaloneSonarLintEngineImpl(StandaloneGlobalConfiguration globalConfig) {
    this.globalConfig = globalConfig;
    LoggingConfigurator.init(globalConfig.isVerbose(), globalConfig.getLogOutput());
    start();
  }

  @Override
  public void setVerbose(boolean verbose) {
    rwl.writeLock().lock();
    try {
      LoggingConfigurator.setVerbose(verbose);
    } finally {
      rwl.writeLock().unlock();
    }
  }

  public StandaloneGlobalContainer getGlobalContainer() {
    return globalContainer;
  }

  public void start() {
    rwl.writeLock().lock();
    this.globalContainer = StandaloneGlobalContainer.create(globalConfig);
    try {
      globalContainer.startComponents();
    } catch (RuntimeException e) {
      throw SonarLintWrappedException.wrap(e);
    } finally {
      rwl.writeLock().unlock();
    }
  }

  @Override
  public RuleDetails getRuleDetails(String ruleKey) {
    rwl.readLock().lock();
    try {
      return globalContainer.getRuleDetails(ruleKey);
    } finally {
      rwl.readLock().unlock();
    }
  }

  @Override
  public AnalysisResults analyze(StandaloneAnalysisConfiguration configuration, IssueListener issueListener) {
    checkNotNull(configuration);
    checkNotNull(issueListener);
    rwl.readLock().lock();
    try {
      return globalContainer.analyze(configuration, issueListener);
    } catch (RuntimeException e) {
      throw SonarLintWrappedException.wrap(e);
    } finally {
      rwl.readLock().unlock();
    }
  }

  @Override
  public void stop() {
    rwl.writeLock().lock();
    try {
      if (globalContainer == null) {
        return;
      }
      globalContainer.stopComponents(false);
    } catch (RuntimeException e) {
      throw SonarLintWrappedException.wrap(e);
    } finally {
      this.globalContainer = null;
      rwl.writeLock().unlock();
    }
  }

}
