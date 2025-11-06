import { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { Cloud, History, Bell } from 'lucide-react'

interface LayoutProps {
  children: ReactNode
}

export default function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900">
      <nav className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-md shadow-lg sticky top-0 z-50 border-b border-gray-200 dark:border-gray-700">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <Link to="/" className="flex items-center space-x-3 group">
              <div className="relative">
                <div className="absolute inset-0 bg-blue-400 rounded-full blur-md opacity-50 group-hover:opacity-75 transition-opacity"></div>
                <Cloud className="w-10 h-10 text-blue-600 dark:text-blue-400 relative z-10 transform group-hover:scale-110 transition-transform" />
              </div>
              <span className="text-2xl font-bold bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent">
                Weather Platform
              </span>
            </Link>
            <div className="flex space-x-2">
              <Link
                to="/"
                className="flex items-center space-x-2 px-4 py-2 rounded-xl text-gray-600 hover:text-blue-600 dark:text-gray-300 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-gray-700 transition-all duration-200 font-medium"
              >
                <Cloud className="w-5 h-5" />
                <span>Dashboard</span>
              </Link>
              <Link
                to="/history"
                className="flex items-center space-x-2 px-4 py-2 rounded-xl text-gray-600 hover:text-indigo-600 dark:text-gray-300 dark:hover:text-indigo-400 hover:bg-indigo-50 dark:hover:bg-gray-700 transition-all duration-200 font-medium"
              >
                <History className="w-5 h-5" />
                <span>History</span>
              </Link>
              <Link
                to="/subscriptions"
                className="flex items-center space-x-2 px-4 py-2 rounded-xl text-gray-600 hover:text-purple-600 dark:text-gray-300 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-gray-700 transition-all duration-200 font-medium"
              >
                <Bell className="w-5 h-5" />
                <span>Alerts</span>
              </Link>
            </div>
          </div>
        </div>
      </nav>
      <main className="container mx-auto px-6 py-10">{children}</main>
      <footer className="bg-white/50 dark:bg-gray-800/50 backdrop-blur-md border-t border-gray-200 dark:border-gray-700 mt-16">
        <div className="container mx-auto px-6 py-6 text-center text-gray-600 dark:text-gray-400 text-sm">
          <p>Built with Spring Boot 3 & React 18 | Hexagonal Architecture | Production Ready</p>
        </div>
      </footer>
    </div>
  )
}
