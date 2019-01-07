package com.turvo.perf.grafana.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DashboardTemplateListCurrent {
  String text;
  String value;
}
