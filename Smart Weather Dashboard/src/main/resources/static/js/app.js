/**
 * Client-side JavaScript for Smart Weather Dashboard
 * Handles interactive features like search validation and enhancements
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get search input element
    const cityInput = document.getElementById('city');

    // Add input validation
    if (cityInput) {
        cityInput.addEventListener('input', function(e) {
            // Remove special characters (keep only letters, spaces, and hyphens)
            this.value = this.value.replace(/[^a-zA-Z\s\-']/g, '');
        });

        // Focus on input on page load if no weather is displayed
        const weatherCard = document.querySelector('.weather-card');
        if (!weatherCard) {
            cityInput.focus();
        }
    }

    // Add form submission validation
    const searchForm = document.querySelector('.search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const cityValue = cityInput.value.trim();

            if (!cityValue) {
                e.preventDefault();
                alert('Please enter a city name');
                cityInput.focus();
            }
        });
    }

    // Add click handlers for recent search tags
    const searchTags = document.querySelectorAll('.search-tag');
    searchTags.forEach(tag => {
        tag.addEventListener('click', function(e) {
            // The href is already set, so this just provides visual feedback
            console.log('Searching for city:', this.textContent);
        });
    });

    // Add keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + K to focus search input
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            if (cityInput) {
                cityInput.focus();
                cityInput.select();
            }
        }
    });

    // Add loading indicator for buttons
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.type === 'submit') {
                // Store original text
                const originalText = this.textContent;
                this.textContent = 'Loading...';

                // Do NOT disable the button - it needs to remain enabled for form submission
                // Re-enable after request completes or timeout
                const resetButton = () => {
                    this.textContent = originalText;
                };

                // Reset after 10 seconds max (reasonable timeout)
                setTimeout(resetButton, 10000);

                // Also try to detect when form completes (page reload)
                window.addEventListener('beforeunload', resetButton, { once: true });
            }
        });
    });

    // Smooth scroll behavior
    document.documentElement.style.scrollBehavior = 'smooth';
});

/**
 * Utility function to format temperature
 */
function formatTemperature(temp, unit) {
    return temp.toFixed(1) + '°' + unit;
}

/**
 * Utility function to get weather icon emoji
 */
function getWeatherEmoji(weatherMain) {
    const emojiMap = {
        'Clear': '☀️',
        'Clouds': '☁️',
        'Rain': '🌧️',
        'Drizzle': '🌦️',
        'Thunderstorm': '⛈️',
        'Snow': '❄️',
        'Mist': '🌫️',
        'Fog': '🌫️',
        'Wind': '💨'
    };

    return emojiMap[weatherMain] || '🌤️';
}

/**
 * Log message for debugging (only in development)
 */
function debugLog(message, data) {
    if (location.hostname === 'localhost' || location.hostname === '127.0.0.1') {
        console.log('[DEBUG] ' + message, data || '');
    }
}

