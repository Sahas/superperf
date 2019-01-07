package com.turvo.perf.grafana.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DashboardMeta {
  String type;
  Boolean canSave;
  Boolean canEdit;
  Boolean canStar;
  String slug;
  String expires;
  String created;
  String updated;
  String updatedBy;
  String createdBy;
  Integer version;
}
