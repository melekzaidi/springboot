// API Configuration
export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081/api';

// Application Constants
export const APP_NAME = 'Formation Management';
export const APP_VERSION = '1.0.0';

// Roles
export const ROLES = {
  ADMIN: 'ADMIN',
  PROF: 'PROF',
  STUDENT: 'STUDENT',
};

// Routes
export const ROUTES = {
  LOGIN: '/login',
  DASHBOARD: '/',
  CALENDAR: '/calendar',
  STUDENTS: '/students',
  PROFESSORS: '/professors',
  COURSES: '/courses',
  SESSIONS: '/sessions',
};

// Test Accounts
export const TEST_ACCOUNTS = {
  ADMIN: {
    email: 'admin@formation.com',
    password: 'admin123',
    role: 'ADMIN',
  },
  PROF: {
    email: 'prof@formation.com',
    password: 'prof123',
    role: 'PROF',
  },
  STUDENT: {
    email: 'student@formation.com',
    password: 'student123',
    role: 'STUDENT',
  },
};