import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { getWeather } from '../../services/weatherApi'
import WeatherCard from './WeatherCard'
import AirQualityCard from './AirQualityCard'

export default function Dashboard() {
  const [city, setCity] = useState('London')
  const [searchInput, setSearchInput] = useState('London')

  const { data, isLoading, error } = useQuery({
    queryKey: ['weather', city],
    queryFn: () => getWeather(city),
    enabled: !!city,
  })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (searchInput.trim()) {
      setCity(searchInput.trim())
    }
  }

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="bg-gradient-to-r from-white to-blue-50 dark:from-gray-800 dark:to-gray-900 rounded-2xl shadow-xl p-8 border border-blue-100 dark:border-gray-700">
        <h1 className="text-3xl font-bold bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent mb-6">
          Weather & Air Quality Dashboard
        </h1>
        <form onSubmit={handleSearch} className="flex gap-4">
          <div className="flex-1 relative group">
            <input
              type="text"
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              placeholder="Enter city name (e.g., London, Tokyo, New York)..."
              className="w-full px-6 py-4 border-2 border-gray-200 dark:border-gray-600 rounded-xl focus:ring-4 focus:ring-blue-500/50 focus:border-blue-500 dark:bg-gray-700 dark:text-white transition-all duration-300 outline-none text-lg group-hover:border-blue-300 dark:group-hover:border-blue-500"
            />
            <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-blue-500 to-purple-500 opacity-0 group-hover:opacity-10 transition-opacity duration-300 pointer-events-none"></div>
          </div>
          <button
            type="submit"
            className="px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl flex items-center gap-3 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-105 font-semibold"
          >
            <Search className="w-6 h-6" />
            Search
          </button>
        </form>
      </div>

      {isLoading && (
        <div className="text-center py-16 animate-fade-in">
          <div className="relative inline-block">
            <div className="animate-spin rounded-full h-16 w-16 border-4 border-blue-200 border-t-blue-600 mx-auto"></div>
            <div className="absolute inset-0 animate-ping rounded-full h-16 w-16 border-4 border-blue-400 opacity-20"></div>
          </div>
          <p className="mt-6 text-xl text-gray-600 dark:text-gray-400 font-medium">Loading weather data...</p>
        </div>
      )}

      {error && (
        <div className="bg-gradient-to-r from-red-50 to-pink-50 dark:from-red-900/20 dark:to-pink-900/20 border-2 border-red-200 dark:border-red-800 rounded-2xl p-6 shadow-lg animate-fade-in">
          <p className="text-red-800 dark:text-red-200 text-lg font-medium">
            ⚠️ Error loading weather data. Please try again.
          </p>
        </div>
      )}

      {data && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 animate-fade-in">
          <WeatherCard weather={data.weather} location={data.location} />
          <AirQualityCard airQuality={data.airQuality} />
        </div>
      )}

      <div className="relative overflow-hidden bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-700 rounded-2xl shadow-2xl p-8 text-white animate-fade-in">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32 blur-3xl"></div>
        <div className="absolute bottom-0 left-0 w-64 h-64 bg-purple-500/20 rounded-full -ml-32 -mb-32 blur-3xl"></div>
        <div className="relative z-10">
          <h3 className="text-2xl font-bold mb-3 flex items-center gap-2">
            <span className="text-3xl">🚀</span>
            About This Platform
          </h3>
          <p className="text-blue-100 text-lg leading-relaxed">
            Production-grade weather & air quality platform built with <span className="font-bold text-white">Spring Boot 3 (Java 21)</span> and <span className="font-bold text-white">React + TypeScript</span>. Features hexagonal architecture, multi-source data aggregation, intelligent caching, and resilience patterns.
          </p>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-3 text-center">
              <div className="text-2xl font-bold">Java 21</div>
              <div className="text-xs text-blue-200 mt-1">Backend</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-3 text-center">
              <div className="text-2xl font-bold">React 18</div>
              <div className="text-xs text-blue-200 mt-1">Frontend</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-3 text-center">
              <div className="text-2xl font-bold">Redis</div>
              <div className="text-xs text-blue-200 mt-1">Caching</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-3 text-center">
              <div className="text-2xl font-bold">Docker</div>
              <div className="text-xs text-blue-200 mt-1">Deploy</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
