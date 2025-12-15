// Configuration
const CONFIG = {
  BASE_URL: "/api/actuator",
  REFRESH_INTERVAL: 5000,
  MAX_CHART_POINTS: 50,
  CHART_COLORS: {
    primary: "#3b82f6",
    success: "#10b981",
    warning: "#f59e0b",
    danger: "#ef4444",
    purple: "#8b5cf6",
  },
}

// Global state
const state = {
  isConnected: false,
  isPaused: false,
  currentView: "dashboard",
  refreshTimer: null,
  data: {
    routes: [],
    routeDefinitions: [],
    globalFilters: {},
    routeFilters: {},
    routePredicates: {},
    metrics: {},
    health: {},
    metricHistory: new Map(),
  },
}

// Utility functions
const $ = (selector) => document.querySelector(selector)
const $$ = (selector) => document.querySelectorAll(selector)

const formatBytes = (bytes) => {
  if (bytes === 0) {
    return "0 B"
  }
  const k = 1024
  const sizes = ["B", "KB", "MB", "GB", "TB"]
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i]
}

const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + "M"
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + "K"
  }
  return num.toString()
}

const formatDuration = (seconds) => {
  if (seconds < 60) {
    return `${seconds.toFixed(2)}s`
  }
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}m ${remainingSeconds.toFixed(0)}s`
}

const escapeHtml = (text) => {
  const div = document.createElement("div")
  div.textContent = text
  return div.innerHTML
}

// API functions
const api = {
  async get(endpoint) {
    try {
      const response = await fetch(`${CONFIG.BASE_URL}${endpoint}`)
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      return await response.json()
    } catch (error) {
      console.error(`API Error (${endpoint}):`, error)
      throw error
    }
  },

  async getMetrics() {
    return await this.get("/metrics")
  },

  async getMetric(name) {
    return await this.get(`/metrics/${encodeURIComponent(name)}`)
  },

  async getHealth() {
    return await this.get("/health")
  },

  async getRoutes() {
    return await this.get("/gateway/routes")
  },

  async getRouteDefinitions() {
    return await this.get("/gateway/routedefinitions")
  },

  async getGlobalFilters() {
    return await this.get("/gateway/globalfilters")
  },

  async getRouteFilters() {
    return await this.get("/gateway/routefilters")
  },

  async getRoutePredicates() {
    return await this.get("/gateway/routepredicates")
  },

  async getLoggers() {
    return await this.get("/loggers")
  },

  async getLogger(name) {
    return await this.get(`/loggers/${encodeURIComponent(name)}`)
  },

  async setLogLevel(name, level) {
    const response = await fetch(
        `${CONFIG.BASE_URL}/loggers/${encodeURIComponent(name)}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({configuredLevel: level}),
        })
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }
    return response.ok
  },

  async getFeatures() {
    return await this.get("/features")
  },

  async getThreadDump() {
    return await this.get("/threaddump")
  },

  async getHeapDump() {
    const response = await fetch(`${CONFIG.BASE_URL}/heapdump`)
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }
    return response.blob()
  },

  async getEnv() {
    return await this.get("/env")
  },

  async getEnvProperty(name) {
    return await this.get(`/env/${encodeURIComponent(name)}`)
  },

  async getConfigProps() {
    return await this.get("/configprops")
  },

  async getConditions() {
    return await this.get("/conditions")
  },

  async getBeans() {
    return await this.get("/beans")
  },
}

// Chart utilities
const chartUtils = {
  createChart(canvas, type = "line") {
    const ctx = canvas.getContext("2d")
    const dpr = window.devicePixelRatio || 1
    const rect = canvas.getBoundingClientRect()

    canvas.width = rect.width * dpr
    canvas.height = rect.height * dpr
    ctx.scale(dpr, dpr)

    return ctx
  },

  drawLineChart(ctx, data, options = {}) {
    const {width, height} = ctx.canvas
    const actualWidth = width / (window.devicePixelRatio || 1)
    const actualHeight = height / (window.devicePixelRatio || 1)

    ctx.clearRect(0, 0, actualWidth, actualHeight)

    // Draw grid
    ctx.strokeStyle = "#e5e7eb"
    ctx.lineWidth = 1
    ctx.beginPath()
    for (let i = 0; i <= 5; i++) {
      const y = (actualHeight / 5) * i
      ctx.moveTo(0, y)
      ctx.lineTo(actualWidth, y)
    }
    ctx.stroke()

    if (!data || data.length === 0) {
      return
    }

    const max = Math.max(...data, 1)
    const min = Math.min(...data, 0)
    const range = max - min || 1

    // Draw line
    ctx.strokeStyle = options.color || CONFIG.CHART_COLORS.primary
    ctx.lineWidth = 2
    ctx.beginPath()

    data.forEach((value, index) => {
      const x = (actualWidth / Math.max(data.length - 1, 1)) * index
      const y = actualHeight - ((value - min) / range) * actualHeight

      if (index === 0) {
        ctx.moveTo(x, y)
      } else {
        ctx.lineTo(x, y)
      }
    })

    ctx.stroke()

    // Draw fill
    if (options.fill) {
      ctx.globalAlpha = 0.1
      ctx.fillStyle = options.color || CONFIG.CHART_COLORS.primary
      ctx.lineTo(actualWidth, actualHeight)
      ctx.lineTo(0, actualHeight)
      ctx.closePath()
      ctx.fill()
      ctx.globalAlpha = 1
    }
  },

  drawMultiLineChart(ctx, datasets, options = {}) {
    const {width, height} = ctx.canvas
    const actualWidth = width / (window.devicePixelRatio || 1)
    const actualHeight = height / (window.devicePixelRatio || 1)

    ctx.clearRect(0, 0, actualWidth, actualHeight)

    // Draw grid
    ctx.strokeStyle = "#e5e7eb"
    ctx.lineWidth = 1
    ctx.beginPath()
    for (let i = 0; i <= 5; i++) {
      const y = (actualHeight / 5) * i
      ctx.moveTo(0, y)
      ctx.lineTo(actualWidth, y)
    }
    ctx.stroke()

    if (!datasets || datasets.length === 0) {
      return
    }

    // Find global min/max
    const allValues = datasets.flatMap((d) => d.data)
    const max = Math.max(...allValues, 1)
    const min = Math.min(...allValues, 0)
    const range = max - min || 1

    // Draw each dataset
    datasets.forEach((dataset, datasetIndex) => {
      if (!dataset.data || dataset.data.length === 0) {
        return
      }

      ctx.strokeStyle = dataset.color || CONFIG.CHART_COLORS.primary
      ctx.lineWidth = 2
      ctx.beginPath()

      dataset.data.forEach((value, index) => {
        const x = (actualWidth / Math.max(dataset.data.length - 1, 1)) * index
        const y = actualHeight - ((value - min) / range) * actualHeight

        if (index === 0) {
          ctx.moveTo(x, y)
        } else {
          ctx.lineTo(x, y)
        }
      })

      ctx.stroke()
    })
  },
}

