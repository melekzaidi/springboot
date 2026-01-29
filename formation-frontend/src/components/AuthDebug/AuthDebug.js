import React from 'react';
import { useSelector } from 'react-redux';
import { Box, Typography, Paper } from '@mui/material';

const AuthDebug = () => {
  const auth = useSelector((state) => state.auth);

  // Only show in development
  if (process.env.NODE_ENV !== 'development') {
    return null;
  }

  return (
 <></>
  );
};

export default AuthDebug;