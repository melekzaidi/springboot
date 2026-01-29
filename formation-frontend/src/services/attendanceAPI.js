import api from './api';

const attendanceAPI = {
  getBySession: (sessionId) => api.get(`/attendances/session/${sessionId}`),
  markAttendance: (sessionId, attendanceData) => api.post(`/attendances/session/${sessionId}/mark`, attendanceData),
  markMultipleAttendance: (sessionId, attendanceData) => api.post(`/attendances/session/${sessionId}/mark-multiple`, attendanceData),
};

export default attendanceAPI;