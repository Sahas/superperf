package com.turvo.perf.grafana.domain;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DashboardRow {
  Boolean collapse;
  String title;
  String titleSize;
  Integer height;
  List<DashboardPanel> panels;
}
