package com.turvo.perf.grafana.domain;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DashboardTemplate {
  List<DashboardTemplateList> list;
}
