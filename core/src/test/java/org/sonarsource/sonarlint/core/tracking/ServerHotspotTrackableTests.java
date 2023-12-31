/*
 * SonarLint Core - Implementation
 * Copyright (C) 2016-2023 SonarSource SA
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
package org.sonarsource.sonarlint.core.tracking;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;

import static org.assertj.core.api.Assertions.assertThat;

class ServerHotspotTrackableTests {

  @Test
  void should_delegate_fields_to_server_issue() {
    var creationDate = Instant.now();
    var textRange = new TextRangeWithHash(1, 2, 3, 4, "realHash");
    var trackable = new ServerHotspotTrackable(new ServerHotspot("key", "ruleKey", "message", "filePath", textRange, creationDate, HotspotReviewStatus.SAFE, VulnerabilityProbability.LOW, null));

    assertThat(trackable.getServerIssueKey()).isEqualTo("key");
    assertThat(trackable.getMessage()).isEqualTo("message");
    assertThat(trackable.getLineHash()).isNull();
    assertThat(trackable.getRuleKey()).isEqualTo("ruleKey");
    assertThat(trackable.isResolved()).isTrue();
    assertThat(trackable.getCreationDate()).isEqualTo(creationDate.toEpochMilli());
    assertThat(trackable.getSeverity()).isNull();
    assertThat(trackable.getLine()).isEqualTo(1);
    assertThat(trackable.getType()).isEqualTo(RuleType.SECURITY_HOTSPOT);
    assertThat(trackable.getTextRange()).isEqualTo(textRange);
    assertThat(trackable.getReviewStatus()).isEqualTo(HotspotReviewStatus.SAFE);
  }
}
