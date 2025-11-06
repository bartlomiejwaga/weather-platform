import { AlertCircle } from 'lucide-react'
import type { AirQuality } from '../../types/weather'

interface AirQualityCardProps {
  airQuality: AirQuality
}

export default function AirQualityCard({ airQuality }: AirQualityCardProps) {
  const getAQIColor = (level: string) => {
    switch (level) {
      case 'GOOD':
        return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
      case 'MODERATE':
        return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
      case 'UNHEALTHY_SENSITIVE':
        return 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200'
      case 'UNHEALTHY':
        return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
      case 'VERY_UNHEALTHY':
        return 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200'
      case 'HAZARDOUS':
        return 'bg-red-900 text-white'
      default:
        return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    }
  }

  const getAQIBarWidth = (aqi: number) => {
    return Math.min((aqi / 500) * 100, 100)
  }

  return (
    <div className="bg-gradient-to-br from-white to-purple-50 dark:from-gray-800 dark:to-gray-900 rounded-2xl shadow-xl p-6 transform transition-all duration-300 hover:scale-[1.02] hover:shadow-2xl border border-purple-100 dark:border-gray-700">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
          Air Quality
        </h2>
        <div className="relative">
          <div className="absolute inset-0 bg-purple-400 rounded-full blur-md opacity-40 animate-pulse"></div>
          <AlertCircle className="w-8 h-8 text-purple-600 dark:text-purple-400 relative z-10" />
        </div>
      </div>

      <div className="mb-8">
        <div className="flex items-baseline mb-3">
          <span className="text-6xl font-bold bg-gradient-to-br from-gray-900 to-gray-600 dark:from-white dark:to-gray-300 bg-clip-text text-transparent">
            {airQuality.aqi}
          </span>
          <span className="text-2xl text-gray-500 dark:text-gray-400 ml-2">AQI</span>
        </div>
        <span
          className={`inline-block px-4 py-2 rounded-full text-sm font-bold shadow-lg transform transition-transform duration-200 hover:scale-105 ${getAQIColor(
            airQuality.level
          )}`}
        >
          {airQuality.levelDescription}
        </span>
      </div>

      <div className="mb-8">
        <div className="relative w-full bg-gray-200 dark:bg-gray-700 rounded-full h-4 overflow-hidden shadow-inner">
          <div
            className="h-full bg-gradient-to-r from-green-500 via-yellow-500 via-orange-500 to-red-500 transition-all duration-1000 ease-out relative"
            style={{ width: `${getAQIBarWidth(airQuality.aqi)}%` }}
          >
            <div className="absolute inset-0 bg-white/30 animate-pulse"></div>
          </div>
        </div>
        <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-2 font-medium">
          <span>0</span>
          <span>Good</span>
          <span>Moderate</span>
          <span>Unhealthy</span>
          <span>500</span>
        </div>
      </div>

      <div className="space-y-2">
        <h3 className="text-sm font-bold text-gray-700 dark:text-gray-300 mb-4 uppercase tracking-wider flex items-center gap-2">
          <span className="w-1 h-4 bg-gradient-to-b from-purple-500 to-pink-500 rounded-full"></span>
          Pollutant Levels
        </h3>
        <div className="grid grid-cols-2 gap-3">
          <Pollutant label="PM2.5" value={airQuality.pm25} unit="μg/m³" />
          <Pollutant label="PM10" value={airQuality.pm10} unit="μg/m³" />
          <Pollutant label="CO" value={airQuality.co} unit="μg/m³" />
          <Pollutant label="NO₂" value={airQuality.no2} unit="μg/m³" />
          <Pollutant label="SO₂" value={airQuality.so2} unit="μg/m³" />
          <Pollutant label="O₃" value={airQuality.o3} unit="μg/m³" />
        </div>
      </div>

      <div className="mt-6 pt-4 border-t border-gray-200 dark:border-gray-700">
        <p className="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
          <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
          Last updated: {new Date(airQuality.timestamp).toLocaleString()}
        </p>
      </div>
    </div>
  )
}

interface PollutantProps {
  label: string
  value: number | null
  unit: string
}

function Pollutant({ label, value, unit }: PollutantProps) {
  return (
    <div className="bg-white/50 dark:bg-gray-700/50 rounded-lg p-3 backdrop-blur-sm transition-all duration-200 hover:bg-white/80 dark:hover:bg-gray-700/80 hover:shadow-md">
      <span className="text-xs text-gray-500 dark:text-gray-400 uppercase tracking-wide block mb-1">{label}</span>
      <span className="text-sm font-bold text-gray-800 dark:text-white">
        {value != null ? value.toFixed(1) : 'N/A'} <span className="text-xs text-gray-500">{unit}</span>
      </span>
    </div>
  )
}
