import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';
import { Box, Typography, Paper } from '@mui/material';

const ProtectedRoute = ({ children, requiredRole, requiredRoles }) => {
  const { isAuthenticated, user } = useSelector((state) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has required role(s)
  if (requiredRole && user?.role !== requiredRole) {
    return (
      <Box sx={{ p: 3 }}>
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography variant="h5" color="error" gutterBottom>
            Accès refusé
          </Typography>
          <Typography variant="body1">
            Vous n'avez pas les permissions nécessaires pour accéder à cette page.
          </Typography>
        </Paper>
      </Box>
    );
  }

  if (requiredRoles && !requiredRoles.includes(user?.role)) {
    return (
      <Box sx={{ p: 3 }}>
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography variant="h5" color="error" gutterBottom>
            Accès refusé
          </Typography>
          <Typography variant="body1">
            Vous n'avez pas les permissions nécessaires pour accéder à cette page.
          </Typography>
        </Paper>
      </Box>
    );
  }

  return children;
};

export default ProtectedRoute;