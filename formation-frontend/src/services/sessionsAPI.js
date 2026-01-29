import api from './api';

const sessionsAPI = {
  getAll: () => api.get('/sessions'),
  getById: (id) => api.get(`/sessions/${id}`),
  create: (sessionData) => api.post('/sessions', sessionData),
  update: (id, sessionData) => api.put(`/sessions/${id}`, sessionData),
  delete: (id) => api.delete(`/sessions/${id}`),
  getByStudent: (studentId) => api.get(`/sessions/student/${studentId}`),
  getByProf: (profId) => api.get(`/sessions/prof/${profId}`),
};

export default sessionsAPI;