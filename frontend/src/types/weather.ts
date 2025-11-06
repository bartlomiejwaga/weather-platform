export interface Location {
  city: string
  country: string
  latitude: number
  longitude: number
}

export interface CurrentWeather {
  temperature: number
  temperatureUnit: string
  humidity: number
  pressure: number
  windSpeed: number
  windDirection: number
  condition: string
  description: string
  icon: string
  visibility: number | null
  cloudiness: number
  timestamp: string
}

export interface AirQuality {
  aqi: number
  level: string
  levelDescription: string
  pm25: number | null
  pm10: number | null
  co: number | null
  no2: number | null
  so2: number | null
  o3: number | null
  timestamp: string
}

export interface Metadata {
  dataSource: string
  fromCache: boolean
  retrievedAt: string
}

export interface WeatherResponse {
  location: Location
  weather: CurrentWeather
  airQuality: AirQuality
  metadata: Metadata
}