const chartManager = {
  charts: new Map(),

  createChart(containerId, metricName, data) {
    const container = document.getElementById(containerId)
    if (!container) {
      return null
    }

    const canvas = document.createElement("canvas")
    canvas.width = container.clientWidth
    canvas.height = 200
    container.innerHTML = ""
    container.appendChild(canvas)

    const ctx = canvas.getContext("2d")
    const chart = {
      canvas,
      ctx,
      data: data || [],
      metricName,
      render: () => this.renderChart(chart),
    }

    this.charts.set(containerId, chart)
    chart.render()
    return chart
  },

  renderChart(chart) {
    const {ctx, canvas, data, metricName} = chart
    const width = canvas.width
    const height = canvas.height

    // Clear canvas
    ctx.clearRect(0, 0, width, height)

    if (!data || data.length === 0) {
      ctx.fillStyle = "#666"
      ctx.font = "14px Arial"
      ctx.textAlign = "center"
      ctx.fillText("No data available", width / 2, height / 2)
      return
    }

    // Draw chart background
    ctx.fillStyle = "#f8f9fa"
    ctx.fillRect(0, 0, width, height)

    // Calculate scales
    const maxValue = Math.max(...data.map((d) => d.value))
    const minValue = Math.min(...data.map((d) => d.value))
    const range = maxValue - minValue || 1

    const padding = 40
    const chartWidth = width - padding * 2
    const chartHeight = height - padding * 2

    // Draw grid lines
    ctx.strokeStyle = "#e0e0e0"
    ctx.lineWidth = 1
    for (let i = 0; i <= 5; i++) {
      const y = padding + (chartHeight / 5) * i
      ctx.beginPath()
      ctx.moveTo(padding, y)
      ctx.lineTo(width - padding, y)
      ctx.stroke()
    }

    // Draw data line
    if (data.length > 1) {
      ctx.strokeStyle = CONFIG.CHART_COLORS.primary
      ctx.lineWidth = 2
      ctx.beginPath()

      data.forEach((point, index) => {
        const x = padding + (chartWidth / (data.length - 1)) * index
        const y = padding + chartHeight - ((point.value - minValue) / range)
            * chartHeight

        if (index === 0) {
          ctx.moveTo(x, y)
        } else {
          ctx.lineTo(x, y)
        }
      })

      ctx.stroke()

      // Draw data points
      ctx.fillStyle = CONFIG.CHART_COLORS.primary
      data.forEach((point, index) => {
        const x = padding + (chartWidth / (data.length - 1)) * index
        const y = padding + chartHeight - ((point.value - minValue) / range)
            * chartHeight

        ctx.beginPath()
        ctx.arc(x, y, 3, 0, 2 * Math.PI)
        ctx.fill()
      })
    }

    // Draw labels
    ctx.fillStyle = "#333"
    ctx.font = "12px Arial"
    ctx.textAlign = "left"
    ctx.fillText(`Max: ${maxValue.toFixed(2)}`, padding, 15)
    ctx.textAlign = "right"
    ctx.fillText(`Min: ${minValue.toFixed(2)}`, width - padding, 15)
    ctx.textAlign = "center"
    ctx.fillText(metricName, width / 2, height - 5)
  },

  updateChart(containerId, newData) {
    const chart = this.charts.get(containerId)
    if (chart) {
      chart.data = newData
      chart.render()
    }
  },

  removeChart(containerId) {
    this.charts.delete(containerId)
  },
}

// Data processing
const dataProcessor = {
  processRoutes(routes) {
    return routes.map((route) => ({
      id: route.route_id,
      predicate: route.predicate,
      uri: route.uri,
      filters: route.filters || [],
      order: route.order || 0,
    }))
  },

  processRouteDefinitions(definitions) {
    return definitions.map((def) => ({
      id: def.id,
      predicates: def.predicates || [],
      filters: def.filters || [],
      uri: def.uri,
      order: def.order || 0,
      metadata: def.metadata || {},
      isAuthorized: this.checkAuthorization(def.filters),
    }))
  },

  checkAuthorization(filters) {
    if (!filters) {
      return false
    }
    return filters.some(
        (filter) => filter.name === "Authorization" && filter.args
            && filter.args._genkey_0 === "true")
  },

  processGlobalFilters(filters) {
    return Object.entries(filters)
    .map(([name, order]) => ({
      name: this.extractFilterName(name),
      fullName: name,
      order: order,
      type: this.getFilterType(name),
    }))
    .sort((a, b) => a.order - b.order)
  },

  extractFilterName(fullName) {
    const match = fullName.match(/([^.]+)@/)
    return match ? match[1] : fullName
  },

  getFilterType(name) {
    if (name.includes("Routing")) {
      return "Routing"
    }
    if (name.includes("Metrics")) {
      return "Metrics"
    }
    if (name.includes("LoadBalancer")) {
      return "Load Balancer"
    }
    if (name.includes("Response")) {
      return "Response"
    }
    if (name.includes("Request")) {
      return "Request"
    }
    return "Other"
  },

  processMetrics(metricsIndex) {
    const categories = {
      http: [],
      jvm: [],
      system: [],
      gateway: [],
      other: [],
    }

    metricsIndex.names.forEach((name) => {
      if (name.startsWith("http.")) {
        categories.http.push(name)
      } else if (name.startsWith("jvm.")) {
        categories.jvm.push(name)
      } else if (name.startsWith("system.")) {
        categories.system.push(name)
      } else if (name.includes("gateway")) {
        categories.gateway.push(name)
      } else {
        categories.other.push(name)
      }
    })

    return categories
  },

  processHealth(health) {
    const components = []

    if (health.components) {
      Object.entries(health.components).forEach(([name, component]) => {
        components.push({
          name,
          status: component.status,
          description: component.description,
          details: component.details,
        })
      })
    }

    return {
      status: health.status,
      components,
    }
  },
}

