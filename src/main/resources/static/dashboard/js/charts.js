// Advanced charting utilities
const ChartRenderer = {
  // Chart color schemes
  colorSchemes: {
    primary: ["#3b82f6", "#1d4ed8", "#1e40af"],
    success: ["#10b981", "#059669", "#047857"],
    warning: ["#f59e0b", "#d97706", "#b45309"],
    danger: ["#ef4444", "#dc2626", "#b91c1c"],
    purple: ["#8b5cf6", "#7c3aed", "#6d28d9"],
    gradient: {
      blue: ["#667eea", "#764ba2"],
      green: ["#11998e", "#38ef7d"],
      orange: ["#f093fb", "#f5576c"],
      purple: ["#667eea", "#764ba2"],
    },
  },

  // Create responsive canvas
  createCanvas(canvas, options = {}) {
    const ctx = canvas.getContext("2d")
    const dpr = window.devicePixelRatio || 1
    const rect = canvas.getBoundingClientRect()

    canvas.width = rect.width * dpr
    canvas.height = rect.height * dpr
    ctx.scale(dpr, dpr)

    // Set default styles
    ctx.lineCap = "round"
    ctx.lineJoin = "round"

    return {
      ctx,
      width: rect.width,
      height: rect.height,
      dpr,
    }
  },

  // Draw grid background
  drawGrid(ctx, width, height, options = {}) {
    const {
      color = "#e5e7eb",
      lineWidth = 1,
      horizontalLines = 5,
      verticalLines = 0
    } = options

    ctx.save()
    ctx.strokeStyle = color
    ctx.lineWidth = lineWidth
    ctx.globalAlpha = 0.5

    ctx.beginPath()

    // Horizontal lines
    for (let i = 0; i <= horizontalLines; i++) {
      const y = (height / horizontalLines) * i
      ctx.moveTo(0, y)
      ctx.lineTo(width, y)
    }

    // Vertical lines
    if (verticalLines > 0) {
      for (let i = 0; i <= verticalLines; i++) {
        const x = (width / verticalLines) * i
        ctx.moveTo(x, 0)
        ctx.lineTo(x, height)
      }
    }

    ctx.stroke()
    ctx.restore()
  },

  // Draw line chart
  drawLineChart(canvas, data, options = {}) {
    const {ctx, width, height} = this.createCanvas(canvas)
    const {
      color = this.colorSchemes.primary[0],
      lineWidth = 2,
      fill = false,
      fillAlpha = 0.1,
      showPoints = false,
      pointRadius = 3,
      showGrid = true,
      padding = {top: 20, right: 20, bottom: 20, left: 20},
    } = options

    ctx.clearRect(0, 0, width, height)

    const chartWidth = width - padding.left - padding.right
    const chartHeight = height - padding.top - padding.bottom

    // Draw grid
    if (showGrid) {
      ctx.save()
      ctx.translate(padding.left, padding.top)
      this.drawGrid(ctx, chartWidth, chartHeight)
      ctx.restore()
    }

    if (!data || data.length === 0) {
      return
    }

    // Calculate bounds
    const max = Math.max(...data)
    const min = Math.min(...data)
    const range = max - min || 1

    // Draw line
    ctx.save()
    ctx.translate(padding.left, padding.top)
    ctx.strokeStyle = color
    ctx.lineWidth = lineWidth

    ctx.beginPath()
    data.forEach((value, index) => {
      const x = (chartWidth / Math.max(data.length - 1, 1)) * index
      const y = chartHeight - ((value - min) / range) * chartHeight

      if (index === 0) {
        ctx.moveTo(x, y)
      } else {
        ctx.lineTo(x, y)
      }
    })
    ctx.stroke()

    // Fill area
    if (fill) {
      ctx.globalAlpha = fillAlpha
      ctx.fillStyle = color
      ctx.lineTo(chartWidth, chartHeight)
      ctx.lineTo(0, chartHeight)
      ctx.closePath()
      ctx.fill()
      ctx.globalAlpha = 1
    }

    // Draw points
    if (showPoints) {
      ctx.fillStyle = color
      data.forEach((value, index) => {
        const x = (chartWidth / Math.max(data.length - 1, 1)) * index
        const y = chartHeight - ((value - min) / range) * chartHeight

        ctx.beginPath()
        ctx.arc(x, y, pointRadius, 0, Math.PI * 2)
        ctx.fill()
      })
    }

    ctx.restore()
  },

  // Draw multi-line chart
  drawMultiLineChart(canvas, datasets, options = {}) {
    const {ctx, width, height} = this.createCanvas(canvas)
    const {
      showGrid = true,
      showLegend = false,
      padding = {top: 20, right: 20, bottom: 20, left: 20}
    } = options

    ctx.clearRect(0, 0, width, height)

    const chartWidth = width - padding.left - padding.right
    const chartHeight = height - padding.top - padding.bottom

    // Draw grid
    if (showGrid) {
      ctx.save()
      ctx.translate(padding.left, padding.top)
      this.drawGrid(ctx, chartWidth, chartHeight)
      ctx.restore()
    }

    if (!datasets || datasets.length === 0) {
      return
    }

    // Calculate global bounds
    const allValues = datasets.flatMap((d) => d.data || [])
    if (allValues.length === 0) {
      return
    }

    const max = Math.max(...allValues)
    const min = Math.min(...allValues)
    const range = max - min || 1

    // Draw each dataset
    datasets.forEach((dataset, datasetIndex) => {
      if (!dataset.data || dataset.data.length === 0) {
        return
      }

      const color = dataset.color || this.colorSchemes.primary[datasetIndex
      % this.colorSchemes.primary.length]

      this.drawLineChart(canvas, dataset.data, {
        color,
        lineWidth: dataset.lineWidth || 2,
        fill: dataset.fill || false,
        showPoints: dataset.showPoints || false,
        showGrid: false, // Already drawn
        padding,
      })
    })
  },

  // Draw bar chart
  drawBarChart(canvas, data, options = {}) {
    const {ctx, width, height} = this.createCanvas(canvas)
    const {
      color = this.colorSchemes.primary[0],
      showGrid = true,
      padding = {top: 20, right: 20, bottom: 40, left: 40},
      labels = [],
    } = options

    ctx.clearRect(0, 0, width, height)

    const chartWidth = width - padding.left - padding.right
    const chartHeight = height - padding.top - padding.bottom

    // Draw grid
    if (showGrid) {
      ctx.save()
      ctx.translate(padding.left, padding.top)
      this.drawGrid(ctx, chartWidth, chartHeight)
      ctx.restore()
    }

    if (!data || data.length === 0) {
      return
    }

    const max = Math.max(...data)
    const barWidth = (chartWidth / data.length) * 0.8
    const barSpacing = (chartWidth / data.length) * 0.2

    ctx.save()
    ctx.translate(padding.left, padding.top)
    ctx.fillStyle = color

    data.forEach((value, index) => {
      const barHeight = (value / max) * chartHeight
      const x = index * (barWidth + barSpacing) + barSpacing / 2
      const y = chartHeight - barHeight

      ctx.fillRect(x, y, barWidth, barHeight)
    })

    ctx.restore()
  },

  // Draw donut chart
  drawDonutChart(canvas, data, options = {}) {
    const {ctx, width, height} = this.createCanvas(canvas)
    const {
      colors = this.colorSchemes.primary,
      innerRadius = 0.5,
      showLabels = true,
      labelOffset = 20
    } = options

    ctx.clearRect(0, 0, width, height)

    const centerX = width / 2
    const centerY = height / 2
    const radius = Math.min(width, height) / 2 - 20
    const innerR = radius * innerRadius

    if (!data || data.length === 0) {
      return
    }

    const total = data.reduce((sum, item) => sum + item.value, 0)
    let currentAngle = -Math.PI / 2

    data.forEach((item, index) => {
      const sliceAngle = (item.value / total) * Math.PI * 2
      const color = colors[index % colors.length]

      // Draw slice
      ctx.beginPath()
      ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
      ctx.arc(centerX, centerY, innerR, currentAngle + sliceAngle, currentAngle,
          true)
      ctx.closePath()
      ctx.fillStyle = color
      ctx.fill()

      // Draw label
      if (showLabels && item.label) {
        const labelAngle = currentAngle + sliceAngle / 2
        const labelX = centerX + Math.cos(labelAngle) * (radius + labelOffset)
        const labelY = centerY + Math.sin(labelAngle) * (radius + labelOffset)

        ctx.fillStyle = "#374151"
        ctx.font = "12px Inter, sans-serif"
        ctx.textAlign = "center"
        ctx.fillText(item.label, labelX, labelY)
      }

      currentAngle += sliceAngle
    })
  },

  // Draw gauge chart
  drawGaugeChart(canvas, value, options = {}) {
    const {ctx, width, height} = this.createCanvas(canvas)
    const {
      min = 0,
      max = 100,
      color = this.colorSchemes.primary[0],
      backgroundColor = "#e5e7eb",
      lineWidth = 20,
      showValue = true,
      unit = "%",
    } = options

    ctx.clearRect(0, 0, width, height)

    const centerX = width / 2
    const centerY = height / 2
    const radius = Math.min(width, height) / 2 - lineWidth

    const startAngle = Math.PI
    const endAngle = 2 * Math.PI
    const valueAngle = startAngle + ((value - min) / (max - min)) * (endAngle
        - startAngle)

    // Draw background arc
    ctx.beginPath()
    ctx.arc(centerX, centerY, radius, startAngle, endAngle)
    ctx.strokeStyle = backgroundColor
    ctx.lineWidth = lineWidth
    ctx.stroke()

    // Draw value arc
    ctx.beginPath()
    ctx.arc(centerX, centerY, radius, startAngle, valueAngle)
    ctx.strokeStyle = color
    ctx.lineWidth = lineWidth
    ctx.stroke()

    // Draw value text
    if (showValue) {
      ctx.fillStyle = "#374151"
      ctx.font = "bold 24px Inter, sans-serif"
      ctx.textAlign = "center"
      ctx.fillText(`${value}${unit}`, centerX, centerY + 8)
    }
  },

  // Animate chart
  animateChart(canvas, drawFunction, duration = 1000) {
    const startTime = performance.now()

    const animate = (currentTime) => {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)

      // Easing function
      const easeProgress = 1 - Math.pow(1 - progress, 3)

      drawFunction(easeProgress)

      if (progress < 1) {
        requestAnimationFrame(animate)
      }
    }

    requestAnimationFrame(animate)
  },
}

// Export for use in other modules
if (typeof module !== "undefined" && module.exports) {
  module.exports = ChartRenderer
}
