import api from './api';

const professorsAPI = {
  getAll: () => api.get('/profs'),
  getById: (id) => api.get(`/profs/${id}`),
  create: (professorData) => api.post('/profs', professorData),
  update: (id, professorData) => api.put(`/profs/${id}`, professorData),
  delete: (id) => api.delete(`/profs/${id}`),
};

export default professorsAPI;