// UI Controllers
const ui = {
  updateConnectionStatus(connected) {
    const statusDot = $("#connectionStatus")
    const statusText = $("#connectionText")

    if (connected) {
      statusDot.style.background = CONFIG.CHART_COLORS.success
      statusText.textContent = "Connected"
    } else {
      statusDot.style.background = CONFIG.CHART_COLORS.danger
      statusText.textContent = "Disconnected"
    }

    state.isConnected = connected
  },

  updateLastUpdated() {
    $("#lastUpdated").textContent = new Date().toLocaleTimeString()
  },

  showView(viewName) {
    // Update navigation
    $$(".nav button").forEach((btn) => btn.classList.remove("active"))
    $(`.nav button[data-view="${viewName}"]`).classList.add("active")

    // Update views
    $$(".view").forEach((view) => (view.hidden = true))
    $(`#view-${viewName}`).hidden = false

    // Update page title
    const titles = {
      dashboard: "Gateway Dashboard",
      routes: "Route Management",
      filters: "Filter Management",
      metrics: "Metrics Explorer",
      health: "Health Monitoring",
      monitoring: "Live Monitoring",
      loggers: "Loggers",
      features: "Features",
      threaddump: "Thread Dump",
      env: "Environment",
      configprops: "Configuration",
      conditions: "Conditions",
      beans: "Beans",
    }

    $("#pageTitle").textContent = titles[viewName] || "Gateway Admin"
    state.currentView = viewName
  },

  updateKPIs(data) {
    // Routes
    $("#kpi-routes").textContent = data.routes?.length || 0
    $("#kpi-routes-trend").textContent = `${data.routeDefinitions?.length
    || 0} defined`

    // Filters
    const filterCount = Object.keys(data.globalFilters || {}).length
    $("#kpi-filters").textContent = filterCount
    $("#kpi-filters-trend").textContent = `${Object.keys(
        data.routeFilters || {}).length} available`

    // HTTP Requests
    const httpMetric = data.metrics["http.server.requests"]
    if (httpMetric) {
      const countMeasurement = httpMetric.measurements?.find(
          (m) => m.statistic === "COUNT")
      const count = countMeasurement?.value || 0
      $("#kpi-requests").textContent = formatNumber(count)

      const totalTime = httpMetric.measurements?.find(
          (m) => m.statistic === "TOTAL_TIME")?.value || 0
      const avgTime = count > 0 ? ((totalTime / count) * 1000).toFixed(2) : 0
      $("#kpi-requests-trend").textContent = `${avgTime}ms avg`
    }

    // CPU Usage
    const cpuMetric = data.metrics["system.cpu.usage"]
    if (cpuMetric) {
      const cpuValue = cpuMetric.measurements?.[0]?.value || 0
      $("#kpi-cpu").textContent = `${(cpuValue * 100).toFixed(1)}%`
      $("#kpi-cpu-trend").textContent = cpuValue > 0.8 ? "High" : cpuValue > 0.5
          ? "Medium" : "Low"
    }
  },

  updateCharts(data) {
    // Request trends chart
    const requestsCanvas = $("#requestsChart")
    if (requestsCanvas) {
      const ctx = chartUtils.createChart(requestsCanvas)
      const httpMetric = data.metrics["http.server.requests"]

      if (httpMetric) {
        const countMeasurement = httpMetric.measurements?.find(
            (m) => m.statistic === "COUNT")
        const currentCount = countMeasurement?.value || 0

        // Update history
        let history = state.data.metricHistory.get("http.server.requests") || []
        history.push(currentCount)
        if (history.length > CONFIG.MAX_CHART_POINTS) {
          history = history.slice(-CONFIG.MAX_CHART_POINTS)
        }
        state.data.metricHistory.set("http.server.requests", history)

        chartUtils.drawLineChart(ctx, history, {
          color: CONFIG.CHART_COLORS.primary,
          fill: true,
        })
      }
    }

    // System resources chart
    const systemCanvas = $("#systemChart")
    if (systemCanvas) {
      const ctx = chartUtils.createChart(systemCanvas)

      const datasets = []

      // CPU data
      const cpuMetric = data.metrics["system.cpu.usage"]
      if (cpuMetric) {
        const cpuValue = cpuMetric.measurements?.[0]?.value || 0
        let cpuHistory = state.data.metricHistory.get("system.cpu.usage") || []
        cpuHistory.push(cpuValue * 100)
        if (cpuHistory.length > CONFIG.MAX_CHART_POINTS) {
          cpuHistory = cpuHistory.slice(-CONFIG.MAX_CHART_POINTS)
        }
        state.data.metricHistory.set("system.cpu.usage", cpuHistory)

        datasets.push({
          data: cpuHistory,
          color: CONFIG.CHART_COLORS.primary,
        })
      }

      // Memory data
      const memoryMetric = data.metrics["jvm.memory.used"]
      if (memoryMetric) {
        const memoryValue = memoryMetric.measurements?.[0]?.value || 0
        let memoryHistory = state.data.metricHistory.get("jvm.memory.used")
            || []
        memoryHistory.push(memoryValue / 1024 / 1024) // Convert to MB
        if (memoryHistory.length > CONFIG.MAX_CHART_POINTS) {
          memoryHistory = memoryHistory.slice(-CONFIG.MAX_CHART_POINTS)
        }
        state.data.metricHistory.set("jvm.memory.used", memoryHistory)

        datasets.push({
          data: memoryHistory,
          color: CONFIG.CHART_COLORS.success,
        })
      }

      chartUtils.drawMultiLineChart(ctx, datasets)
    }
  },

  updateSystemInfo(data) {
    const container = $("#systemInfo")
    if (!container) {
      return
    }

    const info = []

    // System metrics
    const cpuCount = data.metrics["system.cpu.count"]
    if (cpuCount) {
      info.push({
        label: "CPU Cores",
        value: cpuCount.measurements?.[0]?.value || "N/A",
      })
    }

    const diskFree = data.metrics["disk.free"]
    const diskTotal = data.metrics["disk.total"]
    if (diskFree && diskTotal) {
      const free = diskFree.measurements?.[0]?.value || 0
      const total = diskTotal.measurements?.[0]?.value || 0
      const used = total - free
      const usedPercent = total > 0 ? ((used / total) * 100).toFixed(1) : 0

      info.push({
        label: "Disk Usage",
        value: `${formatBytes(used)} / ${formatBytes(total)} (${usedPercent}%)`,
      })
    }

    const uptime = data.metrics["process.uptime"]
    if (uptime) {
      const uptimeSeconds = uptime.measurements?.[0]?.value || 0
      info.push({
        label: "Uptime",
        value: formatDuration(uptimeSeconds),
      })
    }

    this.renderInfoGrid(container, info)
  },

  updateJVMInfo(data) {
    const container = $("#jvmInfo")
    if (!container) {
      return
    }

    const info = []

    const memoryUsed = data.metrics["jvm.memory.used"]
    const memoryMax = data.metrics["jvm.memory.max"]
    if (memoryUsed && memoryMax) {
      const used = memoryUsed.measurements?.[0]?.value || 0
      const max = memoryMax.measurements?.[0]?.value || 0
      const usedPercent = max > 0 ? ((used / max) * 100).toFixed(1) : 0

      info.push({
        label: "Memory Usage",
        value: `${formatBytes(used)} / ${formatBytes(max)} (${usedPercent}%)`,
      })
    }

    const threadsLive = data.metrics["jvm.threads.live"]
    if (threadsLive) {
      info.push({
        label: "Live Threads",
        value: threadsLive.measurements?.[0]?.value || "N/A",
      })
    }

    const classesLoaded = data.metrics["jvm.classes.loaded"]
    if (classesLoaded) {
      info.push({
        label: "Classes Loaded",
        value: formatNumber(classesLoaded.measurements?.[0]?.value || 0),
      })
    }

    this.renderInfoGrid(container, info)
  },

  updateGatewayInfo(data) {
    const container = $("#gatewayInfo")
    if (!container) {
      return
    }

    const info = []

    info.push({
      label: "Active Routes",
      value: data.routes?.length || 0,
    })

    info.push({
      label: "Route Definitions",
      value: data.routeDefinitions?.length || 0,
    })

    info.push({
      label: "Global Filters",
      value: Object.keys(data.globalFilters || {}).length,
    })

    const gatewayRoutes = data.metrics["spring.cloud.gateway.routes.count"]
    if (gatewayRoutes) {
      info.push({
        label: "Gateway Routes",
        value: gatewayRoutes.measurements?.[0]?.value || "N/A",
      })
    }

    this.renderInfoGrid(container, info)
  },

  renderInfoGrid(container, items) {
    container.innerHTML = items
    .map(
        (item) => `
      <div class="info-item">
        <span class="info-label">${escapeHtml(item.label)}</span>
        <span class="info-value">${escapeHtml(String(item.value))}</span>
      </div>
    `,
    )
    .join("")
  },

  updateRoutesTable(routes) {
    const tbody = $("#routesTable tbody")
    if (!tbody) {
      return
    }

    tbody.innerHTML = routes
    .map(
        (route) => `
      <tr>
        <td><strong>${escapeHtml(route.id)}</strong></td>
        <td><code>${escapeHtml(route.predicate)}</code></td>
        <td>${escapeHtml(route.uri)}</td>
        <td>
          <span class="status-badge ${route.isAuthorized ? "authorized"
            : "public"}">
            ${route.isAuthorized ? "Authorized" : "Public"}
          </span>
        </td>
        <td>
          <span class="status-badge">${route.filters.length} filters</span>
        </td>
        <td>
          <span class="status-badge active">Active</span>
        </td>
      </tr>
    `,
    )
    .join("")
  },

  updateRouteDefinitionsTable(definitions) {
    const tbody = $("#routeDefinitionsTable tbody")
    if (!tbody) {
      return
    }

    tbody.innerHTML = definitions
    .map(
        (def) => `
      <tr>
        <td><strong>${escapeHtml(def.id)}</strong></td>
        <td>
          ${def.predicates
        .map(
            (p) => `
            <div><code>${escapeHtml(p.name)}: ${escapeHtml(
                JSON.stringify(p.args))}</code></div>
          `,
        )
        .join("")}
        </td>
        <td>${escapeHtml(def.uri)}</td>
        <td>${def.order}</td>
        <td>
          ${def.filters
        .map(
            (f) => `
            <div><span class="status-badge">${escapeHtml(f.name)}</span></div>
          `,
        )
        .join("")}
        </td>
        <td>
          <button class="btn ghost small">Edit</button>
          <button class="btn ghost small">Delete</button>
        </td>
      </tr>
    `,
    )
    .join("")

    // Update stats
    $("#totalRoutes").textContent = definitions.length
    $("#activeRoutes").textContent = definitions.length // Assuming all are active
  },

  updateActiveRoutesTable(routes) {
    const tbody = $("#activeRoutesTable tbody")
    if (!tbody) {
      return
    }

    tbody.innerHTML = routes
    .map(
        (route) => `
      <tr>
        <td><strong>${escapeHtml(route.id)}</strong></td>
        <td><code>${escapeHtml(route.predicate)}</code></td>
        <td>${escapeHtml(route.uri)}</td>
        <td>${route.filters.length}</td>
        <td>
          <button class="btn ghost small">View Details</button>
        </td>
      </tr>
    `,
    )
    .join("")

    $("#loadedRoutes").textContent = routes.length
  },

  updateGlobalFiltersTable(filters) {
    const tbody = $("#globalFiltersTable tbody")
    if (!tbody) {
      return
    }

    tbody.innerHTML = filters
    .map(
        (filter) => `
      <tr>
        <td><strong>${escapeHtml(filter.name)}</strong></td>
        <td>${filter.order}</td>
        <td>
          <span class="status-badge">${escapeHtml(filter.type)}</span>
        </td>
      </tr>
    `,
    )
    .join("")
  },

  updateRouteFiltersTable(filters) {
    const tbody = $("#routeFiltersTable tbody")
    if (!tbody) {
      return
    }

    const filterEntries = Object.entries(filters)
    tbody.innerHTML = filterEntries
    .map(
        ([name, config]) => `
      <tr>
        <td>${escapeHtml(dataProcessor.extractFilterName(name))}</td>
        <td><code>${escapeHtml(String(config))}</code></td>
        <td>
          <span class="status-badge active">Available</span>
        </td>
      </tr>
    `,
    )
    .join("")
  },

  updateRoutePredicatesTable(predicates) {
    const tbody = $("#routePredicatesTable tbody")
    if (!tbody) {
      return
    }

    const predicateEntries = Object.entries(predicates)
    tbody.innerHTML = predicateEntries
    .map(
        ([name, config]) => `
      <tr>
        <td>${escapeHtml(dataProcessor.extractFilterName(name))}</td>
        <td><code>${escapeHtml(String(config))}</code></td>
        <td>Route matching predicate</td>
      </tr>
    `,
    )
    .join("")
  },

  updateLoggersView(loggers) {
    const container = document.getElementById("loggers-content")
    if (!container) {
      return
    }

    const loggerEntries = Object.entries(loggers.loggers || {})

    container.innerHTML = `
      <div class="section-header">
        <h3>Logger Configuration</h3>
        <div class="logger-controls">
          <input type="text" id="logger-search" placeholder="Search loggers..." class="search-input">
          <select id="log-level-filter" class="filter-select">
            <option value="">All Levels</option>
            <option value="TRACE">TRACE</option>
            <option value="DEBUG">DEBUG</option>
            <option value="INFO">INFO</option>
            <option value="WARN">WARN</option>
            <option value="ERROR">ERROR</option>
          </select>
        </div>
      </div>
      <div class="loggers-table">
        <table>
          <thead>
            <tr>
              <th>Logger Name</th>
              <th>Configured Level</th>
              <th>Effective Level</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            ${loggerEntries
    .map(
        ([name, config]) => `
              <tr>
                <td class="logger-name">${name}</td>
                <td class="configured-level">${config.configuredLevel
        || "INHERITED"}</td>
                <td class="effective-level">${config.effectiveLevel}</td>
                <td class="logger-actions">
                  <select class="level-selector" data-logger="${name}">
                    <option value="">INHERITED</option>
                    <option value="TRACE" ${config.configuredLevel === "TRACE"
            ? "selected" : ""}>TRACE</option>
                    <option value="DEBUG" ${config.configuredLevel === "DEBUG"
            ? "selected" : ""}>DEBUG</option>
                    <option value="INFO" ${config.configuredLevel === "INFO"
            ? "selected" : ""}>INFO</option>
                    <option value="WARN" ${config.configuredLevel === "WARN"
            ? "selected" : ""}>WARN</option>
                    <option value="ERROR" ${config.configuredLevel === "ERROR"
            ? "selected" : ""}>ERROR</option>
                  </select>
                  <button class="btn-small btn-primary" onclick="app.setLogLevel('${name}', this.previousElementSibling.value)">
                    Update
                  </button>
                </td>
              </tr>
            `,
    )
    .join("")}
          </tbody>
        </table>
      </div>
    `

    // Add search functionality
    const searchInput = document.getElementById("logger-search")
    const levelFilter = document.getElementById("log-level-filter")

    const filterLoggers = () => {
      const searchTerm = searchInput.value.toLowerCase()
      const levelFilter = document.getElementById("log-level-filter").value
      const rows = container.querySelectorAll("tbody tr")

      rows.forEach((row) => {
        const loggerName = row.querySelector(
            ".logger-name").textContent.toLowerCase()
        const effectiveLevel = row.querySelector(".effective-level").textContent

        const matchesSearch = loggerName.includes(searchTerm)
        const matchesLevel = !levelFilter || effectiveLevel === levelFilter

        row.style.display = matchesSearch && matchesLevel ? "" : "none"
      })
    }

    searchInput.addEventListener("input", filterLoggers)
    levelFilter.addEventListener("change", filterLoggers)
  },

  updateFeaturesView(features) {
    const container = document.getElementById("features-content")
    if (!container) {
      return
    }

    container.innerHTML = `
      <div class="section-header">
        <h3>Application Features</h3>
      </div>
      <div class="features-grid">
        ${Object.entries(features)
    .map(
        ([category, featureList]) => `
          <div class="feature-category">
            <h4>${category}</h4>
            <div class="feature-list">
              ${featureList
        .map(
            (feature) => `
                <div class="feature-item ${feature.enabled ? "enabled"
                : "disabled"}">
                  <span class="feature-name">${feature.name}</span>
                  <span class="feature-status">${feature.enabled ? "Enabled"
                : "Disabled"}</span>
                </div>
              `,
        )
        .join("")}
            </div>
          </div>
        `,
    )
    .join("")}
      </div>
    `
  },

  updateThreadDumpView(threadDump) {
    const container = document.getElementById("threaddump-content")
    if (!container) {
      return
    }

    const threads = threadDump.threads || []
    const summary = {
      total: threads.length,
      runnable: threads.filter((t) => t.threadState === "RUNNABLE").length,
      blocked: threads.filter((t) => t.threadState === "BLOCKED").length,
      waiting: threads.filter((t) => t.threadState === "WAITING").length,
      timedWaiting: threads.filter(
          (t) => t.threadState === "TIMED_WAITING").length,
    }

    container.innerHTML = `
      <div class="section-header">
        <h3>Thread Dump Analysis</h3>
        <button class="btn-primary" onclick="app.downloadThreadDump()">Download Full Dump</button>
      </div>
      
      <div class="thread-summary">
        <div class="summary-card">
          <h4>Total Threads</h4>
          <span class="summary-value">${summary.total}</span>
        </div>
        <div class="summary-card">
          <h4>Runnable</h4>
          <span class="summary-value text-success">${summary.runnable}</span>
        </div>
        <div class="summary-card">
          <h4>Blocked</h4>
          <span class="summary-value text-danger">${summary.blocked}</span>
        </div>
        <div class="summary-card">
          <h4>Waiting</h4>
          <span class="summary-value text-warning">${summary.waiting}</span>
        </div>
        <div class="summary-card">
          <h4>Timed Waiting</h4>
          <span class="summary-value text-info">${summary.timedWaiting}</span>
        </div>
      </div>

      <div class="threads-table">
        <table>
          <thead>
            <tr>
              <th>Thread Name</th>
              <th>State</th>
              <th>CPU Time</th>
              <th>Blocked Count</th>
              <th>Waited Count</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            ${threads
    .slice(0, 50)
    .map(
        (thread) => `
              <tr>
                <td class="thread-name">${thread.threadName}</td>
                <td class="thread-state state-${thread.threadState.toLowerCase()}">${thread.threadState}</td>
                <td>${thread.cpuTime || "N/A"}</td>
                <td>${thread.blockedCount || 0}</td>
                <td>${thread.waitedCount || 0}</td>
                <td>
                  <button class="btn-small btn-secondary" onclick="app.showThreadDetails('${thread.threadId}')">
                    Details
                  </button>
                </td>
              </tr>
            `,
    )
    .join("")}
          </tbody>
        </table>
        ${threads.length > 50
        ? `<p class="table-note">Showing first 50 of ${threads.length} threads</p>`
        : ""}
      </div>
    `
  },

  updateEnvView(env) {
    const container = document.getElementById("env-content")
    if (!container) {
      return
    }

    const profiles = env.activeProfiles || []
    const propertySources = env.propertySources || []

    container.innerHTML = `
      <div class="section-header">
        <h3>Environment Properties</h3>
        <div class="env-controls">
          <input type="text" id="env-search" placeholder="Search properties..." class="search-input">
          <select id="source-filter" class="filter-select">
            <option value="">All Sources</option>
            ${propertySources
    .map(
        (source) => `
              <option value="${source.name}">${source.name}</option>
            `,
    )
    .join("")}
          </select>
        </div>
      </div>

      <div class="env-summary">
        <div class="summary-card">
          <h4>Active Profiles</h4>
          <div class="profiles">
            ${
        profiles.length > 0
            ? profiles
            .map(
                (profile) => `
              <span class="profile-badge">${profile}</span>
            `,
            )
            .join("")
            : '<span class="text-muted">No active profiles</span>'
    }
          </div>
        </div>
      </div>

      <div class="property-sources">
        ${propertySources
    .map(
        (source) => `
          <div class="property-source" data-source="${source.name}">
            <h4 class="source-name">${source.name}</h4>
            <div class="properties-table">
              <table>
                <thead>
                  <tr>
                    <th>Property</th>
                    <th>Value</th>
                    <th>Origin</th>
                  </tr>
                </thead>
                <tbody>
                  ${Object.entries(source.properties || {})
        .map(
            ([key, prop]) => `
                    <tr class="property-row" data-key="${key.toLowerCase()}">
                      <td class="property-key">${key}</td>
                      <td class="property-value">${this.formatPropertyValue(
                prop.value)}</td>
                      <td class="property-origin">${prop.origin || "N/A"}</td>
                    </tr>
                  `,
        )
        .join("")}
                </tbody>
              </table>
            </div>
          </div>
        `,
    )
    .join("")}
      </div>
    `

    // Add search and filter functionality
    const searchInput = document.getElementById("env-search")
    const sourceFilter = document.getElementById("source-filter")

    const filterProperties = () => {
      const searchTerm = searchInput.value.toLowerCase()
      const selectedSource = sourceFilter.value

      const sources = container.querySelectorAll(".property-source")
      sources.forEach((source) => {
        const sourceName = source.dataset.source
        const shouldShowSource = !selectedSource || sourceName
            === selectedSource

        if (!shouldShowSource) {
          source.style.display = "none"
          return
        }

        source.style.display = ""
        const rows = source.querySelectorAll(".property-row")
        rows.forEach((row) => {
          const key = row.dataset.key
          const matchesSearch = key.includes(searchTerm)
          row.style.display = matchesSearch ? "" : "none"
        })
      })
    }

    searchInput.addEventListener("input", filterProperties)
    sourceFilter.addEventListener("change", filterProperties)
  },

  updateConfigPropsView(configProps) {
    const container = document.getElementById("configprops-content")
    if (!container) {
      return
    }

    const contexts = configProps.contexts || {}

    container.innerHTML = `
      <div class="section-header">
        <h3>Configuration Properties</h3>
        <input type="text" id="configprops-search" placeholder="Search configuration properties..." class="search-input">
      </div>

      <div class="config-contexts">
        ${Object.entries(contexts)
    .map(
        ([contextName, context]) => `
          <div class="config-context">
            <h4>${contextName}</h4>
            <div class="config-beans">
              ${Object.entries(context.beans || {})
        .map(
            ([beanName, bean]) => `
                <div class="config-bean" data-bean="${beanName.toLowerCase()}">
                  <div class="bean-header">
                    <h5>${beanName}</h5>
                    <span class="bean-prefix">${bean.prefix || "N/A"}</span>
                  </div>
                  <div class="bean-properties">
                    <table>
                      <thead>
                        <tr>
                          <th>Property</th>
                          <th>Value</th>
                          <th>Type</th>
                        </tr>
                      </thead>
                      <tbody>
                        ${Object.entries(bean.properties || {})
            .map(
                ([propName, propValue]) => `
                          <tr>
                            <td class="prop-name">${propName}</td>
                            <td class="prop-value">${this.formatPropertyValue(
                    propValue)}</td>
                            <td class="prop-type">${typeof propValue}</td>
                          </tr>
                        `,
            )
            .join("")}
                      </tbody>
                    </table>
                  </div>
                </div>
              `,
        )
        .join("")}
            </div>
          </div>
        `,
    )
    .join("")}
      </div>
    `

    // Add search functionality
    const searchInput = document.getElementById("configprops-search")
    searchInput.addEventListener("input", (e) => {
      const searchTerm = e.target.value.toLowerCase()
      const beans = container.querySelectorAll(".config-bean")

      beans.forEach((bean) => {
        const beanName = bean.dataset.bean
        const matchesSearch = beanName.includes(searchTerm)
        bean.style.display = matchesSearch ? "" : "none"
      })
    })
  },

  updateConditionsView(conditions) {
    const container = document.getElementById("conditions-content")
    if (!container) {
      return
    }

    const contexts = conditions.contexts || {}

    container.innerHTML = `
      <div class="section-header">
        <h3>Auto-configuration Conditions</h3>
        <div class="condition-controls">
          <input type="text" id="conditions-search" placeholder="Search conditions..." class="search-input">
          <select id="condition-filter" class="filter-select">
            <option value="">All Conditions</option>
            <option value="matched">Matched</option>
            <option value="not-matched">Not Matched</option>
          </select>
        </div>
      </div>

      <div class="conditions-contexts">
        ${Object.entries(contexts)
    .map(
        ([contextName, context]) => `
          <div class="condition-context">
            <h4>${contextName}</h4>
            
            <div class="condition-section">
              <h5>Positive Matches</h5>
              <div class="condition-list matched">
                ${Object.entries(context.positiveMatches || {})
        .map(
            ([className, conditions]) => `
                  <div class="condition-item" data-class="${className.toLowerCase()}" data-status="matched">
                    <div class="condition-header">
                      <span class="class-name">${className}</span>
                      <span class="condition-status matched">✓ Matched</span>
                    </div>
                    <div class="condition-details">
                      ${conditions
            .map(
                (condition) => `
                        <div class="condition-detail">
                          <span class="condition-text">${condition.condition}</span>
                          <span class="condition-message">${condition.message
                || ""}</span>
                        </div>
                      `,
            )
            .join("")}
                    </div>
                  </div>
                `,
        )
        .join("")}
              </div>
            </div>

            <div class="condition-section">
              <h5>Negative Matches</h5>
              <div class="condition-list not-matched">
                ${Object.entries(context.negativeMatches || {})
        .map(
            ([className, conditions]) => `
                  <div class="condition-item" data-class="${className.toLowerCase()}" data-status="not-matched">
                    <div class="condition-header">
                      <span class="class-name">${className}</span>
                      <span class="condition-status not-matched">✗ Not Matched</span>
                    </div>
                    <div class="condition-details">
                      ${conditions.notMatched
            .map(
                (condition) => `
                        <div class="condition-detail">
                          <span class="condition-text">${condition.condition}</span>
                          <span class="condition-message">${condition.message
                || ""}</span>
                        </div>
                      `,
            )
            .join("")}
                    </div>
                  </div>
                `,
        )
        .join("")}
              </div>
            </div>
          </div>
        `,
    )
    .join("")}
      </div>
    `

    // Add search and filter functionality
    const searchInput = document.getElementById("conditions-search")
    const statusFilter = document.getElementById("condition-filter")

    const filterConditions = () => {
      const searchTerm = searchInput.value.toLowerCase()
      const selectedStatus = statusFilter.value

      const items = container.querySelectorAll(".condition-item")
      items.forEach((item) => {
        const className = item.dataset.class
        const status = item.dataset.status

        const matchesSearch = className.includes(searchTerm)
        const matchesStatus = !selectedStatus || status === selectedStatus

        item.style.display = matchesSearch && matchesStatus ? "" : "none"
      })
    }

    searchInput.addEventListener("input", filterConditions)
    statusFilter.addEventListener("change", filterConditions)
  },

  updateBeansView(beans) {
    const container = document.getElementById("beans-content")
    if (!container) {
      return
    }

    const contexts = beans.contexts || {}

    container.innerHTML = `
      <div class="section-header">
        <h3>Spring Beans</h3>
        <div class="beans-controls">
          <input type="text" id="beans-search" placeholder="Search beans..." class="search-input">
          <select id="scope-filter" class="filter-select">
            <option value="">All Scopes</option>
            <option value="singleton">Singleton</option>
            <option value="prototype">Prototype</option>
            <option value="request">Request</option>
            <option value="session">Session</option>
          </select>
        </div>
      </div>

      <div class="beans-contexts">
        ${Object.entries(contexts)
    .map(
        ([contextName, context]) => `
          <div class="beans-context">
            <h4>${contextName}</h4>
            <div class="beans-summary">
              <span class="bean-count">Total Beans: ${Object.keys(
            context.beans || {}).length}</span>
            </div>
            <div class="beans-table">
              <table>
                <thead>
                  <tr>
                    <th>Bean Name</th>
                    <th>Type</th>
                    <th>Scope</th>
                    <th>Dependencies</th>
                    <th>Resource</th>
                  </tr>
                </thead>
                <tbody>
                  ${Object.entries(context.beans || {})
        .map(
            ([beanName, bean]) => `
                    <tr class="bean-row" data-name="${beanName.toLowerCase()}" data-scope="${bean.scope
            || "singleton"}">
                      <td class="bean-name">${beanName}</td>
                      <td class="bean-type">${bean.type || "N/A"}</td>
                      <td class="bean-scope">
                        <span class="scope-badge scope-${bean.scope
            || "singleton"}">${bean.scope || "singleton"}</span>
                      </td>
                      <td class="bean-dependencies">
                        ${
                (bean.dependencies || []).length > 0
                    ? `<span class="dependency-count">${bean.dependencies.length} dependencies</span>`
                    : '<span class="text-muted">No dependencies</span>'
            }
                      </td>
                      <td class="bean-resource">${bean.resource || "N/A"}</td>
                    </tr>
                  `,
        )
        .join("")}
                </tbody>
              </table>
            </div>
          </div>
        `,
    )
    .join("")}
      </div>
    `

    // Add search and filter functionality
    const searchInput = document.getElementById("beans-search")
    const scopeFilter = document.getElementById("scope-filter")

    const filterBeans = () => {
      const searchTerm = searchInput.value.toLowerCase()
      const selectedScope = scopeFilter.value

      const rows = container.querySelectorAll(".bean-row")
      rows.forEach((row) => {
        const beanName = row.dataset.name
        const scope = row.dataset.scope

        const matchesSearch = beanName.includes(searchTerm)
        const matchesScope = !selectedScope || scope === selectedScope

        row.style.display = matchesSearch && matchesScope ? "" : "none"
      })
    }

    searchInput.addEventListener("input", filterBeans)
    scopeFilter.addEventListener("change", filterBeans)
  },

  updateMetricsView(metricsIndex) {
    const container = document.getElementById("metrics-content")
    if (!container) {
      return
    }

    const metrics = metricsIndex.names || []
    const categorizedMetrics = this.categorizeMetrics(metrics)

    container.innerHTML = `
      <div class="section-header">
        <h3>Application Metrics</h3>
        <div class="metrics-controls">
          <input type="text" id="metrics-search" placeholder="Search metrics..." class="search-input">
          <select id="category-filter" class="filter-select">
            <option value="">All Categories</option>
            ${Object.keys(categorizedMetrics)
    .map(
        (category) => `
              <option value="${category}">${category}</option>
            `,
    )
    .join("")}
          </select>
          <button class="btn-primary" onclick="app.showAddChartModal()">Add Chart</button>
        </div>
      </div>

      <div class="metrics-categories">
        ${Object.entries(categorizedMetrics)
    .map(
        ([category, categoryMetrics]) => `
          <div class="metric-category" data-category="${category}">
            <h4 class="category-title">${category} (${categoryMetrics.length})</h4>
            <div class="metrics-grid">
              ${categoryMetrics
        .map(
            (metric) => `
                <div class="metric-item" data-metric="${metric.toLowerCase()}">
                  <div class="metric-header">
                    <span class="metric-name">${metric}</span>
                    <div class="metric-actions">
                      <button class="btn-small btn-secondary" onclick="app.viewMetricDetails('${metric}')">
                        View
                      </button>
                      <button class="btn-small btn-primary" onclick="app.addMetricChart('${metric}')">
                        Chart
                      </button>
                    </div>
                  </div>
                </div>
              `,
        )
        .join("")}
            </div>
          </div>
        `,
    )
    .join("")}
      </div>

      <div class="metric-charts-section">
        <h4>Active Charts</h4>
        <div id="metric-charts-container" class="charts-container">
          <!-- Dynamic charts will be added here -->
        </div>
      </div>
    `

    // Add search and filter functionality
    const searchInput = document.getElementById("metrics-search")
    const categoryFilter = document.getElementById("category-filter")

    const filterMetrics = () => {
      const searchTerm = searchInput.value.toLowerCase()
      const selectedCategory = categoryFilter.value

      const categories = container.querySelectorAll(".metric-category")
      categories.forEach((category) => {
        const categoryName = category.dataset.category
        const shouldShowCategory = !selectedCategory || categoryName
            === selectedCategory

        if (!shouldShowCategory) {
          category.style.display = "none"
          return
        }

        category.style.display = ""
        const items = category.querySelectorAll(".metric-item")
        let visibleCount = 0

        items.forEach((item) => {
          const metricName = item.dataset.metric
          const matchesSearch = metricName.includes(searchTerm)
          item.style.display = matchesSearch ? "" : "none"
          if (matchesSearch) {
            visibleCount++
          }
        })

        // Update category count
        const title = category.querySelector(".category-title")
        const originalText = title.textContent.split(" (")[0]
        title.textContent = `${originalText} (${visibleCount})`
      })
    }

    searchInput.addEventListener("input", filterMetrics)
    categoryFilter.addEventListener("change", filterMetrics)
  },

  categorizeMetrics(metrics) {
    const categories = {
      HTTP: [],
      "JVM Memory": [],
      "JVM Threads": [],
      "JVM Classes": [],
      System: [],
      Database: [],
      Cache: [],
      Gateway: [],
      Custom: [],
      Other: [],
    }

    metrics.forEach((metric) => {
      if (metric.includes("http.")) {
        categories["HTTP"].push(metric)
      } else if (metric.includes("jvm.memory")) {
        categories["JVM Memory"].push(metric)
      } else if (metric.includes("jvm.threads")) {
        categories["JVM Threads"].push(metric)
      } else if (metric.includes("jvm.classes")) {
        categories["JVM Classes"].push(metric)
      } else if (metric.includes("system.")) {
        categories["System"].push(metric)
      } else if (metric.includes("jdbc.") || metric.includes("hikaricp.")) {
        categories["Database"].push(metric)
      } else if (metric.includes("cache.")) {
        categories["Cache"].push(metric)
      } else if (metric.includes("gateway.") || metric.includes(
          "spring.cloud.gateway")) {
        categories["Gateway"].push(metric)
      } else if (metric.includes("application.")) {
        categories["Custom"].push(metric)
      } else {
        categories["Other"].push(metric)
      }
    })

    // Remove empty categories
    Object.keys(categories).forEach((key) => {
      if (categories[key].length === 0) {
        delete categories[key]
      }
    })

    return categories
  },

  formatPropertyValue(value) {
    if (value === null || value === undefined) {
      return '<span class="text-muted">null</span>'
    }
    if (typeof value === "string" && value.length > 100) {
      return `<span class="truncated" title="${value}">${value.substring(0,
          100)}...</span>`
    }
    if (typeof value === "object") {
      return `<span class="object-value">[Object]</span>`
    }
    return String(value)
  },
}

