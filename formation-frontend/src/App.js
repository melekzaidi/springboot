import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Box, CircularProgress } from '@mui/material';
import { checkAuth } from './store/slices/authSlice';
import Layout from './components/Layout/Layout';
import Login from './pages/Login/Login';
import Dashboard from './pages/Dashboard/Dashboard';
import Students from './pages/Students/Students';
import Professors from './pages/Professors/Professors';
import Courses from './pages/Courses/Courses';
import Niveaux from './pages/Niveaux/Niveaux';
import Sessions from './pages/Sessions/Sessions';
import Calendar from './pages/Calendar/Calendar';
import Grades from './pages/Grades/Grades';
import GradeForm from './pages/Grades/GradeForm';
import GradesSummary from './pages/Grades/GradesSummary';
import MyAssignments from './pages/Grades/MyAssignments';
import MyGradesSummary from './pages/Grades/MyGradesSummary';
import MyStudentGrades from './pages/Grades/MyStudentGrades';
import ProfessorNotes from './pages/ProfessorNotes/ProfessorNotes';
import StudentNotes from './pages/StudentNotes/StudentNotes';
import IntegratedNotes from './pages/IntegratedNotes/IntegratedNotes';
import ProtectedRoute from './components/ProtectedRoute/ProtectedRoute';
import AuthDebug from './components/AuthDebug/AuthDebug';

function App() {
  const dispatch = useDispatch();
  const { isAuthenticated, loading, initialized, user } = useSelector((state) => state.auth);

  useEffect(() => {
    // Only check auth once on app startup
    if (!initialized) {
      dispatch(checkAuth());
    }
  }, [dispatch, initialized]);

  // Show loading only during initial auth check
  if (!initialized && loading) {
    return (
      <Box 
        display="flex" 
        justifyContent="center" 
        alignItems="center" 
        minHeight="100vh"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <>
      <AuthDebug />
      <Routes>
        <Route 
          path="/login" 
          element={
            isAuthenticated ? 
            <Navigate to={user?.role === 'ADMIN' ? '/dashboard' : '/calendar'} replace /> : 
            <Login />
          } 
        />
        
        <Route 
          path="/*" 
          element={
            isAuthenticated ? (
              <Layout>
                <Routes>
                  <Route path="/" element={
                    user?.role === 'ADMIN' ? 
                    <Navigate to="/dashboard" replace /> : 
                    <Navigate to="/calendar" replace />
                  } />
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/calendar" element={<Calendar />} />
                  
                  {/* Admin only routes */}
                  <Route 
                    path="/students/*" 
                    element={
                      <ProtectedRoute requiredRole="ADMIN">
                        <Students />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/professors/*" 
                    element={
                      <ProtectedRoute requiredRole="ADMIN">
                        <Professors />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/courses/*" 
                    element={
                      <ProtectedRoute requiredRole="ADMIN">
                        <Courses />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/niveaux/*" 
                    element={
                      <ProtectedRoute requiredRole="ADMIN">
                        <Niveaux />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* Admin and Prof routes */}
                  <Route 
                    path="/sessions/*" 
                    element={
                      <ProtectedRoute requiredRoles={["ADMIN", "PROF"]}>
                        <Sessions />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades" 
                    element={
                      <ProtectedRoute requiredRoles={["ADMIN", "PROF"]}>
                        <Grades />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades/my-assignments" 
                    element={
                      <ProtectedRoute requiredRole="PROF">
                        <MyAssignments />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades/my-summary/:profCourseNiveauId" 
                    element={
                      <ProtectedRoute requiredRole="PROF">
                        <MyGradesSummary />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades/form" 
                    element={
                      <ProtectedRoute requiredRoles={["ADMIN", "PROF"]}>
                        <GradeForm />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades/edit/:id" 
                    element={
                      <ProtectedRoute requiredRoles={["ADMIN", "PROF"]}>
                        <GradeForm />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/grades/summary/:profCourseNiveauId" 
                    element={
                      <ProtectedRoute requiredRoles={["ADMIN", "PROF"]}>
                        <GradesSummary />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* Integrated Notes and Remarques */}
                  <Route 
                    path="/integrated-notes" 
                    element={
                      <ProtectedRoute requiredRoles={["PROF", "STUDENT"]}>
                        <IntegratedNotes />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* Professor Notes */}
                  <Route 
                    path="/professor-notes" 
                    element={
                      <ProtectedRoute requiredRole="PROF">
                        <ProfessorNotes />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* Student only routes */}
                  <Route 
                    path="/my-grades" 
                    element={
                      <ProtectedRoute requiredRole="STUDENT">
                        <MyStudentGrades />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/student-notes" 
                    element={
                      <ProtectedRoute requiredRole="STUDENT">
                        <StudentNotes />
                      </ProtectedRoute>
                    } 
                  />
                </Routes>
              </Layout>
            ) : (
              <Navigate to="/login" replace />
            )
          } 
        />
      </Routes>
    </>
  );
}

export default App;