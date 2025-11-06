import { Thermometer, Droplets, Wind, Eye, Cloud } from 'lucide-react'
import type { CurrentWeather, Location } from '../../types/weather'

interface WeatherCardProps {
  weather: CurrentWeather
  location: Location
}

export default function WeatherCard({ weather, location }: WeatherCardProps) {
  return (
    <div className="bg-gradient-to-br from-white to-blue-50 dark:from-gray-800 dark:to-gray-900 rounded-2xl shadow-xl p-6 transform transition-all duration-300 hover:scale-[1.02] hover:shadow-2xl border border-blue-100 dark:border-gray-700">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
            {location.city}, {location.country}
          </h2>
          <p className="text-gray-500 dark:text-gray-400 text-sm mt-1 flex items-center gap-1">
            <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            {new Date(weather.timestamp).toLocaleString()}
          </p>
        </div>
        {weather.icon && (
          <div className="relative">
            <div className="absolute inset-0 bg-blue-400 rounded-full blur-xl opacity-30 animate-pulse"></div>
            <img
              src={`https://openweathermap.org/img/wn/${weather.icon}@2x.png`}
              alt={weather.description}
              className="w-20 h-20 relative z-10 drop-shadow-2xl"
            />
          </div>
        )}
      </div>

      <div className="mb-8">
        <div className="flex items-baseline">
          <span className="text-6xl font-bold bg-gradient-to-br from-gray-900 to-gray-600 dark:from-white dark:to-gray-300 bg-clip-text text-transparent">
            {Math.round(weather.temperature)}°
          </span>
          <span className="text-3xl text-gray-500 dark:text-gray-400 ml-2">C</span>
        </div>
        <p className="text-xl text-gray-600 dark:text-gray-300 capitalize mt-2 font-medium">
          {weather.description}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <WeatherMetric
          icon={<Droplets className="w-5 h-5" />}
          label="Humidity"
          value={`${weather.humidity}%`}
        />
        <WeatherMetric
          icon={<Wind className="w-5 h-5" />}
          label="Wind Speed"
          value={`${weather.windSpeed} m/s`}
        />
        <WeatherMetric
          icon={<Thermometer className="w-5 h-5" />}
          label="Pressure"
          value={`${weather.pressure} hPa`}
        />
        <WeatherMetric
          icon={<Eye className="w-5 h-5" />}
          label="Visibility"
          value={weather.visibility != null ? `${(weather.visibility / 1000).toFixed(1)} km` : 'N/A'}
        />
        <WeatherMetric
          icon={<Cloud className="w-5 h-5" />}
          label="Cloudiness"
          value={`${weather.cloudiness}%`}
        />
      </div>
    </div>
  )
}

interface WeatherMetricProps {
  icon: React.ReactNode
  label: string
  value: string
}

function WeatherMetric({ icon, label, value }: WeatherMetricProps) {
  return (
    <div className="flex items-center space-x-3 bg-white/50 dark:bg-gray-700/50 rounded-xl p-3 backdrop-blur-sm transition-all duration-200 hover:bg-white/80 dark:hover:bg-gray-700/80 hover:shadow-md">
      <div className="text-blue-600 dark:text-blue-400 bg-blue-100 dark:bg-blue-900/30 p-2 rounded-lg">
        {icon}
      </div>
      <div>
        <p className="text-xs text-gray-500 dark:text-gray-400 uppercase tracking-wide">{label}</p>
        <p className="text-sm font-bold text-gray-800 dark:text-white">{value}</p>
      </div>
    </div>
  )
}
