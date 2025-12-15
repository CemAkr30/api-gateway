// Theme management
const ThemeManager = {
  init() {
    this.loadTheme()
    this.setupThemeToggle()
  },

  loadTheme() {
    const savedTheme = localStorage.getItem("gateway-theme") || "light"
    document.documentElement.className = savedTheme
    this.updateThemeIcon(savedTheme)
  },

  toggleTheme() {
    const currentTheme = document.documentElement.className || "light"
    const newTheme = currentTheme === "dark" ? "light" : "dark"

    document.documentElement.className = newTheme
    localStorage.setItem("gateway-theme", newTheme)
    this.updateThemeIcon(newTheme)
  },

  updateThemeIcon(theme) {
    const themeToggle = document.getElementById("themeToggle")
    if (themeToggle) {
      themeToggle.textContent = theme === "dark" ? "☀️" : "🌙"
    }
  },

  setupThemeToggle() {
    const themeToggle = document.getElementById("themeToggle")
    if (themeToggle) {
      themeToggle.addEventListener("click", () => {
        this.toggleTheme()
      })
    }
  },
}

// Auto-initialize theme when script loads
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => ThemeManager.init())
} else {
  ThemeManager.init()
}
