package com.turvo.perf.grafana.domain;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DashboardTemplateList {
  DashboardTemplateListCurrent current;
  Integer hide;
  Boolean includeAll;
  String label;
  Boolean multi;
  String name;
  String query;
  String type;
}
