import axios from 'axios'
import type { WeatherResponse } from '../types/weather'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export async function getWeather(city: string, country?: string): Promise<WeatherResponse> {
  const params = new URLSearchParams({ city })
  if (country) params.append('country', country)

  const response = await apiClient.get<WeatherResponse>(`/weather?${params}`)
  return response.data
}

export async function getForecast(city: string, days: number = 7, country?: string) {
  const params = new URLSearchParams({ city, days: days.toString() })
  if (country) params.append('country', country)

  const response = await apiClient.get(`/forecast?${params}`)
  return response.data
}

export async function getHistory(
  city: string,
  from: string,
  to: string,
  country?: string
) {
  const params = new URLSearchParams({ city, from, to })
  if (country) params.append('country', country)

  const response = await apiClient.get(`/history?${params}`)
  return response.data
}

export async function createSubscription(subscription: any) {
  const response = await apiClient.post('/subscriptions', subscription)
  return response.data
}

export async function getSubscriptions(userId: string) {
  const response = await apiClient.get(`/subscriptions?userId=${userId}`)
  return response.data
}

export async function deleteSubscription(id: number) {
  await apiClient.delete(`/subscriptions/${id}`)
}
