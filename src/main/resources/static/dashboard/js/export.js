// Data export utilities
const DataExporter = {
  // Export formats
  formats: {
    JSON: "json",
    CSV: "csv",
    XML: "xml",
    EXCEL: "xlsx",
  },

  // Export routes data
  exportRoutes(routes, format = "json") {
    const data = {
      timestamp: new Date().toISOString(),
      routes: routes.map((route) => ({
        id: route.id,
        predicate: route.predicate,
        uri: route.uri,
        filters: route.filters,
        order: route.order,
        isAuthorized: route.isAuthorized,
      })),
    }

    return this.exportData(data, format, "gateway-routes")
  },

  // Export filters data
  exportFilters(globalFilters, routeFilters, format = "json") {
    const data = {
      timestamp: new Date().toISOString(),
      globalFilters: globalFilters,
      routeFilters: routeFilters,
    }

    return this.exportData(data, format, "gateway-filters")
  },

  // Export metrics data
  exportMetrics(metrics, format = "json") {
    const data = {
      timestamp: new Date().toISOString(),
      metrics: Object.entries(metrics).map(([name, metric]) => ({
        name,
        baseUnit: metric.baseUnit,
        measurements: metric.measurements,
      })),
    }

    return this.exportData(data, format, "gateway-metrics")
  },

  // Export health data
  exportHealth(health, format = "json") {
    const data = {
      timestamp: new Date().toISOString(),
      health: health,
    }

    return this.exportData(data, format, "gateway-health")
  },

  // Generic export function
  exportData(data, format, filename) {
    let content, mimeType, extension

    switch (format) {
      case this.formats.JSON:
        content = JSON.stringify(data, null, 2)
        mimeType = "application/json"
        extension = "json"
        break

      case this.formats.CSV:
        content = this.convertToCSV(data)
        mimeType = "text/csv"
        extension = "csv"
        break

      case this.formats.XML:
        content = this.convertToXML(data)
        mimeType = "application/xml"
        extension = "xml"
        break

      default:
        throw new Error(`Unsupported format: ${format}`)
    }

    this.downloadFile(content, `${filename}.${extension}`, mimeType)
  },

  // Convert data to CSV format
  convertToCSV(data) {
    let csv = ""

    if (data.routes) {
      csv += "Routes\n"
      csv += "ID,Predicate,URI,Filters Count,Order,Authorized\n"
      data.routes.forEach((route) => {
        csv += `"${this.escapeCSV(route.id)}","${this.escapeCSV(
            route.predicate)}","${this.escapeCSV(
            route.uri)}",${route.filters.length},${route.order},${route.isAuthorized}\n`
      })
      csv += "\n"
    }

    if (data.metrics) {
      csv += "Metrics\n"
      csv += "Name,Base Unit,Statistic,Value\n"
      data.metrics.forEach((metric) => {
        metric.measurements.forEach((measurement) => {
          csv += `"${this.escapeCSV(metric.name)}","${this.escapeCSV(
              metric.baseUnit
              || "")}","${measurement.statistic}",${measurement.value}\n`
        })
      })
      csv += "\n"
    }

    if (data.globalFilters) {
      csv += "Global Filters\n"
      csv += "Filter Name,Order\n"
      Object.entries(data.globalFilters).forEach(([name, order]) => {
        csv += `"${this.escapeCSV(name)}",${order}\n`
      })
      csv += "\n"
    }

    if (data.health) {
      csv += "Health Components\n"
      csv += "Component,Status,Description\n"
      if (data.health.components) {
        Object.entries(data.health.components).forEach(([name, component]) => {
          csv += `"${this.escapeCSV(
              name)}","${component.status}","${this.escapeCSV(
              component.description || "")}"\n`
        })
      }
    }

    return csv
  },

  // Convert data to XML format
  convertToXML(data) {
    let xml = '<?xml version="1.0" encoding="UTF-8"?>\n'
    xml += `<gateway-data timestamp="${data.timestamp}">\n`

    if (data.routes) {
      xml += "  <routes>\n"
      data.routes.forEach((route) => {
        xml += `    <route id="${this.escapeXML(
            route.id)}" uri="${this.escapeXML(
            route.uri)}" order="${route.order}" authorized="${route.isAuthorized}">\n`
        xml += `      <predicate>${this.escapeXML(
            route.predicate)}</predicate>\n`
        xml += "      <filters>\n"
        route.filters.forEach((filter) => {
          xml += `        <filter>${this.escapeXML(filter)}</filter>\n`
        })
        xml += "      </filters>\n"
        xml += "    </route>\n"
      })
      xml += "  </routes>\n"
    }

    if (data.metrics) {
      xml += "  <metrics>\n"
      data.metrics.forEach((metric) => {
        xml += `    <metric name="${this.escapeXML(
            metric.name)}" baseUnit="${this.escapeXML(
            metric.baseUnit || "")}">\n`
        xml += "      <measurements>\n"
        metric.measurements.forEach((measurement) => {
          xml += `        <measurement statistic="${measurement.statistic}" value="${measurement.value}"/>\n`
        })
        xml += "      </measurements>\n"
        xml += "    </metric>\n"
      })
      xml += "  </metrics>\n"
    }

    if (data.globalFilters) {
      xml += "  <globalFilters>\n"
      Object.entries(data.globalFilters).forEach(([name, order]) => {
        xml += `    <filter name="${this.escapeXML(name)}" order="${order}"/>\n`
      })
      xml += "  </globalFilters>\n"
    }

    if (data.health) {
      xml += "  <health>\n"
      xml += `    <status>${data.health.status}</status>\n`
      if (data.health.components) {
        xml += "    <components>\n"
        Object.entries(data.health.components).forEach(([name, component]) => {
          xml += `      <component name="${this.escapeXML(
              name)}" status="${component.status}">\n`
          if (component.description) {
            xml += `        <description>${this.escapeXML(
                component.description)}</description>\n`
          }
          if (component.details) {
            xml += "        <details>\n"
            Object.entries(component.details).forEach(([key, value]) => {
              xml += `          <detail key="${this.escapeXML(
                  key)}" value="${this.escapeXML(String(value))}"/>\n`
            })
            xml += "        </details>\n"
          }
          xml += "      </component>\n"
        })
        xml += "    </components>\n"
      }
      xml += "  </health>\n"
    }

    xml += "</gateway-data>"
    return xml
  },

  // Escape CSV values
  escapeCSV(value) {
    if (typeof value !== "string") {
      return value
    }
    return value.replace(/"/g, '""')
  },

  // Escape XML values
  escapeXML(value) {
    if (typeof value !== "string") {
      return value
    }
    return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&apos;")
  },

  // Download file
  downloadFile(content, filename, mimeType) {
    const blob = new Blob([content], {type: mimeType})
    const url = URL.createObjectURL(blob)

    const link = document.createElement("a")
    link.href = url
    link.download = filename
    link.style.display = "none"

    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    URL.revokeObjectURL(url)
  },

  // Generate report
  generateReport(data, format = "json") {
    const report = {
      timestamp: new Date().toISOString(),
      summary: {
        totalRoutes: data.routes?.length || 0,
        totalFilters: Object.keys(data.globalFilters || {}).length,
        healthStatus: data.health?.status || "UNKNOWN",
        systemStatus: this.getSystemStatus(data.metrics),
      },
      routes: data.routes,
      filters: {
        global: data.globalFilters,
        route: data.routeFilters,
      },
      metrics: data.metrics,
      health: data.health,
    }

    return this.exportData(report, format, "gateway-report")
  },

  // Get system status from metrics
  getSystemStatus(metrics) {
    if (!metrics) {
      return "UNKNOWN"
    }

    const cpuMetric = metrics["system.cpu.usage"]
    const memoryUsed = metrics["jvm.memory.used"]
    const memoryMax = metrics["jvm.memory.max"]

    const status = {
      cpu: "UNKNOWN",
      memory: "UNKNOWN",
      overall: "UNKNOWN",
    }

    if (cpuMetric) {
      const cpuUsage = cpuMetric.measurements?.[0]?.value || 0
      status.cpu = cpuUsage > 0.8 ? "HIGH" : cpuUsage > 0.5 ? "MEDIUM" : "LOW"
    }

    if (memoryUsed && memoryMax) {
      const used = memoryUsed.measurements?.[0]?.value || 0
      const max = memoryMax.measurements?.[0]?.value || 0
      const usage = max > 0 ? used / max : 0
      status.memory = usage > 0.8 ? "HIGH" : usage > 0.5 ? "MEDIUM" : "LOW"
    }

    // Determine overall status
    if (status.cpu === "HIGH" || status.memory === "HIGH") {
      status.overall = "HIGH"
    } else if (status.cpu === "MEDIUM" || status.memory === "MEDIUM") {
      status.overall = "MEDIUM"
    } else if (status.cpu === "LOW" && status.memory === "LOW") {
      status.overall = "LOW"
    }

    return status
  },
}

// Export for use in other modules
if (typeof module !== "undefined" && module.exports) {
  module.exports = DataExporter
}
