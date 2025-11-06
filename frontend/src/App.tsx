import { Routes, Route } from 'react-router-dom'
import Layout from './components/common/Layout'
import Dashboard from './components/dashboard/Dashboard'

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
      </Routes>
    </Layout>
  )
}

export default App