const app = {
  async loadData() {
    try {
      ui.updateConnectionStatus(false)

      const [
        routes,
        routeDefinitions,
        globalFilters,
        routeFilters,
        routePredicates,
        metricsIndex,
        health,
        loggers,
        features,
        env,
        configProps,
        conditions,
        beans,
      ] = await Promise.all([
        api.getRoutes().catch(() => []),
        api.getRouteDefinitions().catch(() => []),
        api.getGlobalFilters().catch(() => ({})),
        api.getRouteFilters().catch(() => ({})),
        api.getRoutePredicates().catch(() => ({})),
        api.getMetrics().catch(() => ({names: []})),
        api.getHealth().catch(() => ({status: "UNKNOWN", components: {}})),
        api.getLoggers().catch(() => ({loggers: {}})),
        api.getFeatures().catch(() => ({})),
        api.getEnv().catch(() => ({activeProfiles: [], propertySources: []})),
        api.getConfigProps().catch(() => ({contexts: {}})),
        api.getConditions().catch(() => ({contexts: {}})),
        api.getBeans().catch(() => ({contexts: {}})),
      ])

      // Load key metrics
      const keyMetrics = [
        "http.server.requests",
        "system.cpu.usage",
        "jvm.memory.used",
        "jvm.memory.max",
        "system.cpu.count",
        "disk.free",
        "disk.total",
        "process.uptime",
        "jvm.threads.live",
        "jvm.classes.loaded",
        "spring.cloud.gateway.routes.count",
      ]

      const metrics = {}
      await Promise.all(
          keyMetrics.map(async (metricName) => {
            try {
              metrics[metricName] = await api.getMetric(metricName)
            } catch (error) {
              console.warn(`Failed to load metric ${metricName}:`, error)
            }
          }),
      )

      // Process and store data
      state.data = {
        routes: dataProcessor.processRoutes(routes),
        routeDefinitions: dataProcessor.processRouteDefinitions(
            routeDefinitions),
        globalFilters,
        routeFilters,
        routePredicates,
        metrics,
        health,
        metricsIndex,
        loggers,
        features,
        env,
        configProps,
        conditions,
        beans,
        metricHistory: state.data.metricHistory, // Preserve history
      }

      // Update UI based on current view
      this.updateCurrentView()

      ui.updateConnectionStatus(true)
      ui.updateLastUpdated()
    } catch (error) {
      console.error("Failed to load data:", error)
      ui.updateConnectionStatus(false)
    }
  },

  async setLogLevel(loggerName, level) {
    try {
      await api.setLogLevel(loggerName, level)
      // Reload loggers data
      const loggers = await api.getLoggers()
      state.data.loggers = loggers
      ui.updateLoggersView(loggers)
      this.showNotification("Log level updated successfully", "success")
    } catch (error) {
      console.error("Failed to set log level:", error)
      this.showNotification("Failed to update log level", "error")
    }
  },

  async downloadThreadDump() {
    try {
      const threadDump = await api.getThreadDump()
      const blob = new Blob([JSON.stringify(threadDump, null, 2)],
          {type: "application/json"})
      const url = URL.createObjectURL(blob)
      const a = document.createElement("a")
      a.href = url
      a.download = `threaddump-${new Date().toISOString()}.json`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    } catch (error) {
      console.error("Failed to download thread dump:", error)
      this.showNotification("Failed to download thread dump", "error")
    }
  },

  async downloadHeapDump() {
    try {
      const heapDump = await api.getHeapDump()
      const url = URL.createObjectURL(heapDump)
      const a = document.createElement("a")
      a.href = url
      a.download = `heapdump-${new Date().toISOString()}.hprof`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    } catch (error) {
      console.error("Failed to download heap dump:", error)
      this.showNotification("Failed to download heap dump", "error")
    }
  },

  showAddChartModal() {
    const modal = document.getElementById("add-chart-modal")
    if (!modal) {
      // Create modal if it doesn't exist
      const modalHtml = `
        <div id="add-chart-modal" class="modal">
          <div class="modal-content">
            <div class="modal-header">
              <h3>Add Metric Chart</h3>
              <button class="modal-close" onclick="app.closeAddChartModal()">&times;</button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label for="chart-metric-select">Select Metric:</label>
                <select id="chart-metric-select" class="form-control">
                  <option value="">Choose a metric...</option>
                </select>
              </div>
              <div class="form-group">
                <label for="chart-title">Chart Title:</label>
                <input type="text" id="chart-title" class="form-control" placeholder="Enter chart title">
              </div>
              <div class="form-group">
                <label for="chart-type">Chart Type:</label>
                <select id="chart-type" class="form-control">
                  <option value="line">Line Chart</option>
                  <option value="area">Area Chart</option>
                  <option value="bar">Bar Chart</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn-secondary" onclick="app.closeAddChartModal()">Cancel</button>
              <button class="btn-primary" onclick="app.createMetricChart()">Create Chart</button>
            </div>
          </div>
        </div>
      `
      document.body.insertAdjacentHTML("beforeend", modalHtml)
    }

    // Populate metric options
    const select = document.getElementById("chart-metric-select")
    const metrics = state.data.metricsIndex?.names || []
    select.innerHTML =
        '<option value="">Choose a metric...</option>' +
        metrics.map(
            (metric) => `<option value="${metric}">${metric}</option>`).join("")

    document.getElementById("add-chart-modal").style.display = "flex"
  },

  closeAddChartModal() {
    document.getElementById("add-chart-modal").style.display = "none"
  },

  async createMetricChart() {
    const metricName = document.getElementById("chart-metric-select").value
    const title = document.getElementById("chart-title").value || metricName
    const type = document.getElementById("chart-type").value

    if (!metricName) {
      this.showNotification("Please select a metric", "error")
      return
    }

    try {
      // Create chart container
      const chartId = `chart-${Date.now()}`
      const chartsContainer = document.getElementById("metric-charts-container")

      const chartHtml = `
        <div class="metric-chart" id="${chartId}">
          <div class="chart-header">
            <h5>${title}</h5>
            <div class="chart-controls">
              <button class="btn-small btn-secondary" onclick="app.refreshChart('${chartId}', '${metricName}')">Refresh</button>
              <button class="btn-small btn-danger" onclick="app.removeChart('${chartId}')">Remove</button>
            </div>
          </div>
          <div class="chart-container" id="${chartId}-container">
            <div class="loading">Loading chart data...</div>
          </div>
        </div>
      `

      chartsContainer.insertAdjacentHTML("beforeend", chartHtml)

      // Load initial data and create chart
      await this.refreshChart(chartId, metricName)

      this.closeAddChartModal()
      this.showNotification("Chart created successfully", "success")
    } catch (error) {
      console.error("Failed to create chart:", error)
      this.showNotification("Failed to create chart", "error")
    }
  },

  async refreshChart(chartId, metricName) {
    try {
      const metricData = await api.getMetric(metricName)
      const containerId = `${chartId}-container`

      // Process metric data for chart
      const chartData = this.processMetricForChart(metricData)

      // Create or update chart
      chartManager.createChart(containerId, metricName, chartData)
    } catch (error) {
      console.error("Failed to refresh chart:", error)
      const container = document.getElementById(`${chartId}-container`)
      if (container) {
        container.innerHTML = '<div class="error">Failed to load chart data</div>'
      }
    }
  },

  removeChart(chartId) {
    const chartElement = document.getElementById(chartId)
    if (chartElement) {
      chartManager.removeChart(`${chartId}-container`)
      chartElement.remove()
    }
  },

  processMetricForChart(metricData) {
    // Convert metric data to chart format
    if (metricData.measurements && metricData.measurements.length > 0) {
      return metricData.measurements.map((measurement, index) => ({
        timestamp: Date.now() - (metricData.measurements.length - index) * 1000,
        value: measurement.value,
      }))
    }

    // Single value metric
    if (typeof metricData.value === "number") {
      return [
        {
          timestamp: Date.now(),
          value: metricData.value,
        },
      ]
    }

    return []
  },

  async addMetricChart(metricName) {
    const chartId = `chart-${Date.now()}`
    const chartsContainer = document.getElementById("metric-charts-container")

    const chartHtml = `
      <div class="metric-chart" id="${chartId}">
        <div class="chart-header">
          <h5>${metricName}</h5>
          <div class="chart-controls">
            <button class="btn-small btn-secondary" onclick="app.refreshChart('${chartId}', '${metricName}')">Refresh</button>
            <button class="btn-small btn-danger" onclick="app.removeChart('${chartId}')">Remove</button>
          </div>
        </div>
        <div class="chart-container" id="${chartId}-container">
          <div class="loading">Loading chart data...</div>
        </div>
      </div>
    `

    chartsContainer.insertAdjacentHTML("beforeend", chartHtml)
    await this.refreshChart(chartId, metricName)
  },

  async viewMetricDetails(metricName) {
    try {
      const metricData = await api.getMetric(metricName)

      const modal = document.createElement("div")
      modal.className = "modal"
      modal.style.display = "flex"
      modal.innerHTML = `
        <div class="modal-content">
          <div class="modal-header">
            <h3>Metric Details: ${metricName}</h3>
            <button class="modal-close" onclick="this.closest('.modal').remove()">&times;</button>
          </div>
          <div class="modal-body">
            <pre class="metric-details">${JSON.stringify(metricData, null, 2)}</pre>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" onclick="this.closest('.modal').remove()">Close</button>
          </div>
        </div>
      `

      document.body.appendChild(modal)
    } catch (error) {
      console.error("Failed to load metric details:", error)
      this.showNotification("Failed to load metric details", "error")
    }
  },

  showNotification(message, type = "info") {
    const notification = document.createElement("div")
    notification.className = `notification notification-${type}`
    notification.textContent = message

    document.body.appendChild(notification)

    setTimeout(() => {
      notification.classList.add("show")
    }, 100)

    setTimeout(() => {
      notification.classList.remove("show")
      setTimeout(() => {
        document.body.removeChild(notification)
      }, 300)
    }, 3000)
  },

  updateCurrentView() {
    const currentView = state.currentView

    switch (currentView) {
      case "dashboard":
        ui.updateDashboardView(state.data)
        break
      case "routes":
        ui.updateRoutesView(state.data.routes)
        break
      case "filters":
        ui.updateFiltersView(state.data.globalFilters, state.data.routeFilters)
        break
      case "metrics":
        ui.updateMetricsView(state.data.metricsIndex)
        break
      case "health":
        ui.updateHealthView(state.data.health)
        break
      case "loggers":
        ui.updateLoggersView(state.data.loggers)
        break
      case "features":
        ui.updateFeaturesView(state.data.features)
        break
      case "threaddump":
        if (state.data.threadDump) {
          ui.updateThreadDumpView(state.data.threadDump)
        } else {
          // Load thread dump data on demand
          api
          .getThreadDump()
          .then((threadDump) => {
            state.data.threadDump = threadDump
            ui.updateThreadDumpView(threadDump)
          })
          .catch((error) => {
            console.error("Failed to load thread dump:", error)
          })
        }
        break
      case "env":
        ui.updateEnvView(state.data.env)
        break
      case "configprops":
        ui.updateConfigPropsView(state.data.configProps)
        break
      case "conditions":
        ui.updateConditionsView(state.data.conditions)
        break
      case "beans":
        ui.updateBeansView(state.data.beans)
        break
    }
  },

  updateDashboardView(data) {
    ui.updateKPIs(data)
    ui.updateCharts(data)
    ui.updateSystemInfo(data)
    ui.updateJVMInfo(data)
    ui.updateGatewayInfo(data)
    ui.updateRoutesTable(data.routes)
  },

  updateRoutesView(routes) {
    ui.updateRouteDefinitionsTable(state.data.routeDefinitions)
    ui.updateActiveRoutesTable(routes)
  },

  updateFiltersView(globalFilters, routeFilters) {
    const processedFilters = dataProcessor.processGlobalFilters(globalFilters)
    ui.updateGlobalFiltersTable(processedFilters)
    ui.updateRouteFiltersTable(routeFilters)
    ui.updateRoutePredicatesTable(state.data.routePredicates)
  },
}

// Initialize application when DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
  app.init()
})

// Handle page visibility changes
document.addEventListener("visibilitychange", () => {
  if (document.hidden) {
    app.stopAutoRefresh()
  } else if (!state.isPaused) {
    app.startAutoRefresh()
  }
})
